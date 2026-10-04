package com.lluviamagica.client;

import com.lluviamagica.LluviaMagicaMod;
import com.lluviamagica.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LluviaMagicaMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        boolean raining = ClientRainState.isActive()
                && level != null
                && player != null
                && level.dimension() == Level.OVERWORLD;

        ClientRainState.tickIntensity(raining);
        if (!raining || mc.isPaused()) {
            return;
        }

        ParticleStatus status = mc.options.particles().get();
        int count = status == ParticleStatus.ALL ? 14 : (status == ParticleStatus.DECREASED ? 6 : 0);
        RandomSource random = level.random;

        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5D) * 40.0D;
            double z = player.getZ() + (random.nextDouble() - 0.5D) * 40.0D;
            BlockPos probe = BlockPos.containing(x, player.getY(), z);
            if (!level.hasChunkAt(probe)) {
                continue;
            }
            int top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe).getY();
            double y = top + 3.0D + random.nextDouble() * 10.0D;
            level.addParticle(random.nextInt(3) == 0 ? ModParticles.GUMMY.get() : ModParticles.COTTON.get(),
                    x, y, z, 0.0D, 0.0D, 0.0D);
        }

        if (random.nextInt(50) == 0) {
            level.playLocalSound(player.getX(), player.getY() + 8.0D, player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.WEATHER,
                    0.25F, 1.4F + random.nextFloat() * 0.6F, false);
        }
    }

    /** Cielo y niebla con un tinte rosa mientras llueve magia. */
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        float intensity = ClientRainState.getIntensity();
        if (intensity <= 0.0F) {
            return;
        }
        float amount = intensity * 0.55F;
        event.setRed(Mth.lerp(amount, event.getRed(), 1.00F));
        event.setGreen(Mth.lerp(amount, event.getGreen(), 0.72F));
        event.setBlue(Mth.lerp(amount, event.getBlue(), 0.92F));
    }
}
