package com.lluviamagica.client;

import com.lluviamagica.LluviaMagicaMod;
import com.lluviamagica.registry.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LluviaMagicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.COTTON.get(), RainParticle.CottonProvider::new);
        event.registerSpriteSet(ModParticles.GUMMY.get(), RainParticle.GummyProvider::new);
    }
}
