package divineadditions.item.sword;

import com.google.common.collect.Multimap;
import divineadditions.api.InfiniteAttackEvent;
import divineadditions.event.LivingDropsEventHandler;
import divineadditions.item.ItemMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCustomSword extends ItemSword {
    private final SwordProperties properties;

    public ItemCustomSword(ToolMaterial material, SwordProperties properties) {
        super(material);
        this.properties = properties;
    }

    // region Overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getSwordProps().getMaxItemDamage(stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!target.getEntityWorld().isRemote) {
            stack.damageItem(getAppliedPotionsCount(target, getSwordProps().getAttackEffects(stack)), attacker);

            if (getSwordProps().isInstantKill(stack)) {
                return tryInstantKill(stack, target, attacker);
            }
        }

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity victim) {
        if (getSwordProps().isInstantKill(stack)
                && !victim.getEntityWorld().isRemote
                && victim.isEntityAlive()
                && victim instanceof EntityPlayer
                && ((EntityPlayer) victim).isCreative()
                && tryInstantKill(stack, ((EntityPlayer) victim), player)) {
            // todo trigger
            return true;
        }

        return super.onLeftClickEntity(stack, player, victim);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!playerIn.getEntityWorld().isRemote) {
            int appliedPotionsCount = getAppliedPotionsCount(playerIn, getSwordProps().getDefendEffects(stack));

            if (appliedPotionsCount > 0) {
                stack.damageItem(appliedPotionsCount, playerIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            float attackDamage = Math.max(getSwordProps().getAttackDamage(stack), getAttackDamage());

            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", getSwordProps().getAttackSpeed(stack), 0));
        }

        return multimap;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return ItemMod.createImmortalEntity(world, location, itemstack);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return getSwordProps().isImmortal(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        getSwordProps().getPowersTag(stack);
        return stack;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        getSwordProps().getPowersTag(stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        if (getSwordProps().isImmortal(stack)) {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.immortal_item").getFormattedText());
            tooltip.add("");
        }

        if (getSwordProps().isInstantKill(stack)) {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.immortal_item").getFormattedText());
            tooltip.add("");
        }

        int soulPerKills = getSwordProps().getSoulPerKills(stack);
        if (soulPerKills > 0) {
            int looting = EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack);
            String format = new DecimalFormat("#0.000").format(1f / LivingDropsEventHandler.getSoulDropChance(soulPerKills, looting, stack));
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.soul_per_kills", format).getFormattedText());
            tooltip.add("");
        }

        List<PotionEffect> effects = getSwordProps().getAttackEffects(stack);
        if (!effects.isEmpty()) {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.attack_effects").getFormattedText());
            divineadditions.utils.PotionUtils.addToolTipNew(effects, tooltip, 1);
            tooltip.add("");
        }

        effects = getSwordProps().getDefendEffects(stack);
        if (!effects.isEmpty()) {
            tooltip.add(new TextComponentTranslation("divineadditions.tooltip.defend_effects").getFormattedText());
            divineadditions.utils.PotionUtils.addToolTipNew(effects, tooltip, 1);
            tooltip.add("");
        }
    }

    // endregion

    public SwordProperties getSwordProps() {
        return properties;
    }

    // region private

    protected int getAppliedPotionsCount(@Nonnull EntityLivingBase target, List<PotionEffect> effects) {
        int result = 0;

        List<PotionEffect> instant = effects.stream().filter(x -> x.getPotion().isInstant()).collect(Collectors.toList());
        for (PotionEffect effect : instant) {
            target.hurtResistantTime = 0;
            effect.performEffect(target);
            result++;
        }

        effects.removeAll(instant);

        for (PotionEffect effect : effects) {
            if (target.getActivePotionEffect(effect.getPotion()) == null) {
                target.addPotionEffect(effect);
                result++;
            }
        }

        return result;
    }

    protected boolean tryInstantKill(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        InfiniteAttackEvent event = new InfiniteAttackEvent(target, attacker);
        if (MinecraftForge.EVENT_BUS.post(event))
            return false;

        String damageSourceName = attacker.getName();

        target.getCombatTracker().trackDamage(new DamageSource(damageSourceName).setDamageBypassesArmor(), target.getHealth(), target.getHealth());
        target.setHealth(0);
        target.onDeath(new EntityDamageSource(damageSourceName, attacker));
        stack.damageItem(10, attacker);
        return true;
    }

    // endregion
}
