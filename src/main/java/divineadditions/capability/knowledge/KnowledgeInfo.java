package divineadditions.capability.knowledge;

import divineadditions.DivineAdditions;
import divineadditions.capability.base.OwnerCap;
import divineadditions.msg.KnowledgeMessage;
import divineadditions.utils.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Loader;

public class KnowledgeInfo extends OwnerCap<Entity> implements IKnowledgeInfo {
    public KnowledgeInfo(Entity owner) {
        super(owner);

        if (owner instanceof EntityPlayer && owner.getEntityWorld().isRemote) {
            // call cap from server
            accept(level());
        }
    }

    public KnowledgeInfo() {
        this(null);
    }

    @Override
    public Property<Integer> level() {
        return getOrCreate("Level", () -> new Property<>(0));
    }

    @Override
    public Property<Integer> defender() {
        return getOrCreate("Defender", () -> new Property<>(0));
    }

    @Override
    public void accept(Property property) {
        DivineAdditions.proxy.scheduleUpdate(this, () -> updateInner(property));
    }

    private void updateInner(Property prop) {
        Entity owner = getOwner();

        if (!(owner instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) owner;
        KnowledgeMessage msg = new KnowledgeMessage(this);

        if (player instanceof EntityPlayerMP) {
            DivineAdditions.networkWrapper.sendTo(msg, ((EntityPlayerMP) player));
        } else {
            DivineAdditions.networkWrapper.sendToServer(msg);

            if (prop == level()) {
                if (Loader.isModLoaded("jei")) {
                    divineadditions.jei.JeiModule.recalculateRecipes(level().get());
                }
            }
        }
    }
}
