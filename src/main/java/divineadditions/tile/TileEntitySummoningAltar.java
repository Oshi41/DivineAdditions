package divineadditions.tile;

import divineadditions.DivineAdditions;
import divineadditions.api.IArmorEssence;
import divineadditions.api.IPedestal;
import divineadditions.entity.EntityArmorDefender;
import divineadditions.holders.Items;
import divineadditions.item.ItemArmorEssence;
import divineadditions.utils.InventoryHelper;
import divineadditions.utils.NbtUtils;
import divinerpg.DivineRPG;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.ArmorEquippedEvent;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;
import openmods.tileentity.SimpleNetTileEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TileEntitySummoningAltar extends SimpleNetTileEntity {
    private static final String summonTriesName = "SummonCount";
    private int radius;

    public TileEntitySummoningAltar() {

    }

    public TileEntitySummoningAltar(int radius) {
        this.radius = radius;
    }

    public boolean trySummon(World world, EntityPlayer player) {
        if (world == null || player == null)
            return false;

        if (!acceptItem(player))
            return false;

        List<IItemHandler> pedestals = findPedestals(radius);
        int length = EntityEquipmentSlot.values().length;
        if (pedestals.size() != length) {
            if (!world.isRemote)
                player.sendMessage(new TextComponentTranslation("divineadditions.wrong_pedestal_count", length));
            return false;
        }

        Map<EntityEquipmentSlot, ItemStack> items = Arrays.stream(EntityEquipmentSlot.values())
                .collect(Collectors.toMap(x -> x, x -> ItemStack.EMPTY));

        List<ItemStack> stacks = pedestals.stream()
                .map(x -> x.getStackInSlot(0))
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());

        for (ItemStack stack : stacks) {
            items.put(EntityLiving.getSlotForItemStack(stack), stack.copy());
        }

        IArmorDescription armorDescription = detectDescription(items, player);
        if (armorDescription == null) {
            DivineAdditions.logger.warn("Can't find armor desciption");
            return false;
        }

        ItemStack essence = createEssence(items, armorDescription);
        if (essence.isEmpty()) {
            DivineAdditions.logger.warn("Can't create armor essence");
            return false;
        }

        pedestals.forEach(InventoryHelper::clear);
        player.getHeldItemMainhand().shrink(1);

        spawnEffects(world, getPos());

        if (!world.isRemote && !summonEntity(items, player, essence)) {
            DivineAdditions.logger.warn("Can't summon boss for armor essence");
        }

        return true;
    }

    /**
     * Checks if holding items is acceptable
     *
     * @param player
     * @return
     */
    private boolean acceptItem(EntityLivingBase player) {
        ResourceLocation name = player.getHeldItemMainhand().getItem().getRegistryName();

        if (DivineRPG.MODID.equals(name.getResourceDomain())) {
            return name.getResourcePath().toLowerCase().endsWith("heart");
        }

        return false;
    }

    /**
     * Searching pedestals (tiles implementing IPedestal interface) in radius on the same height
     *
     * @param radius
     * @return
     */
    private List<IItemHandler> findPedestals(int radius) {
        return StreamSupport.stream(BlockPos.getAllInBox(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius)).spliterator(), false)
                .map(world::getTileEntity)
                .filter(x -> x instanceof IPedestal)
                .map(x -> ((IPedestal) x).getHandler())
                .collect(Collectors.toList());
    }

    private IArmorDescription detectDescription(Map<EntityEquipmentSlot, ItemStack> items, ICommandSender sender) {
        ArmorEquippedEvent equippedEvent = new ArmorEquippedEvent(items);
        MinecraftForge.EVENT_BUS.post(equippedEvent);

        Set<ResourceLocation> armorSets = equippedEvent.getConfirmed();
        if (armorSets.size() == 0) {
            if (!world.isRemote)
                sender.sendMessage(new TextComponentTranslation("divineadditions.armor_is_not_powered"));
            return null;
        }

        if (armorSets.size() > 1) {
            if (!world.isRemote)
                sender.sendMessage(new TextComponentTranslation("divineadditions.armor_multiple_power"));
            return null;
        }

        if (!checkAttackEqupment(items, sender)) {
            return null;
        }

        ResourceLocation id = armorSets.stream().findFirst().orElse(null);
        IArmorDescription armorDescription = DivineAPI.getArmorDescriptionRegistry().getValue(id);
        return armorDescription;
    }

    private boolean checkAttackEqupment(Map<EntityEquipmentSlot, ItemStack> items, ICommandSender sender) {
        Item item = items.get(EntityEquipmentSlot.MAINHAND).getItem();

        if (item instanceof ItemSword || item instanceof ItemBow) {
            return true;
        }

        sender.sendMessage(new TextComponentTranslation("divineadditions.weapon_is_needed"));
        return false;
    }

    /**
     * Creating armor essence
     *
     * @param items       - absorbing items
     * @param description - armor desciption
     * @return
     */
    private ItemStack createEssence(Map<EntityEquipmentSlot, ItemStack> items, IArmorDescription description) {
        if (items != null && !items.isEmpty() && description != null && Items.armor_essence instanceof IArmorEssence) {
            ItemStack essence = Items.armor_essence.getDefaultInstance();
            if (((ItemArmorEssence) essence.getItem()).absorb(essence, items, description)) {
                return essence;
            }
        }

        return ItemStack.EMPTY;
    }

    private void spawnEffects(World world, BlockPos pos) {
        if (world.isRemote) {
            Random rand = world.rand;

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
            EntityLightningBolt lightningBolt = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true);
            world.addWeatherEffect(lightningBolt);
        }
    }

    private boolean summonEntity(Map<EntityEquipmentSlot, ItemStack> items, EntityPlayer summoner, ItemStack essence) {
        if (items == null || items.isEmpty() || summoner == null || essence == null || essence.isEmpty())
            return false;

        NBTTagCompound tag = NbtUtils.getOrCreateModPlayerPersistTag(summoner, DivineAdditions.MOD_NAME);
        int tries = tag.getInteger(summonTriesName);


        EntityArmorDefender armorDefender = new EntityArmorDefender(world, summoner, items, essence);
        armorDefender.increaseStats(1 + tries / 5.);

        Random rand = getWorld().rand;

        BlockPos blockPos = getPos().add(
                rand.nextInt(7) - 7,
                rand.nextInt(3),
                rand.nextInt(7) - 7
        );

        armorDefender.setPosition(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        world.spawnEntity(armorDefender);

        tag.setInteger(summonTriesName, tries + 1);
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        nbt.setInteger("Radius", radius);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        radius = compound.getInteger("Radius");
    }
}
