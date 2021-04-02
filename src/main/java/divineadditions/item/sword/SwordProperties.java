package divineadditions.item.sword;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwordProperties {
    // region attackEffects
    private final List<PotionEffect> attackEffects = new ArrayList<>();

    // region attackDamage
    // region defendEffects
    private final List<PotionEffect> defendEffects = new ArrayList<>();
    private final Map<Enchantment, Integer> enchMap = new HashMap<>();
    private float attackDamage = 4;
    private int maxItemDamage = 100;

    // endregion

    // region maxItemDamage
    private double attackSpeed = -2.4000000953674316D;
    private boolean instantKill = false;
    private boolean immortal = false;
    private int soulPerKills = -1;

    // endregion

    // region attackSpeed

    public void saveToNbt(@Nonnull ItemStack stack) {
        setAttackDamage(stack, attackDamage);
        setMaxItemDamage(stack, maxItemDamage);
        setAttackSpeed(stack, attackSpeed);
        attackEffects.forEach(x -> addAttackEffect(stack, x));
        defendEffects.forEach(x -> addDefendEffect(stack, x));
        setInstantKill(stack, instantKill);
        setImmortal(stack, immortal);
        setSoulPerKills(stack, soulPerKills);
        enchMap.forEach((enchantment, level) -> enchant(stack, enchantment, level));
    }

    public float getAttackDamage(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getFloat("attackDamage");
    }

    public SwordProperties setAttackDamage(@Nonnull ItemStack stack, float attackDamage) {
        getPowersTag(stack).setFloat("attackDamage", attackDamage);
        return this;
    }

    public SwordProperties setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    // endregion

    public int getMaxItemDamage(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getInteger("maxItemDamage");
    }

    public SwordProperties setMaxItemDamage(@Nonnull ItemStack stack, int maxItemDamage) {
        getPowersTag(stack).setInteger("maxItemDamage", maxItemDamage);
        return this;
    }

    public SwordProperties setMaxItemDamage(int maxItemDamage) {
        this.maxItemDamage = maxItemDamage;
        return this;
    }

    public double getAttackSpeed(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getDouble("attackSpeed");
    }

    public SwordProperties setAttackSpeed(@Nonnull ItemStack stack, double attackSpeed) {
        getPowersTag(stack).setDouble("attackSpeed", attackSpeed);
        return this;
    }

    // endregion

    public SwordProperties setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public SwordProperties addAttackEffect(PotionEffect effect) {
        attackEffects.add(effect);
        return this;
    }

    public List<PotionEffect> getAttackEffects(@Nonnull ItemStack stack) {
        NBTTagCompound potions = getPowersTag(stack).getCompoundTag("attackEffects");
        return PotionUtils.getEffectsFromTag(potions);
    }

    public SwordProperties addAttackEffect(@Nonnull ItemStack stack, PotionEffect effect) {
        List<PotionEffect> attackEffects = getAttackEffects(stack);
        attackEffects.add(effect);
        getPowersTag(stack).setTag("attackEffects", divineadditions.utils.PotionUtils.writeEffects(null, attackEffects));
        return this;
    }

    public SwordProperties removeAttackEffect(@Nonnull ItemStack stack, PotionEffect effect) {
        List<PotionEffect> attackEffects = getAttackEffects(stack);
        attackEffects.remove(effect);
        getPowersTag(stack).setTag("attackEffects", divineadditions.utils.PotionUtils.writeEffects(null, attackEffects));
        return this;
    }

    // endregion

    // region instantKill

    public SwordProperties addDefendEffect(PotionEffect effect) {
        defendEffects.add(effect);
        return this;
    }

    public List<PotionEffect> getDefendEffects(@Nonnull ItemStack stack) {
        NBTTagCompound potions = getPowersTag(stack).getCompoundTag("defendEffects");
        return PotionUtils.getEffectsFromTag(potions);
    }

    public SwordProperties addDefendEffect(@Nonnull ItemStack stack, PotionEffect effect) {
        List<PotionEffect> attackEffects = getDefendEffects(stack);
        attackEffects.add(effect);
        getPowersTag(stack).setTag("defendEffects", divineadditions.utils.PotionUtils.writeEffects(null, attackEffects));
        return this;
    }

    public SwordProperties removeDefendEffect(@Nonnull ItemStack stack, PotionEffect effect) {
        List<PotionEffect> attackEffects = getDefendEffects(stack);
        attackEffects.remove(effect);
        getPowersTag(stack).setTag("defendEffects", divineadditions.utils.PotionUtils.writeEffects(null, attackEffects));
        return this;
    }

    // endregion

    // region immortal

    public boolean isInstantKill(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getBoolean("instantKill");
    }

    public SwordProperties setInstantKill(boolean instantKill) {
        this.instantKill = instantKill;
        return this;
    }

    public SwordProperties setInstantKill(@Nonnull ItemStack stack, boolean instantKill) {
        getPowersTag(stack).setBoolean("instantKill", instantKill);
        return this;
    }

    public boolean isImmortal(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getBoolean("immortal");
    }

    // endregion

    // region soulPerKills

    public SwordProperties setImmortal(boolean immortal) {
        this.immortal = immortal;
        return this;
    }

    public SwordProperties setImmortal(@Nonnull ItemStack stack, boolean immortal) {
        getPowersTag(stack).setBoolean("immortal", immortal);
        return this;
    }

    public int getSoulPerKills(@Nonnull ItemStack stack) {
        return getPowersTag(stack).getInteger("soulPerKills");
    }

    public SwordProperties setSoulPerKills(int soulPerKills) {
        this.soulPerKills = soulPerKills;
        return this;
    }

    // endregion

    // region enchantments

    public SwordProperties setSoulPerKills(@Nonnull ItemStack stack, int soulPerKills) {
        getPowersTag(stack).setInteger("soulPerKills", soulPerKills);
        return this;
    }

    public SwordProperties enchant(Enchantment e, int level) {
        enchMap.put(e, level);
        return this;
    }

    public SwordProperties enchant(@Nonnull ItemStack stack, Enchantment e, int level) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        map.put(e, level);
        EnchantmentHelper.setEnchantments(map, stack);
        return this;
    }

    // endregion

    public NBTTagCompound getPowersTag(@Nonnull ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("powers")) {
            tag.setTag("powers", new NBTTagCompound());
            saveToNbt(stack);
        }

        return tag.getCompoundTag("powers");
    }
}
