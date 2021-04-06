package divineadditions.event;

import divineadditions.DivineAdditions;
import divineadditions.api.IEntityCage;
import divineadditions.holders.Items;
import divineadditions.item.sword.ItemCustomSword;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = DivineAdditions.MOD_ID)
public class LivingDropsEventHandler {
    @SubscribeEvent
    public static void onEntityKilled(LivingDropsEvent entityEvent) {
        if (entityEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            ItemStack handItem = ((EntityLivingBase) entityEvent.getSource().getTrueSource()).getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if (!handItem.isEmpty() && handItem.getItem() instanceof ItemCustomSword) {
                EntityItem entityItem = handleSoulDrop(entityEvent.getEntity(), ((ItemCustomSword) handItem.getItem()), handItem, entityEvent.getLootingLevel());
                if (entityItem != null) {
                    entityEvent.getDrops().add(entityItem);
                }
            }
        }
    }

    private static EntityItem handleSoulDrop(Entity victim, ItemCustomSword item, ItemStack stack, int lootingLevel) {
        int soulPerKills = item.getSwordProps().getSoulPerKills(stack);
        if (soulPerKills < 0)
            return null;

        Random rand = victim.getEntityWorld().rand;

        rand.setSeed(rand.nextLong());

        soulPerKills = getSoulDropChance(soulPerKills, lootingLevel, stack);
        if (rand.nextInt(soulPerKills) != 0)
            return null;

        ItemStack drop = Items.caged_mob.getDefaultInstance();
        drop.setTagCompound(new NBTTagCompound());
        ((IEntityCage) drop.getItem()).imprison(victim, drop.getTagCompound());

        if (victim instanceof EntityLivingBase) {
            drop.getOrCreateSubCompound(IEntityCage.cagedTagName).setFloat("Health", rand.nextFloat() * ((EntityLivingBase) victim).getMaxHealth());
        }


        return new EntityItem(victim.getEntityWorld(), victim.posX, victim.posY, victim.posZ, drop);
    }

    /**
     * Returns soul drop chance including looting enchant
     *
     * @param soulPerKills
     * @param looting
     * @return
     */
    public static int getSoulDropChance(int soulPerKills, int looting, ItemStack sword) {
        return looting > 0
                ? soulPerKills / (looting + 1)
                : soulPerKills;
    }
}
