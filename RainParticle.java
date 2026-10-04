package com.lluviamagica.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/** Particula que cae como la lluvia: puffs de algodon de azucar y gominolas de colores. */
public class RainParticle extends TextureSheetParticle {
    private static final float[][] COTTON_COLORS = {
            {1.00F, 0.62F, 0.80F}, {0.62F, 0.80F, 1.00F}, {1.00F, 1.00F, 1.00F}, {0.85F, 0.70F, 1.00F}
    };
    private static final float[][] GUMMY_COLORS = {
            {1.00F, 0.25F, 0.30F}, {0.30F, 0.90F, 0.35F}, {1.00F, 0.90F, 0.25F},
            {0.70F, 0.35F, 1.00F}, {1.00F, 0.55F, 0.15F}
    };

    protected RainParticle(ClientLevel level, double x, double y, double z,
                           SpriteSet sprites, float[][] palette, float size) {
        super(level, x, y, z);
        this.pickSprite(sprites);
        float[] color = palette[this.random.nextInt(palette.length)];
        this.setColor(color[0], color[1], color[2]);
        this.quadSize = size * (0.8F + this.random.nextFloat() * 0.5F);
        this.lifetime = 80;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.hasPhysics = true;
        this.xd = (this.random.nextDouble() - 0.5D) * 0.02D;
        this.yd = -(0.30D + this.random.nextDouble() * 0.15D);
        this.zd = (this.random.nextDouble() - 0.5D) * 0.02D;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880; // brillan, aunque sea de noche
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class CottonProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public CottonProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xd, double yd, double zd) {
            return new RainParticle(level, x, y, z, sprites, COTTON_COLORS, 0.16F);
        }
    }

    public static class GummyProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public GummyProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xd, double yd, double zd) {
            return new RainParticle(level, x, y, z, sprites, GUMMY_COLORS, 0.12F);
        }
    }
}
