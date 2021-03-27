package divineadditions.item;

import com.google.common.collect.Multimap;
import divineadditions.DivineAdditions;
import divineadditions.api.InfiniteAttackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemBlankSword extends ItemSword {
    protected final static String damageSourceName = new ResourceLocation(DivineAdditions.MOD_NAME, "blank_sword").toString();
    protected final static String damageName = "DamageAmount";
    protected final static String maxDamageName = "MaxItemDamage";
    protected final static String attackSpeedName = "AttackSpeed";
    protected final static String attackPotionsName = "AttackPotions";
    protected final static String defencePotionName = "DefendsPotions";
    protected final static String instantKillName = "InstantKill";

    public ItemBlankSword(ToolMaterial material) {
        super(material);
    }

    // region Overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (checkItemStack(stack)) {
            return stack.getTagCompound().getInteger(maxDamageName);
        }

        return super.getMaxDamage(stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!target.getEntityWorld().isRemote) {
            if (checkItemStack(stack)) {
                stack.damageItem(applyPotions(target, getAttackEffects(stack)), attacker);

                if (instantKill(stack, target, attacker)) {
                    return true;
                }
            }
        }

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (checkItemStack(stack) && stack.getTagCompound().getBoolean(instantKillName)) {
            if (entity.getEntityWorld().isRemote && entity instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) entity;

                if (target.isCreative() && target.isEntityAlive()) {
                    instantKill(stack, target, player);
                    // todo add trigger
                    return true;
                }
            }
        }


        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (checkItemStack(stack) && !playerIn.getEntityWorld().isRemote) {
            int appliedCount = applyPotions(playerIn, getDefenceEffects(stack));
            stack.damageItem(appliedCount, playerIn);

            if (appliedCount > 0) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (checkItemStack(stack)) {
            Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

            if (slot == EntityEquipmentSlot.MAINHAND) {
                multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", stack.getTagCompound().getFloat(damageName), 0));
                multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", stack.getTagCompound().getDouble(attackSpeedName), 0));
            }

            return multimap;
        }

        return super.getAttributeModifiers(slot, stack);
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return ImmortalItem.createImmortalEntity(world, location, itemstack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    // endregion

    // region Interface methods

    /**
     * current attack damage
     *
     * @param stack - sword item
     * @return
     */
    public float getAttackDamage(ItemStack stack) {
        if (checkItemStack(stack)) {
            return stack.getTagCompound().getFloat(damageName);
        }

        return getAttackDamage();
    }

    /**
     * Change current damage amount of sword
     *
     * @param sword    - sword item
     * @param newValue - new damage value
     */
    public void setCurrentDamage(ItemStack sword, float newValue) {
        if (checkItemStack(sword)) {
            sword.getTagCompound().setFloat(damageName, newValue);
        }
    }

    /**
     * Current potion effects on attack
     *
     * @param stack
     * @return
     */
    public List<PotionEffect> getAttackEffects(ItemStack stack) {
        return getEffectsFrom(stack, attackPotionsName);
    }

    /**
     * Adding attack effect on every hit
     *
     * @param sword
     * @param effect
     */
    public void addAttackEffect(ItemStack sword, PotionEffect effect) {
        addEffect(sword, effect, attackPotionsName);
    }

    /**
     * Current potion effects on defence (right click)
     *
     * @param stack
     * @return
     */
    public List<PotionEffect> getDefenceEffects(ItemStack stack) {
        return getEffectsFrom(stack, defencePotionName);
    }

    /**
     * Adding attack effect on rignt click
     *
     * @param sword
     * @param effect
     */
    public void addDefenceEffect(ItemStack sword, PotionEffect effect) {
        addEffect(sword, effect, defencePotionName);
    }

    /**
     * Removes attack potion effect
     *
     * @param sword
     * @param effect
     */
    public void removeAttackEffect(ItemStack sword, PotionEffect effect) {
        removeEffect(sword, effect, attackPotionsName);
    }

    /**
     * removes defend potion effect
     *
     * @param sword
     * @param effect
     */
    public void removeDefenceEffect(ItemStack sword, PotionEffect effect) {
        removeEffect(sword, effect, defencePotionName);
    }

    /**
     * Setting current attack speed
     *
     * @param sword
     * @param value
     */
    public void setAttackSpeed(ItemStack sword, double value) {
        if (checkItemStack(sword)) {
            sword.getTagCompound().setDouble(attackSpeedName, value);
        }
    }

    // endregion

    // region helping methods

    public void setMaxDamageName(ItemStack sword, int maxDamage) {
        if (checkItemStack(sword)) {
            sword.getTagCompound().setInteger(maxDamageName, maxDamage);
        }
    }

    /**
     * Check if current stack is Blank sword
     *
     * @param stack
     * @return
     */
    protected boolean checkItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemBlankSword))
            return false;

        if (!stack.hasTagCompound()) {
            NBTTagCompound nbtRaw = new NBTTagCompound();
            stack.setTagCompound(nbtRaw);

            nbtRaw.setFloat(damageName, 0);
            nbtRaw.setDouble(attackSpeedName, -2.4000000953674316D);
            nbtRaw.setTag(attackPotionsName, new NBTTagCompound());
            nbtRaw.setTag(defencePotionName, new NBTTagCompound());
            nbtRaw.setBoolean(instantKillName, false);
            nbtRaw.setInteger(maxDamageName, 100);
        }

        return true;
    }

    protected int applyPotions(EntityLivingBase target, List<PotionEffect> effects) {
        int result = 0;

        for (PotionEffect effect : effects) {
            if (target.getActivePotionEffect(effect.getPotion()) != null) {
                target.addPotionEffect(effect);
                result++;
            }
        }

        return result;
    }

    protected boolean instantKill(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (stack.getTagCompound().getBoolean(instantKillName)) {
            InfiniteAttackEvent event = new InfiniteAttackEvent(target);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                target.getCombatTracker().trackDamage(new DamageSource(damageSourceName).setDamageBypassesArmor(), target.getHealth(), target.getHealth());
                target.setHealth(0);
                target.onDeath(new EntityDamageSource(damageSourceName, attacker));
                stack.damageItem(10, attacker);
                return true;
            }
        }

        return false;
    }

    private List<PotionEffect> getEffectsFrom(ItemStack stack, String tagName) {
        if (checkItemStack(stack)) {
            return PotionUtils.getEffectsFromTag(stack.getTagCompound().getCompoundTag(tagName));
        }

        return new ArrayList<>();
    }

    private void addEffect(ItemStack sword, PotionEffect effect, String tagName) {
        if (checkItemStack(sword)) {
            List<PotionEffect> list = getEffectsFrom(sword, tagName);
            list.add(effect);
            PotionUtils.addCustomPotionEffectToList(sword.getTagCompound().getCompoundTag(tagName), list);
        }
    }

    private void removeEffect(ItemStack sword, PotionEffect effect, String tagName) {
        if (checkItemStack(sword)) {
            List<PotionEffect> list = getEffectsFrom(sword, tagName);
            list.remove(effect);
            PotionUtils.addCustomPotionEffectToList(sword.getTagCompound().getCompoundTag(tagName), list);
        }
    }

    // endregion
}
