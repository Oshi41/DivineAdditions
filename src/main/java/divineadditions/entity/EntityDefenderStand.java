package divineadditions.entity;

import com.google.common.base.Optional;
import divineadditions.holders.Items;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityDefenderStand extends EntityLivingBase {
    private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(EntityDefenderStand.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private final NonNullList<ItemStack> armorItems;
    private final NonNullList<ItemStack> handItems;
    private int cooldown;

    private EntityLivingBase owner;


    public EntityDefenderStand(World worldIn) {
        super(worldIn);

        this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
        this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
        this.noClip = this.hasNoGravity();
        this.setSize(0.5F, 1.975F);
        setAlwaysRenderNameTag(false);
    }

    public EntityDefenderStand(World worldIn, EntityLivingBase summoner, BlockPos pos) {
        this(worldIn);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
        owner = summoner;
        dataManager.set(OWNER, Optional.of(owner.getUniqueID()));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(OWNER, Optional.absent());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {

        if (!world.isRemote && isEntityAlive()) {
            if ("arrow".equals(source.getDamageType())
                    || "player".equals(source.getDamageType())
                    || DamageSource.OUT_OF_WORLD.equals(source)
                    || source.isExplosion()) {
                if (!this.isEntityInvulnerable(source)) {

                    setHealth(getHealth() - amount);

                    if (getHealth() < 0.5) {
                        dropEquipment(false, 0);
                        setDead();
                    }
                }
            }
        }


        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (cooldown >= 0)
            cooldown--;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return armorItems;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        switch (slotIn.getSlotType()) {
            case HAND:
                return this.handItems.get(slotIn.getIndex());
            case ARMOR:
                return this.armorItems.get(slotIn.getIndex());
            default:
                return ItemStack.EMPTY;
        }
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        switch (slotIn.getSlotType()) {
            case HAND:
                this.playEquipSound(stack);
                this.handItems.set(slotIn.getIndex(), stack);
                break;
            case ARMOR:
                this.playEquipSound(stack);
                this.armorItems.set(slotIn.getIndex(), stack);
        }
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return SoundEvents.ENTITY_ARMORSTAND_FALL;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ARMORSTAND_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ARMORSTAND_BREAK;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        NBTTagCompound handItems = new NBTTagCompound();
        ItemStackHelper.saveAllItems(handItems, this.handItems);

        NBTTagCompound armor = new NBTTagCompound();
        ItemStackHelper.saveAllItems(armor, this.armorItems);

        compound.setTag("Hands", handItems);
        compound.setTag("Armor", armor);

        if (owner != null)
            compound.setUniqueId("Owner", owner.getUniqueID());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        ItemStackHelper.loadAllItems(compound.getCompoundTag("Hands"), handItems);
        ItemStackHelper.loadAllItems(compound.getCompoundTag("Armor"), armorItems);

        dataManager.set(OWNER, compound.hasKey("Owner")
                ? Optional.of(compound.getUniqueId("Owner"))
                : Optional.absent());
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (heldItem.getItem() == Items.defender_stand_activation) {
            return super.applyPlayerInteraction(player, vec, hand);
        }

        if (!world.isRemote && cooldown < 0) {
            cooldown = 5;
            world.setEntityState(this, (byte) 32);

            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(heldItem);

            if (heldItem.isEmpty()) {
                EntityEquipmentSlot clickedSlot = this.getClickedSlot(vec);

                if (hasItemInSlot(clickedSlot)) {
                    this.swapItem(player, clickedSlot, heldItem, hand);
                }
            } else {
                this.swapItem(player, entityequipmentslot, heldItem, hand);
            }

        }

        return EnumActionResult.SUCCESS;
    }

    /**
     * Returning slot from click position
     *
     * @param hitVec - click hit vector
     * @return
     */
    protected EntityEquipmentSlot getClickedSlot(Vec3d hitVec) {
        EntityEquipmentSlot entityequipmentslot = EntityEquipmentSlot.MAINHAND;
        double d0 = hitVec.y;
        EntityEquipmentSlot entityequipmentslot1 = EntityEquipmentSlot.FEET;

        if (d0 >= 0.1D && d0 < 0.1D + 0.45D && this.hasItemInSlot(entityequipmentslot1)) {
            entityequipmentslot = EntityEquipmentSlot.FEET;
        } else if (d0 >= 0.9D && d0 < 0.9D + 0.7D && this.hasItemInSlot(EntityEquipmentSlot.CHEST)) {
            entityequipmentslot = EntityEquipmentSlot.CHEST;
        } else if (d0 >= 0.4D && d0 < 0.4D + 0.8D && this.hasItemInSlot(EntityEquipmentSlot.LEGS)) {
            entityequipmentslot = EntityEquipmentSlot.LEGS;
        } else if (d0 >= 1.6D && this.hasItemInSlot(EntityEquipmentSlot.HEAD)) {
            entityequipmentslot = EntityEquipmentSlot.HEAD;
        }

        return entityequipmentslot;
    }

    private void swapItem(EntityPlayer player, EntityEquipmentSlot slot, ItemStack playerStack, EnumHand hand) {
        ItemStack stack = getItemStackFromSlot(slot);

        if (!playerStack.isEmpty() && playerStack.getCount() > 1) {
            if (stack.isEmpty()) {
                ItemStack itemstack1 = playerStack.copy();
                itemstack1.setCount(1);
                this.setItemStackToSlot(slot, itemstack1);
                playerStack.shrink(1);
            }
        } else {
            this.setItemStackToSlot(slot, playerStack);
            player.setHeldItem(hand, stack);
        }
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = getItemStackFromSlot(slot);

            if (!stack.isEmpty()) {
                this.entityDropItem(stack, 0.0F);
            }
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {

        if (id == 32) {
            if (world.isRemote) {
                cooldown = 5;
            }
        }

        super.handleStatusUpdate(id);
    }

    @Nullable
    public EntityLivingBase getOwner() {
        Optional<UUID> optional = dataManager.get(OWNER);
        if (!optional.isPresent()) {
            owner = null;
            return null;
        }

        UUID uuid = optional.get();

        if (owner == null || owner.getUniqueID() != uuid) {
            if (world instanceof WorldServer) {
                owner = ((EntityLivingBase) ((WorldServer) world).getEntityFromUuid(uuid));
            } else {
                owner = ((EntityLivingBase) world.getLoadedEntityList().stream().filter(x -> uuid.equals(x.getUniqueID())).findFirst().orElse(null));
            }
        }

        return owner;
    }
}
