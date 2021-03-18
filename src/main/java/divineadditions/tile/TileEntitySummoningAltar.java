package divineadditions.tile;

import divineadditions.api.IArmorEssence;
import divineadditions.api.IPedestal;
import divineadditions.holders.Items;
import divineadditions.utils.InventoryHelper;
import divinerpg.DivineRPG;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.ArmorEquippedEvent;
import divinerpg.api.armor.registry.IArmorDescription;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
    public TileEntitySummoningAltar() {

    }

    public boolean trySummon(World world, EntityLivingBase player) {
        if (world == null || player == null)
            return false;

        if (!acceptItem(player))
            return false;

        List<IItemHandler> pedestals = findPedestals(3);
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
            items.put(EntityLiving.getSlotForItemStack(stack), stack);
        }

        ArmorEquippedEvent equippedEvent = new ArmorEquippedEvent(items);
        MinecraftForge.EVENT_BUS.post(equippedEvent);

        Set<ResourceLocation> armorSets = equippedEvent.getConfirmed();
        if (armorSets.size() == 0) {
            if (!world.isRemote)
                player.sendMessage(new TextComponentTranslation("divineadditions.armor_is_not_powered"));
            return false;
        }

        if (armorSets.size() > 1) {
            if (!world.isRemote)
                player.sendMessage(new TextComponentTranslation("divineadditions.armor_is_not_powered"));
            return false;
        }

        if (!stacks.stream().anyMatch(x -> x.getItem() instanceof ItemSword)) {
            if (!world.isRemote)
                player.sendMessage(new TextComponentTranslation("divineadditions.sword_is_needed"));
            return false;
        }

        if (summonEntity(items, player, armorSets.stream().findFirst().orElse(null))) {
        }

        pedestals.forEach(InventoryHelper::clear);
        player.getHeldItemMainhand().shrink(1);

        return true;
    }

    private List<IItemHandler> findPedestals(int radius) {
        return StreamSupport.stream(BlockPos.getAllInBox(pos.add(-radius, 0, -radius), pos.add(radius, 0, radius)).spliterator(), false)
                .map(world::getTileEntity)
                .filter(x -> x instanceof IPedestal)
                .map(x -> ((IPedestal) x).getHandler())
                .collect(Collectors.toList());
    }

    private boolean summonEntity(Map<EntityEquipmentSlot, ItemStack> items, EntityLivingBase summoner, ResourceLocation location) {
        if (items == null || items.isEmpty() || summoner == null || location == null)
            return false;

        IArmorDescription iArmorDescription = DivineAPI.getArmorDescriptionRegistry().getValue(location);
        if (iArmorDescription == null)
            return false;

        if (summoner.getEntityWorld().isRemote)
            return false;

        EntityLightningBolt lightningBolt = new EntityLightningBolt(world, getPos().getX(), getPos().getY(), getPos().getZ(), true);
        world.addWeatherEffect(lightningBolt);

        Random random = getWorld().rand;
        BlockPos pos = getPos()
                .add(
                        random.nextInt(6) - 3,
                        random.nextInt(2),
                        random.nextInt(6) - 3);

        EntityZombie entityZombie = new EntityZombie(world);
        entityZombie.setPosition(pos.getX(), pos.getY(), pos.getZ());
        items.forEach((slot, stack) -> {
            entityZombie.setItemStackToSlot(slot, stack.copy());
            entityZombie.setDropChance(slot, 0);
        });

        entityZombie.setAttackTarget(summoner);

        ItemStack essence = Items.armor_essence.getDefaultInstance();
        if (essence.getItem() instanceof IArmorEssence) {
            if (((IArmorEssence) essence.getItem()).absorb(essence, items, iArmorDescription)) {
                entityZombie.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, essence);
                entityZombie.setDropChance(EntityEquipmentSlot.OFFHAND, 1);

                return world.spawnEntity(entityZombie);
            }
        }

        return false;
    }

    private boolean acceptItem(EntityLivingBase player) {
        ResourceLocation name = player.getHeldItemMainhand().getItem().getRegistryName();

        if (DivineRPG.MODID.equals(name.getResourceDomain())) {
            return name.getResourcePath().toLowerCase().contains("heart");
        }

        return false;
    }
}
