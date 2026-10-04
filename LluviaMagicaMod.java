package com.lluviamagica;

import com.lluviamagica.network.ModNetwork;
import com.lluviamagica.registry.ModCreativeTabs;
import com.lluviamagica.registry.ModItems;
import com.lluviamagica.registry.ModParticles;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LluviaMagicaMod.MODID)
public class LluviaMagicaMod {
    public static final String MODID = "lluviamagica";

    public LluviaMagicaMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
