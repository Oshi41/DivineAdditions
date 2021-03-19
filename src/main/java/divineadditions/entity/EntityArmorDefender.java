package divineadditions.entity;

import divineadditions.config.DivineAdditionsConfig;
import divineadditions.item.ItemArmorEssence;
import divineadditions.utils.EntityAttributeHelper;
import divinerpg.api.DivineAPI;
import divinerpg.api.armor.registry.IArmorDescription;
import divinerpg.objects.entities.ai.GhastLikeMoveHelper;
import net.minecraft.entity.IEntityLivingData;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityArmorDefender extends AbstractSkeleton {
    private EntityAIAttackRangedBow<AbstractSkeleton> aiArrowAttack;
    private EntityAIAttackMelee aiAttackOnCollide;

    private IArmorDescription description;
    private EntityPlayer summoner;
    private ItemStack essence;

    public EntityArmorDefender(World worldIn) {
        super(worldIn);

        aiArrowAttack = new EntityAIAttackRangedBow<AbstractSkeleton>(this, 1.2, 20, 15.0F);
        aiAttackOnCollide = new EntityAIAttackMelee(this, 1.4, false);
    }

    public EntityArmorDefender(World worldIn, EntityPlayer victim, Map<EntityEquipmentSlot, ItemStack> items, ItemStack essence) {
        this(worldIn);

        this.description = ((ItemArmorEssence) essence.getItem()).getDescription(essence);
        this.essence = essence;
        this.summoner = victim;

        setAttackTarget(victim);
        setCustomNameTag(victim.getName());

        items.forEach((slot, stack) -> {
            setItemStackToSlot(slot, stack);
            setDropChance(slot, 0);
        });
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        // ignored
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

            if (preferWater()) {
                for (PathNodeType value : PathNodeType.values()) {

                    if (value != PathNodeType.WATER)
                        setPathPriority(value, -1);
                    else
                        setPathPriority(value, 16);
                }
            }

            if (preferLava()) {
                for (PathNodeType value : PathNodeType.values()) {

                    if (value != PathNodeType.LAVA && value != PathNodeType.DAMAGE_FIRE)
                        setPathPriority(value, -1);
                    else
                        setPathPriority(value, 16);
                }
            }

            IAttributeInstance speedAttr = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

            if (speedAttr.getAttributeValue() > 3.5) {
                tasks.addTask(3, new EntityAIMoveTowardsTarget(this, speedAttr.getAttributeValue(), 32.0F));
            }
        }

        this.moveHelper = canFly()
                ? new GhastLikeMoveHelper(this)
                : new EntityMoveHelper(this);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityAttributeHelper.applyAttributes(DivineAdditionsConfig.mobsConfig.armor_defender_attrs, this);
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

    public void increaseStats(double increase) {
        List<IAttribute> attrs = Arrays.asList(SharedMonsterAttributes.ARMOR,
                SharedMonsterAttributes.MAX_HEALTH,
                SharedMonsterAttributes.ATTACK_DAMAGE,
                SharedMonsterAttributes.ATTACK_SPEED,
                SharedMonsterAttributes.MOVEMENT_SPEED);

        for (IAttribute attribute : attrs) {
            IAttributeInstance instance = getEntityAttribute(attribute);
            if (instance != null) {
                double newValue = instance.getAttributeValue() * increase;
                instance.setBaseValue(newValue);
            }
        }
    }
}
