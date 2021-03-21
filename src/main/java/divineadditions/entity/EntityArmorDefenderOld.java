package divineadditions.entity;

import divineadditions.DivineAdditions;
import divineadditions.config.DivineAdditionsConfig;
import divineadditions.item.ItemArmorEssence;
import divineadditions.utils.EntityAttributeHelper;
import divineadditions.utils.NbtUtils;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.registry.IArmorDescription;
import divinerpg.objects.entities.ai.AIDivineLookAround;
import divinerpg.objects.entities.ai.AIDivineRandomFly;
import divinerpg.objects.entities.ai.GhastLikeMoveHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityArmorDefenderOld extends AbstractSkeleton {
    private static final String killsName = "DefenderKills";

    private EntityAIAttackRangedBow<AbstractSkeleton> aiArrowAttack;
    private EntityAIAttackMelee aiAttackOnCollide;

    private IArmorDescription description;
    private EntityPlayer summoner;
    private ItemStack essence;
    private boolean canFly;

    public EntityArmorDefenderOld(World worldIn) {
        super(worldIn);
    }

    public EntityArmorDefenderOld(World worldIn, EntityPlayer victim, Map<EntityEquipmentSlot, ItemStack> items, ItemStack essence) {
        this(worldIn);

        this.description = ((ItemArmorEssence) essence.getItem()).getDescription(essence);
        this.essence = essence;
        this.summoner = victim;

        setCustomNameTag(victim.getName());

        items.forEach((slot, stack) -> {
            setItemStackToSlot(slot, stack);
            setDropChance(slot, 0);
        });

        this.canFly = canFly();

        customizeEntityAi();
        customizeEntity();
    }

    @Override
    protected void initEntityAI() {
        this.targetTasks.taskEntries.clear();

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));

        if (description == null)
            return;


    }

    @Override
    protected void applyEntityAttributes() {
        EntityAttributeHelper.applyAttributes(DivineAdditionsConfig.mobsConfig.armor_defender_attrs, this);

        if (getSummoner() != null) {
            increaseSummonCount(getSummoner());
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);

        EntityPlayer summoner = getSummoner();
        if (summoner == null && cause.getTrueSource() instanceof EntityPlayer) {
            summoner = (EntityPlayer) cause.getTrueSource();
        }

        increaseSummonCount(summoner);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        customizeEntityAi();
        recheckFlyAbility();
    }

    @Override
    public void setCombatTask() {
        if (this.world != null && !this.world.isRemote) {

            if (aiAttackOnCollide != null && aiArrowAttack != null) {
                this.tasks.removeTask(this.aiAttackOnCollide);
                this.tasks.removeTask(this.aiArrowAttack);

                ItemStack itemstack = this.getHeldItemMainhand();

                if (itemstack.getItem() instanceof ItemBow) {
                    int i = 20;

                    if (this.world.getDifficulty() != EnumDifficulty.HARD) {
                        i = 40;
                    }

                    this.aiArrowAttack.setAttackCooldown(i);
                    this.tasks.addTask(1, this.aiArrowAttack);
                } else {
                    this.tasks.addTask(1, this.aiAttackOnCollide);
                }
            }
        }
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_STRAY_STEP;
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        if (description != null) {
            nbt.setString("ArmorId", description.getRegistryName().toString());
        }

        if (summoner != null) {
            nbt.setUniqueId("Summoner", summoner.getUniqueID());
        }

        if (essence != null) {
            nbt.setTag("Essence", essence.serializeNBT());
        }

        nbt.setBoolean("Fly", canFly);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        String string = compound.getString("ArmorId");
        if (!string.isEmpty()) {
            description = DivineAPI.getArmorDescriptionRegistry().getValue(new ResourceLocation(string));
        }

        UUID id = compound.getUniqueId("Summoner");
        if (world != null) {
            summoner = world.getPlayerEntityByUUID(id);
        }

        if (compound.hasKey("Essence")) {
            essence = new ItemStack(compound.getCompoundTag("Essence"));
        }

        canFly = compound.getBoolean("Fly");

        super.readFromNBT(compound);
    }

    @Override
    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        super.dropLoot(wasRecentlyHit, lootingModifier, source);
        if (essence != null && !essence.isEmpty()) {
            entityDropItem(essence, 0);
        }
    }

    @Nullable
    public EntityPlayer getSummoner() {
        return summoner;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (canFly) {
            travelFly(strafe, vertical, forward);
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    public void travelFly(float strafe, float vertical, float forward) {
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

    /**
     * @param player
     * @return
     */
    private boolean increaseSummonCount(EntityPlayer player) {
        if (player == null)
            return false;

        NBTTagCompound tag = NbtUtils.getOrCreateModPlayerPersistTag(player, DivineAdditions.MOD_NAME);
        tag.setInteger(killsName, tag.getInteger(killsName) + 1);
        return true;
    }

    private void customizeEntityAi() {
        double speed = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();

        if (speed > 3.5) {
            tasks.addTask(3, new EntityAIMoveTowardsTarget(this, speed, 32.0F));
        }

        List<PathNodeType> paths = new ArrayList<>();

        if (preferWater()) {
            paths.add(PathNodeType.WATER);
        }

        if (preferLava()) {
            paths.add(PathNodeType.LAVA);
            paths.add(PathNodeType.DAMAGE_FIRE);
        }

        if (!paths.isEmpty()) {
            for (int i = 0; i < paths.size(); i++) {
                setPathPriority(paths.get(i), 16 - i);
            }
        }
    }

    /**
     * Server side only
     */
    private void customizeEntity() {
        if (getSummoner() == null || !world.isRemote)
            return;

        NBTTagCompound playerTag = NbtUtils.getOrCreateModPlayerPersistTag(getSummoner(), DivineAdditions.MOD_NAME);
        int killsCount = playerTag.getInteger(killsName);

        increaseStats(killsCount);

        double speed = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        double attackSpeed = getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
        double followRange = getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

        aiArrowAttack = new EntityAIAttackRangedBow<>(
                this,
                speed,
                (int) (SharedMonsterAttributes.ATTACK_SPEED.getDefaultValue() * 20 / attackSpeed),
                (float) followRange);

        aiAttackOnCollide = new EntityAIAttackMelee(
                this,
                speed,
                true);

        this.setCombatTask();

        if (canFly()) {
            this.tasks.addTask(7, new AIDivineRandomFly(this));
            this.tasks.addTask(5, new AIDivineLookAround(this));
        } else {
            this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
            this.tasks.addTask(7, new EntityAIWander(this, 1));
            this.tasks.addTask(8, new EntityAILookIdle(this));
        }

        recheckFlyAbility();

        setAttackTarget(getSummoner());
    }

    private void increaseStats(int killsCount) {
        if (killsCount < 1)
            return;

        increaseWith(SharedMonsterAttributes.MAX_HEALTH, 100 * killsCount);
        increaseWith(SharedMonsterAttributes.ATTACK_DAMAGE, 3 * killsCount);
        increaseWith(SharedMonsterAttributes.MOVEMENT_SPEED, 0.03 * killsCount);
        increaseWith(SharedMonsterAttributes.ATTACK_SPEED, -0.1 * killsCount);
    }

    private void increaseWith(IAttribute attribute, double value) {
        IAttributeInstance instance = getEntityAttribute(attribute);
        double result = instance.getAttributeValue() + value;
        instance.setBaseValue(result);
    }

    private void recheckFlyAbility() {

        boolean canFly = canFly();
        this.moveHelper = canFly ?
                new GhastLikeMoveHelper(this)
                : new EntityMoveHelper(this);
    }


    /////////////////////////////////////
    // Helping methods
    ////////////////////////////////////

    private boolean canFly() {
        if (description != null) {
            return description.getRegistryName().getResourcePath().contains("angelic");
        }

        return false;
    }

    private boolean preferWater() {
        if (description != null) {
            String resourcePath = description.getRegistryName().getResourcePath();

            return resourcePath.equals("aqua")
                    || resourcePath.equals("wildwood")
                    || resourcePath.equals("kraken");
        }

        return false;
    }

    private boolean preferLava() {
        if (description != null) {
            String resourcePath = description.getRegistryName().getResourcePath();

            return resourcePath.equals("bedrock")
                    || resourcePath.equals("netherite")
                    || resourcePath.equals("inferno");
        }

        return false;
    }
}
