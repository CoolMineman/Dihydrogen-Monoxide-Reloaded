package io.github.CoolMineman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FlowWater {
    public static void flowwater(IWorld world, BlockPos fluidPos, FluidState state) {
        if (world.getBlockState(fluidPos).getBlock() instanceof Waterloggable) {
            return;
        }
        if ((world.getBlockState(fluidPos.down()).canBucketPlace(Fluids.WATER)) && (getWaterLevel(fluidPos.down(), world) != 8)) {
            Integer centerlevel = getWaterLevel(fluidPos, world);
            world.setBlockState(fluidPos, Blocks.AIR.getDefaultState(), 11);
            addWater(centerlevel, fluidPos.down(), world);
        } else {
            BlockPos px = new BlockPos(fluidPos.getX()+1, fluidPos.getY(), fluidPos.getZ());
            BlockPos nx = new BlockPos(fluidPos.getX()-1, fluidPos.getY(), fluidPos.getZ());
            BlockPos pz = new BlockPos(fluidPos.getX(), fluidPos.getY(), fluidPos.getZ()+1);
            BlockPos nz = new BlockPos(fluidPos.getX(), fluidPos.getY(), fluidPos.getZ()-1);
            ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
            blocks.add(px);
            blocks.add(nx);
            blocks.add(nz);
            blocks.add(pz);
            ArrayList<BlockPos> badblocks = new ArrayList<BlockPos>();
            for (BlockPos pos : blocks) {
                if (!(world.getBlockState(pos).canBucketPlace(Fluids.WATER))) {
                    badblocks.add(pos);
                }
            }
            blocks.removeAll(badblocks);
            Collections.shuffle(blocks);
            equalizeWater(blocks, fluidPos, world);
        }
    }

    public static Integer getWaterLevel(BlockPos pos, IWorld world) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = blockstate.getFluidState();
        Integer waterlevel = 0;
        if (fluidstate.getFluid() instanceof WaterFluid.Still){
            waterlevel = 8;
        } else if (fluidstate.getFluid() instanceof WaterFluid.Flowing) {
            waterlevel = fluidstate.getLevel();
        }
        return waterlevel;
    }

    public static void setWaterLevel(Integer level, BlockPos pos, IWorld world) {
        if (level == 8) {
            world.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState(), 11);
        } else if (level == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        } else if (level < 8) {
            world.setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState(), 11);
        } else {
            System.out.println("Can't set water >8 something went very wrong!");
        }
    }

    public static void addWater(Integer level, BlockPos pos, IWorld world) {
        Integer existingwater = getWaterLevel(pos, world);
        Integer totalwater = existingwater + level;
        if (totalwater > 8) {
            setWaterLevel(totalwater - 8, pos.up(), world);
            setWaterLevel(8, pos, world);
        } else {
            setWaterLevel(totalwater, pos, world);
        }
    }

    public static void equalizeWater(ArrayList<BlockPos> blocks, BlockPos center, IWorld world) {
        List<Integer> waterlevels = Arrays.asList(new Integer[4]);
        Integer centerwaterlevel = getWaterLevel(center, world);
        for (BlockPos block : blocks) {
            waterlevels.set(blocks.indexOf(block), getWaterLevel(block, world));
        }

        Integer waterlevelsnum = waterlevels.size();
        Integer didnothings = 0;
        Integer waterlevel;
        while (didnothings < waterlevelsnum) {
            didnothings = 0;
            for (int i = 0; i < 4; i++) {
                waterlevel = waterlevels.get(i);
                if (waterlevel != null) {
                    if ((centerwaterlevel >= (waterlevel + 2))) {
                        waterlevel += 1;
                        waterlevels.set(i, waterlevel);
                        centerwaterlevel -= 1;
                    } else {
                        didnothings += 1;
                    }
                } else {
                    didnothings += 1;
                }
            }
        }
        for (BlockPos block : blocks) {
            Integer newwaterlevel = waterlevels.get(blocks.indexOf(block));
            setWaterLevel(newwaterlevel, block, world);
        }
        setWaterLevel(centerwaterlevel, center, world);
    }
}