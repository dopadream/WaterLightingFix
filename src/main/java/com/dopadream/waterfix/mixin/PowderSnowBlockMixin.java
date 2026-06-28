package com.dopadream.waterfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class PowderSnowBlockMixin {

    @Inject(method = "getShadeBrightness", at = @At("RETURN"), cancellable = true)
    void watershadefix$overridePowderSnowShade(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (state.is(Blocks.POWDER_SNOW)) {
            cir.setReturnValue(0.2F);
        }
    }
}
