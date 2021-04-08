package divineadditions.item;

import divineadditions.DivineAdditions;
import divineadditions.api.IArmorEssence;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.entity.EntityArmorDefender;
import divineadditions.entity.EntityDefenderStand;
import divineadditions.holders.Items;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.ArmorEquippedEvent;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemDefenderStand extends Item {
    private final boolean activation;
    private final String guidName = "GUID";

    public ItemDefenderStand(boolean activation) {
        this.activation = activation;
        setMaxStackSize(1);
    }

    /**
     * Placing defender here (if not in activation mode)
     *
     * @param player  - current player using item
     * @param worldIn - player world
     * @param pos     - current pos
     * @param hand    - with current hand
     * @param facing  - player facing
     * @param hitX    - hit vector (<1)
     * @param hitY    - hit vector (<1)
     * @param hitZ    - hit vector (<1)
     * @return
     */
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!activation) {
            if (facing == EnumFacing.DOWN) {
                return EnumActionResult.FAIL;
            } else {
                boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
                BlockPos blockpos = flag ? pos : pos.offset(facing);
                ItemStack itemstack = player.getHeldItem(hand);

                if (!player.canPlayerEdit(blockpos, facing, itemstack)) {
                    return EnumActionResult.FAIL;
                } else {
                    BlockPos blockpos1 = blockpos.up();
                    boolean flag1 = !worldIn.isAirBlock(blockpos) && !worldIn.getBlockState(blockpos).getBlock().isReplaceable(worldIn, blockpos);
                    flag1 = flag1 | (!worldIn.isAirBlock(blockpos1) && !worldIn.getBlockState(blockpos1).getBlock().isReplaceable(worldIn, blockpos1));

                    if (flag1) {
                        return EnumActionResult.FAIL;
                    } else {
                        double d0 = blockpos.getX();
                        double d1 = blockpos.getY();
                        double d2 = blockpos.getZ();
                        List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                        if (!list.isEmpty()) {
                            return EnumActionResult.FAIL;
                        } else {
                            if (!worldIn.isRemote) {
                                worldIn.setBlockToAir(blockpos);
                                worldIn.setBlockToAir(blockpos1);
                                Entity armorStand = new EntityDefenderStand(worldIn, player, new BlockPos(d0 + 0.5D, d1, d2 + 0.5D));
                                worldIn.spawnEntity(armorStand);
                                worldIn.playSound(null, armorStand.posX, armorStand.posY, armorStand.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);

                                ItemStack activatorStack = new ItemStack(Items.defender_stand_activation);
                                NBTTagCompound compound = new NBTTagCompound();
                                compound.setUniqueId(guidName, armorStand.getUniqueID());
                                activatorStack.setTagCompound(compound);
                                player.setHeldItem(hand, activatorStack);
                            }
                            return EnumActionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    /**
     * Summon defender instead of stand. Only in activation mode!
     *
     * @param stack  - activation module stack
     * @param player - current player
     * @param target - target (defender stand)
     * @param hand   - current hand
     * @return
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {

        if (activation && target instanceof EntityDefenderStand) {
            ITextComponent text = null;

            EntityDefenderStand defenderStand = (EntityDefenderStand) target;
            World worldIn = defenderStand.getEntityWorld();
            BlockPos pos = defenderStand.getPosition();

            NBTTagCompound nbt = stack.getTagCompound();

            if (nbt != null && Objects.equals(nbt.getUniqueId(guidName), defenderStand.getUniqueID())) {
                // collecting armor set from defender module
                Map<EntityEquipmentSlot, ItemStack> items = Arrays.stream(EntityEquipmentSlot.values()).collect(Collectors.toMap(x -> x, defenderStand::getItemStackFromSlot));
                ItemStack weapon = items.get(EntityEquipmentSlot.MAINHAND);

                // check for weapon here
                if (weapon.getItem() instanceof ItemSword || weapon.getItem() instanceof ItemBow) {

                    // posting event to detect wearing super sets
                    ArmorEquippedEvent equippedEvent = new ArmorEquippedEvent(items);
                    MinecraftForge.EVENT_BUS.post(equippedEvent);

                    Set<ResourceLocation> confirmed = equippedEvent.getConfirmed();

                    // set must contain only one ability
                    if (confirmed.size() == 1) {
                        ResourceLocation id = confirmed.stream().findFirst().orElse(null);
                        // find armor description from ID
                        IArmorDescription armorDescription = DivineAPI.getArmorDescriptionRegistry().getValue(id);
                        if (armorDescription != null) {
                            ItemStack essence = new ItemStack(Items.armor_essence);
                            if (essence.getItem() instanceof IArmorEssence) {
                                // creating armor essence from current set
                                ((IArmorEssence) essence.getItem()).absorb(essence, items, armorDescription);
                                // removing defender stand because Armor Defender will spawn
                                defenderStand.setDead();
                                // spawn some effects
                                spawnEffects(worldIn, pos);
                                // removing activation module
                                stack.shrink(1);

                                IKnowledgeInfo capability = player.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
                                if (capability != null) {
                                    // increasing summon Armor Defender stats
                                    capability.setArmorDefenderSummonCount(capability.armorDefenderSummonCount() + 1);
                                    // send updates to client
                                    capability.update(player);

                                    Random rand = worldIn.rand;

                                    EntityArmorDefender defender = new EntityArmorDefender(worldIn, items, player, essence);

                                    BlockPos defenderPos = pos.add(
                                            rand.nextInt(4) - rand.nextInt(4),
                                            rand.nextInt(4),
                                            rand.nextInt(4) - rand.nextInt(4)
                                    );

                                    defender.setPosition(
                                            defenderPos.getX(),
                                            worldIn.getHeight(defenderPos.getX(), defenderPos.getZ()),
                                            defenderPos.getZ()
                                    );

                                    if (!worldIn.isRemote) {
                                        worldIn.spawnEntity(defender);
                                    }

                                    return true;
                                } else {
                                    DivineAdditions.logger.warn("IKnowledgeInfo capability was not attached");
                                }
                            } else {
                                DivineAdditions.logger.warn("Items.armor_essence is not derived from IArmorEssence");
                            }
                        } else {
                            DivineAdditions.logger.warn("Cannot locate armor description of " + id.toString());
                        }

                        text = new TextComponentString("Error during summoning entity");
                    } else {
                        text = confirmed.size() == 0
                                ? new TextComponentTranslation("divineadditions.armor_is_not_powered")
                                : new TextComponentTranslation("divineadditions.armor_multiple_power");
                    }
                } else {
                    text = new TextComponentTranslation("divineadditions.weapon_is_needed");
                }
            } else {
                text = new TextComponentTranslation("divineadditions.incorrect_activator");
            }

            if (text != null && !worldIn.isRemote) {
                player.sendMessage(text);
            }
        }

        return target instanceof EntityDefenderStand;
    }

    private void spawnEffects(World world, BlockPos pos) {
        Random rand = world.rand;

        if (world.isRemote) {

            for (int i = 0; i < 10; i++) {
                BlockPos currentPos = pos.add(rand.nextInt(7) - 7, rand.nextInt(3), rand.nextInt(7) - 7);
                world.spawnParticle(
                        EnumParticleTypes.CLOUD,
                        currentPos.getX(),
                        currentPos.getY(),
                        currentPos.getZ(),
                        rand.nextFloat() - rand.nextFloat(),
                        rand.nextFloat(),
                        rand.nextFloat() - rand.nextFloat()
                );
            }

        } else {
            for (int i = 0; i < 4; i++) {
                EntityLightningBolt lightningBolt = new EntityLightningBolt(world,
                        pos.getX() + rand.nextInt(4) - rand.nextInt(4),
                        pos.getY(),
                        pos.getZ() + rand.nextInt(4) - rand.nextInt(4),
                        true);

                world.addWeatherEffect(lightningBolt);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (activation) {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.defender_stand_activation").getFormattedText());

            if (net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt != null && nbt.hasKey(guidName)) {
                    tooltip.add("Binded to " + nbt.getUniqueId(guidName));
                }
            }
        } else {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.defender_stand").getFormattedText());
        }
    }
}
