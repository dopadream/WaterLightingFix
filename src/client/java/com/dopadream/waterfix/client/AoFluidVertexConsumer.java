package com.dopadream.waterfix.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;

public class AoFluidVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final BlockAndLightGetter level;
    private final BlockPos origin;
    private final float ox, oy, oz;
    private float vx, vy, vz;

    public AoFluidVertexConsumer(VertexConsumer delegate, BlockAndLightGetter level, BlockPos origin) {
        this.delegate = delegate;
        this.level = level;
        this.origin = origin;
        this.ox = origin.getX() & 15;
        this.oy = origin.getY() & 15;
        this.oz = origin.getZ() & 15;
    }

    private static int applyAo(int smoothLight, boolean direct, boolean b1, boolean b2, boolean b3, float heightFactor) {
        float sDirect = direct ? 0.2f : 1.0f;
        float s1 = b1 ? 0.2f : 1.0f;
        float s2 = b2 ? 0.2f : 1.0f;
        float sCorner = (b1 && b2) ? s1 : (b3 ? 0.2f : 1.0f);
        float ao = 1.0f;

        if (WaterLightingFixClient.CONFIG.waterAO) {
            ao = (s1 + s2 + sCorner + sDirect) * 0.25f;
            ao = 0.9f - (0.9f - ao) * heightFactor;
        }

        int block = Math.round((smoothLight & 0xFF) * ao);
        int sky = Math.round(((smoothLight >> 16) & 0xFF) * ao);

        return (block & 0xFF) | ((sky & 0xFF) << 16);
    }

    private static int lerpLight(int a, int b, float t) {
        int block = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        int sky = Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        return (block & 0xFF) | ((sky & 0xFF) << 16);
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.vx = x;
        this.vy = y;
        this.vz = z;
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setLight(int packedLight) {
        return delegate.setLight(computeSmoothLight());
    }

    private int fluidLight(BlockPos pos) {
        return LightCoordsUtil.max(
                LightCoordsUtil.getLightCoords(level, pos),
                LightCoordsUtil.getLightCoords(level, pos.above())
        );
    }

    private int computeSmoothLight() {
        float relX = vx - ox;
        float relY = vy - oy;
        float relZ = vz - oz;

        boolean xSideOffset = (relX > 0.0003f && relX < 0.003f) || (relX > 0.997f && relX < 0.9997f);
        boolean zSideOffset = (relZ > 0.0003f && relZ < 0.003f) || (relZ > 0.997f && relZ < 0.9997f);

        if (xSideOffset || zSideOffset) {
            Direction faceDir, hDir;
            if (zSideOffset) {
                faceDir = relZ < 0.5f ? Direction.NORTH : Direction.SOUTH;
                hDir = relX < 0.5f ? Direction.WEST : Direction.EAST;
            } else {
                faceDir = relX < 0.5f ? Direction.WEST : Direction.EAST;
                hDir = relZ < 0.5f ? Direction.NORTH : Direction.SOUTH;
            }
            Direction vDir = relY > 0.5f ? Direction.UP : Direction.DOWN;

            int lAbove = LightCoordsUtil.smoothBlend(fluidLight(origin.relative(hDir).above()), fluidLight(origin.above()), fluidLight(origin.relative(hDir)), fluidLight(origin));
            int lBelow = LightCoordsUtil.smoothBlend(fluidLight(origin.relative(hDir).below()), fluidLight(origin.below()), fluidLight(origin.relative(hDir)), fluidLight(origin));
            int smoothLight = lerpLight(lBelow, lAbove, relY);

            BlockPos faceBase = origin.relative(faceDir);
            boolean direct = isOccluder(faceBase);
            boolean b1 = isOccluder(faceBase.relative(hDir));
            boolean b2 = isOccluder(faceBase.relative(vDir));
            boolean b3 = isOccluder(faceBase.relative(hDir).relative(vDir));

            return applyAo(smoothLight, direct, b1, b2, b3, 1.0f);
        }

        // top or bottom face
        boolean up = relY > 0.125f;

        Direction xDir = relX < 0.5f ? Direction.WEST : Direction.EAST;
        Direction zDir = relZ < 0.5f ? Direction.NORTH : Direction.SOUTH;

        int lCenter = fluidLight(origin);
        int lX = fluidLight(origin.relative(xDir));
        int lZ = fluidLight(origin.relative(zDir));
        int lCorner = fluidLight(origin.relative(xDir).relative(zDir));
        int smoothLight = LightCoordsUtil.smoothBlend(lX, lZ, lCorner, lCenter);

        BlockPos adjBase = up ? origin.above() : origin.below();

        boolean direct = isOccluder(adjBase);
        boolean b1 = isOccluder(adjBase.relative(xDir));
        boolean b2 = isOccluder(adjBase.relative(zDir));
        boolean b3 = isOccluder(adjBase.relative(xDir).relative(zDir));

        // controls AO intensity based on water "height"
        float heightFactor = relY / 0.8889f;

        return applyAo(smoothLight, direct, b1, b2, b3, heightFactor);
    }

    private boolean isOccluder(BlockPos pos) {
        return level.getBlockState(pos).isSolidRender();
    }

    // other shit
    @Override public VertexConsumer setColor(int r, int g, int b, int a) { delegate.setColor(r,g,b,a); return this; }
    @Override public VertexConsumer setColor(int color) { delegate.setColor(color); return this; }
    @Override public VertexConsumer setUv(float u, float v) { delegate.setUv(u,v); return this; }
    @Override public VertexConsumer setUv1(int u, int v) { delegate.setUv1(u,v); return this; }
    @Override public VertexConsumer setUv2(int u, int v) { delegate.setUv2(u,v); return this; }
    @Override public VertexConsumer setNormal(float x, float y, float z) { delegate.setNormal(x,y,z); return this; }
    @Override public VertexConsumer setLineWidth(float width) { delegate.setLineWidth(width); return this; }
}