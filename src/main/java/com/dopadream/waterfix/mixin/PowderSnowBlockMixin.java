package com.dopadream.waterfix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin extends Block {

    public PowderSnowBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.2F;
    }
}
