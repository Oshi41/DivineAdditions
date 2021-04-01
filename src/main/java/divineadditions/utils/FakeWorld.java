package divineadditions.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class FakeWorld extends World {
    private final Cache<ChunkPos, ChunkPrimer> cache = CacheBuilder
            .newBuilder()
            .softValues()
            .build();

    private WeakReference<World> worldRef;

    public FakeWorld(World world) {
        super(null, new WorldInfo(new NBTTagCompound()), new WorldProvider() {
            @Override
            public DimensionType getDimensionType() {
                return DimensionType.OVERWORLD;
            }
        }, new Profiler(), true);

        worldRef = new WeakReference<>(world);
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (isOutsideBuildHeight(pos))
            return false;

        if (isOriginalChunkLoaded(new ChunkPos(pos))) {
            return worldRef.get().setBlockState(pos, newState, flags);
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkPrimer primer = cache.getIfPresent(chunkPos);
        if (primer == null) {
            cache.put(chunkPos, primer = new ChunkPrimer());
        }

        primer.setBlockState(Math.abs(pos.getX() % 16), pos.getY(), Math.abs(pos.getZ() % 16), newState);
        return true;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (isOutsideBuildHeight(pos))
            return Blocks.AIR.getDefaultState();

        // using origin world blocks
        if (isOriginalChunkLoaded(new ChunkPos(pos))) {
            return worldRef.get().getBlockState(pos);
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkPrimer primer = cache.getIfPresent(chunkPos);
        if (primer != null) {
            return primer.getBlockState(Math.abs(pos.getX() % 16), pos.getY(), Math.abs(pos.getZ() % 16));
        }

        return Blocks.AIR.getDefaultState();
    }

    /**
     * Creates chunk prime
     *
     * @param pos
     * @return
     */
    @Nonnull
    public ChunkPrimer createFrom(ChunkPos pos) {
        ChunkPrimer primer = cache.getIfPresent(pos);

        if (primer == null) {
            return new ChunkPrimer();
        }

        return primer;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return isOriginalChunkLoaded(x, z, allowEmpty) || cache.getIfPresent(new ChunkPos(x, z)) != null;
    }

    protected boolean isOriginalChunkLoaded(ChunkPos pos) {
        return isOriginalChunkLoaded(pos.x, pos.z, false);
    }

    protected boolean isOriginalChunkLoaded(int x, int z, boolean allowEmpty) {
        World world = worldRef.get();
        if (world != null) {
            boolean chunkGeneratedAt = world.isChunkGeneratedAt(x, z) && world.getChunkFromChunkCoords(x, z).isPopulated();

            // original world already contains that chunk, we need to delete this
            if (chunkGeneratedAt) {
                cache.invalidate(new ChunkPos(x, z));
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public MinecraftServer getMinecraftServer() {
        World world = worldRef.get();
        if (world != null)
            return world.getMinecraftServer();

        return null;
    }
}
