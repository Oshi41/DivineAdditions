package divineadditions.entity;

import divineadditions.api.IEntityCatcher;
import divineadditions.api.ISingleItemHandler;
import divineadditions.holders.Items;
import divineadditions.item.ItemEntityBullet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCageBullet extends EntityThrowable implements ISingleItemHandler {
    private static final DataParameter<ItemStack> BULLET = EntityDataManager.createKey(EntityCageBullet.class, DataSerializers.ITEM_STACK);

    public EntityCageBullet(World worldIn) {
        super(worldIn);
    }

    public EntityCageBullet(World worldIn, EntityLivingBase throwerIn, ItemStack bullet) {
        super(worldIn, throwerIn);
        getDataManager().set(BULLET, bullet);
        this.setSize(0.25F, 0.25F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager dataManager = getDataManager();
        dataManager.register(BULLET, net.minecraft.init.Items.SNOWBALL.getDefaultInstance());
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        switch (result.typeOfHit) {
            case BLOCK:
                setDead();
                return;

            case MISS:
                return;

            case ENTITY:
                ItemStack itemStack = getDataManager().get(BULLET).copy();
                if (itemStack.getItem() instanceof IEntityCatcher) {
                    IEntityCatcher catcher = (IEntityCatcher) itemStack.getItem();

                    if (catcher.canCatch(result.entityHit)) {
                        itemStack.setTagCompound(new NBTTagCompound());
                        if (catcher.tryCatch(result.entityHit, itemStack.getTagCompound())) {
                            BlockPos position = result.entityHit.getPosition();

                            result.entityHit.setDead();

                            Block.spawnAsEntity(world, position, itemStack);
                        }
                    }
                }

                setDead();
        }
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = getDataManager().get(BULLET);
        return itemStack;
    }


}
