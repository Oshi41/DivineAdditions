package divineadditions.item;

import divineadditions.entity.EntityDefenderStand;
import divineadditions.holders.Items;
import divinerpg.api.armor.ArmorEquippedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemDefenderStand extends Item {
    private final boolean activation;

    public ItemDefenderStand(boolean activation) {
        this.activation = activation;
        setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return activation
                ? placeDefender(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
                : activateDefender(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    private EnumActionResult activateDefender(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        EntityDefenderStand defenderStand = worldIn.getEntitiesWithinAABB(EntityDefenderStand.class, axisAlignedBB).stream().findFirst().orElse(null);
        if (defenderStand != null) {
            Map<EntityEquipmentSlot, ItemStack> items = Arrays.stream(EntityEquipmentSlot.values()).collect(Collectors.toMap(x -> x, defenderStand::getItemStackFromSlot));
            ItemStack weapon = items.get(EntityEquipmentSlot.MAINHAND);

            if (weapon.getItem() instanceof ItemSword || weapon.getItem() instanceof ItemBow) {
                ArmorEquippedEvent equippedEvent = new ArmorEquippedEvent(items);
                MinecraftForge.EVENT_BUS.post(equippedEvent);

                Set<ResourceLocation> confirmed = equippedEvent.getConfirmed();
                if (confirmed.size() == 1) {

                }
            }
        }


        return EnumActionResult.FAIL;
    }

    /**
     * Placing defender here
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
    private EnumActionResult placeDefender(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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
                        }

                        player.setHeldItem(hand, new ItemStack(Items.defender_stand_activation));
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
    }
}
