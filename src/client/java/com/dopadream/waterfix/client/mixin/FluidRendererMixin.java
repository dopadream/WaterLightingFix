package com.dopadream.waterfix.client.mixin;

import com.dopadream.waterfix.client.AoFluidVertexConsumer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {

    @ModifyVariable(method = "tesselate", at = @At("HEAD"), argsOnly = true, name = "output")
    private FluidRenderer.Output waterfix$wrapOutput(
            FluidRenderer.Output output,
            @Local(argsOnly = true) BlockAndTintGetter level,
            @Local(argsOnly = true) BlockPos pos) {
        if (Minecraft.getInstance().options.ambientOcclusion().get() && level.getBlockState(pos).getLightEmission() < 10) {
            return layer -> new AoFluidVertexConsumer(output.getBuilder(layer), level, pos);
        }
        return output;
    }
}