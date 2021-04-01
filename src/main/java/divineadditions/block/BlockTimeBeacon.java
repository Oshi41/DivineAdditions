package divineadditions.block;

import divineadditions.entity.EntityAncientVillager;
import divineadditions.tile.TileEntityTimeBeacon;
import divineadditions.utils.StructureUtils;
import divineadditions.world.dimension.planet.PlanetBiome;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class BlockTimeBeacon extends BlockContainer {
    public BlockTimeBeacon() {
        super(Material.GLASS, MapColor.RED);
        setTickRandomly(true);
    }


    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        super.randomTick(worldIn, pos, state, random);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn instanceof WorldServer) {
            ItemStack heldItem = playerIn.getHeldItem(hand);

            if (heldItem.getItem() == Items.ENDER_EYE) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof TileEntityTimeBeacon) {
                    StructureUtils.StructureInfo info = StructureUtils.readFromNbt(((WorldServer) worldIn), PlanetBiome.ancientPortalId);
                    if (info != null) {
                        BlockPattern.PatternHelper patternHelper = info.match(worldIn, pos);
                        if (patternHelper != null) {
                            heldItem.shrink(1);
                            worldIn.setBlockToAir(pos);
                            info.clearBlocks(patternHelper, worldIn);
                            Explosion explosion = worldIn.newExplosion(playerIn, pos.getX(), pos.getY(), pos.getZ(), 5, false, false);

                            for (Map.Entry<EntityPlayer, Vec3d> entry : explosion.getPlayerKnockbackMap().entrySet()) {
                                Vec3d vec3d = entry.getValue().scale(2);
                                entry.getKey().addVelocity(vec3d.x, vec3d.y, vec3d.z);
                            }

                            EntityAncientVillager villager = new EntityAncientVillager(worldIn, playerIn);
                            villager.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
                            worldIn.spawnEntity(villager);

                            return true;
                        }
                    }
                }
            }

            // todo debug
            if (heldItem.getItem() == Items.ENDER_PEARL && playerIn.isCreative()) {
                StructureUtils.StructureInfo info = StructureUtils.readFromNbt(((WorldServer) worldIn), PlanetBiome.ancientPortalId);
                if (info != null) {
                    Template template = info.getTemplate();
                    BlockPos size = template.getSize();
                    template.addBlocksToWorld(worldIn, pos.add(size.getX() / -2, 0, size.getZ() / -2), new PlacementSettings());
                    heldItem.shrink(1);
                    return true;
                }
            }
        }


        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int radius = 13;

        for (int i = 0; i < 127; i++) {
            worldIn.spawnParticle(
                    EnumParticleTypes.REDSTONE,
                    pos.getX() + rand.nextInt(radius) - rand.nextInt(radius),
                    pos.getY() + rand.nextInt(radius) + 1,
                    pos.getZ() + rand.nextInt(radius) - rand.nextInt(radius),
                    rand.nextFloat() - rand.nextFloat(),
                    rand.nextFloat() - rand.nextFloat(),
                    rand.nextFloat() - rand.nextFloat()
            );
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityTimeBeacon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
