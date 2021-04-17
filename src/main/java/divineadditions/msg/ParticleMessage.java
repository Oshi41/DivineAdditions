package divineadditions.msg;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ParticleMessage implements IMessage {
    private Vec3d pos;
    private EnumParticleTypes types;
    private int count;
    private AxisAlignedBB area;

    public ParticleMessage() {

    }

    public ParticleMessage(Vec3d pos, EnumParticleTypes types, int count, AxisAlignedBB area) {
        this.pos = pos;
        this.types = types;
        this.count = count;
        this.area = area;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        count = buf.readInt();
        types = EnumParticleTypes.valueOf(ByteBufUtils.readUTF8String(buf));

        Vec3d min = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3d max = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

        area = new AxisAlignedBB(min, max);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);


        buf.writeInt(count);
        ByteBufUtils.writeUTF8String(buf, types.name());

        buf.writeDouble(area.minX);
        buf.writeDouble(area.minY);
        buf.writeDouble(area.minZ);

        buf.writeDouble(area.maxX);
        buf.writeDouble(area.maxY);
        buf.writeDouble(area.maxZ);
    }

    public AxisAlignedBB getArea() {
        return area;
    }

    public EnumParticleTypes getTypes() {
        return types;
    }

    public int getCount() {
        return count;
    }

    public Vec3d getPos() {
        return pos;
    }
}
