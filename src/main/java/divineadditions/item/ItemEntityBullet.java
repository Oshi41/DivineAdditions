package divineadditions.item;

import divineadditions.api.IItemEntityBullet;
import divineadditions.entity.EntityCageBullet;
import divinerpg.utils.Lazy;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemEntityBullet extends Item implements IItemEntityBullet {
    private final Lazy<ItemStack> catalyst;

    public ItemEntityBullet(Supplier<ItemStack> catalyst) {
        this.catalyst = new Lazy<>(catalyst);
    }

    public ItemEntityBullet(String name, int count) {
        Item item = Item.getByNameOrId(name);
        if (item == null) {
            item = Items.GUNPOWDER;
        }

        final Item catalystComponent = item;

        catalyst = new Lazy<>(() -> new ItemStack(catalystComponent, count));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        ResourceLocation entityId = getContainingEntityId(stack.getTagCompound());
        if (entityId != null) {
            String translationName = EntityList.getTranslationName(entityId);
            if (translationName != null) {
                tooltip.add(new TextComponentTranslation("entity." + translationName + ".name").getFormattedText());
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getContainingEntityId(stack.getTagCompound()) != null;
    }

    @Override
    public Entity createBulletEntity(World world, EntityPlayer player) {
        EntityCageBullet bullet = new EntityCageBullet(world, player, this.getDefaultInstance());
        Vec3d pos = player.getLookVec()
                .addVector(bullet.posX, bullet.posY, bullet.posZ);
        bullet.setPosition(pos.x, pos.y, pos.z);
        bullet.shoot(player, player.rotationPitch, player.rotationYaw, 0, 3, 1.0F);

        return bullet;
    }

    @Override
    public ItemStack getCatalyst() {
        return catalyst.getValue();
    }
}
