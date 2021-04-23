package divineadditions.entity;

import divineadditions.ai.EntityAIAttackMelee;
import divineadditions.api.IArmorEssence;
import divineadditions.capability.knowledge.IKnowledgeInfo;
import divineadditions.config.MobConfig;
import divineadditions.utils.EntityAttributeHelper;
import divinerpg.objects.entities.ai.AIDivineLookAround;
import divinerpg.objects.entities.ai.AIDivineRandomFly;
import divinerpg.objects.entities.ai.GhastLikeMoveHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EntityArmorDefender extends AbstractSkeleton {
    private final EntityAIAttackRangedBow<AbstractSkeleton> aiArrowAttack = new EntityAIAttackRangedBow<AbstractSkeleton>(this, 1.0D, 20, 15.0F);
    private EntityPlayer summoner;
    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false);
    private ItemStack essence;
    private final AIDivineRandomFly aiDivineRandomFly = new AIDivineRandomFly(this);
    private final AIDivineLookAround aiDivineLookAround = new AIDivineLookAround(this);
    private final EntityAIMoveTowardsRestriction aiMoveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1);
    private final EntityAIWander aiEntityAIWander = new EntityAIWander(this, 1);
    private ResourceLocation description;
    private boolean canFly;
    private int generation = 0;


    public EntityArmorDefender(World worldIn) {
        super(worldIn);
    }

    public EntityArmorDefender(World worldIn, Map<EntityEquipmentSlot, ItemStack> items, EntityPlayer summoner, ItemStack essence) {
        this(worldIn);

        IKnowledgeInfo capability = summoner.getCapability(IKnowledgeInfo.KnowledgeCapability, null);
        if (capability != null) {
            generation = capability.defender().get();
        }

        this.summoner = summoner;
        setEssence(essence);
        setPreferredPaths();
        setCustomNameTag(summoner.getName());

        items.forEach((slot, stack) -> {
            setItemStackToSlot(slot, stack.copy());
            setDropChance(slot, 0);
        });

        increaseStats(generation);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityAttributeHelper.applyAttributes(MobConfig.armor_defender_attrs, this);
    }

    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));

        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    }

    @Override
    public void setCombatTask() {
        if (world == null
                || world.isRemote
                || aiAttackOnCollide == null
                || aiArrowAttack == null
                || aiDivineLookAround == null
                || aiDivineRandomFly == null
                || aiMoveTowardsRestriction == null
                || aiEntityAIWander == null
        )
            return;

        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        tasks.removeTask(aiDivineLookAround);
        tasks.removeTask(aiDivineRandomFly);
        tasks.removeTask(aiMoveTowardsRestriction);
        tasks.removeTask(aiEntityAIWander);


        ItemStack itemstack = this.getHeldItemMainhand();

        if (itemstack.getItem() instanceof ItemBow) {
            int i = 20;

            if (this.world.getDifficulty() != EnumDifficulty.HARD) {
                i = 40;
            }

            i -= generation / 3.;

            this.aiArrowAttack.setAttackCooldown(i);
            this.tasks.addTask(4, this.aiArrowAttack);

        } else {
            this.tasks.addTask(4, this.aiAttackOnCollide);
        }


        if (canFly) {
            this.tasks.addTask(7, aiDivineLookAround);
            this.tasks.addTask(5, aiDivineRandomFly);
        } else {
            this.tasks.addTask(5, aiMoveTowardsRestriction);
            this.tasks.addTask(7, aiEntityAIWander);
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_STRAY_STEP;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        canFly = compound.getBoolean("CanFly");
        generation = compound.getInteger("Generation");
        setEssence(new ItemStack(compound.getCompoundTag("Essence")));

        if (summoner != null)
            compound.setUniqueId("Summoner", summoner.getUniqueID());

        super.readEntityFromNBT(compound);

        setPreferredPaths();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("CanFly", canFly);

        if (essence != null && essence.isEmpty())
            compound.setTag("Essence", essence.serializeNBT());

        compound.setInteger("Generation", generation);

        if (summoner != null) {
            compound.setUniqueId("Summoner", summoner.getUniqueID());
        }

        super.writeEntityToNBT(compound);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (canFly) {
            travelFly(strafe, vertical, forward);
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    /**
     * Dropping here essence
     *
     * @param wasRecentlyHit
     * @param lootingModifier
     * @param source
     */
    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
        if (essence != null && !essence.isEmpty()) {
            entityDropItem(essence, 0);
        }
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Nullable
    @Override
    public EntityLivingBase getAttackTarget() {
        if (summoner != null)
            return summoner;

        return super.getAttackTarget();
    }

    @Override
    protected EntityArrow getArrow(float p_190726_1_) {
        EntityArrow arrow = super.getArrow(p_190726_1_);
        arrow.setDamage(getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        return arrow;
    }

    // region Private methods

    private void travelFly(float strafe, float vertical, float forward) {
        if (this.isInWater()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
        } else if (this.isInLava()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        } else {
            float f = 0.91F;

            if (this.onGround) {
                BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
                IBlockState underState = this.world.getBlockState(underPos);
                f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
            }

            float f1 = 0.16277136F / (f * f * f);
            this.moveRelative(strafe, vertical, forward, this.onGround ? 0.1F * f1 : 0.02F);
            f = 0.91F;

            if (this.onGround) {
                BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
                IBlockState underState = this.world.getBlockState(underPos);
                f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= f;
            this.motionY *= f;
            this.motionZ *= f;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d1 = this.posX - this.prevPosX;
        double d0 = this.posZ - this.prevPosZ;
        float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    private EntityMoveHelper createMoveHelper() {
        return canFly
                ? new GhastLikeMoveHelper(this)
                : new EntityMoveHelper(this);
    }

    private void setEssence(ItemStack essence) {
        this.essence = essence;

        if (essence.getItem() instanceof IArmorEssence) {
            description = ((IArmorEssence) essence.getItem()).getDescription(essence).getRegistryName();
        }
    }

    private void increaseStats(int killsCount) {
        if (killsCount < 1)
            return;

        increaseWith(SharedMonsterAttributes.MAX_HEALTH, 100 * killsCount);
        increaseWith(SharedMonsterAttributes.ATTACK_DAMAGE, 3 * killsCount);
        increaseWith(SharedMonsterAttributes.MOVEMENT_SPEED, 0.03 * killsCount);
        increaseWith(SharedMonsterAttributes.ATTACK_SPEED, -0.1 * killsCount);

        setHealth(getMaxHealth());
    }

    private void increaseWith(IAttribute attribute, double value) {
        IAttributeInstance instance = getEntityAttribute(attribute);
        double result = instance.getAttributeValue() + value;
        instance.setBaseValue(result);
    }

    private void setPreferredPaths() {
        if (description != null) {
            String id = description.toString();

            if (Arrays.asList(MobConfig.aerSets).contains(id)) {
                canFly = true;
            }

            List<PathNodeType> paths = new ArrayList<>();

            if (Arrays.asList(MobConfig.waterSets).contains(id)) {
                paths.add(PathNodeType.WATER);
            }

            if (Arrays.asList(MobConfig.fireSets).contains(id)) {
                paths.add(PathNodeType.LAVA);
                paths.add(PathNodeType.DAMAGE_FIRE);
            }

            if (!paths.isEmpty()) {
                for (int i = 0; i < paths.size(); i++) {
                    setPathPriority(paths.get(i), 16 - i);
                }
            }
        }

        this.moveHelper = createMoveHelper();
    }

    // endregion
}
