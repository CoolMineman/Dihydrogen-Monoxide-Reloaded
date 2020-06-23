package io.github.CoolMineman.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.CoolMineman.FlowWater;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.fluid.FlowableFluid.class)
public class FlowingMixin {
    @Inject(at = @At("HEAD"), method = "canFlowThrough", cancellable = true)
    private void canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState, CallbackInfoReturnable<Boolean> bruh) {
        if (fluid instanceof WaterFluid) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canFlow", cancellable = true)
    private void canFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> bruh) {
        if (fluid instanceof WaterFluid) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryFlow", cancellable = true)
    private void tryFlow(WorldAccess world, BlockPos fluidPos, FluidState state, CallbackInfo bruh) {
        if ((state.getFluid() instanceof WaterFluid.Flowing) || (state.getFluid() instanceof WaterFluid.Still)) {
            FlowWater.flowwater(world, fluidPos, state);
            bruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getUpdatedState", cancellable = true)
    private void getUpdatedState(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> bruh) {
        FluidState fluidstate = state.getFluidState();
        if (fluidstate.getFluid() instanceof WaterFluid.Flowing) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(state.getFluidState().getLevel(), false));
        }
    }
}