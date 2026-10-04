package com.lluviamagica.registry;

import com.lluviamagica.LluviaMagicaMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LluviaMagicaMod.MODID);

    public static final RegistryObject<SimpleParticleType> COTTON =
            PARTICLES.register("cotton", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GUMMY =
            PARTICLES.register("gummy", () -> new SimpleParticleType(false));
}
