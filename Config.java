package com.lluviamagica;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ---- Lluvia ----
    public static final ForgeConfigSpec.IntValue DURATION_SECONDS = BUILDER
            .comment("Duracion de la Lluvia Magica (segundos) cuando se inicia con la varita o con /lluviamagica start.")
            .defineInRange("durationSeconds", 180, 10, 36000);

    public static final ForgeConfigSpec.DoubleValue RANDOM_START_PERCENT = BUILDER
            .comment("Probabilidad (%) por minuto de que la Lluvia Magica empiece 'de golpe' sola en el Overworld. 0 = nunca.")
            .defineInRange("randomStartPercentPerMinute", 2.0D, 0.0D, 100.0D);

    public static final ForgeConfigSpec.DoubleValue CONVERT_RAIN_PERCENT = BUILDER
            .comment("Probabilidad (%) de que una lluvia normal se convierta en Lluvia Magica al empezar. 0 = nunca.")
            .defineInRange("convertNormalRainPercent", 10.0D, 0.0D, 100.0D);

    // ---- Golosinas ----
    public static final ForgeConfigSpec.IntValue CANDY_INTERVAL = BUILDER
            .comment("Cada cuantos ticks cae un par de golosinas (items) cerca de cada jugador. 20 ticks = 1 segundo.")
            .defineInRange("candyDropIntervalTicks", 20, 5, 200);

    public static final ForgeConfigSpec.IntValue MAX_CANDY_NEAR = BUILDER
            .comment("Maximo de golosinas (items) en el suelo alrededor de un jugador. Evita lag.")
            .defineInRange("maxCandyItemsNearPlayer", 40, 0, 500);

    // ---- Mobs ----
    public static final ForgeConfigSpec.BooleanValue SPAWN_MOBS = BUILDER
            .comment("Si aparecen mobs durante la Lluvia Magica.")
            .define("spawnMobs", true);

    public static final ForgeConfigSpec.IntValue MOB_INTERVAL = BUILDER
            .comment("Cada cuantos ticks se intenta aparecer un mob cerca de cada jugador.")
            .defineInRange("mobIntervalTicks", 60, 20, 1200);

    public static final ForgeConfigSpec.DoubleValue MOB_CHANCE_PERCENT = BUILDER
            .comment("Probabilidad (%) de que el intento de aparicion funcione.")
            .defineInRange("mobSpawnChancePercent", 35.0D, 0.0D, 100.0D);

    public static final ForgeConfigSpec.IntValue MAX_MOBS_NEAR = BUILDER
            .comment("Maximo de mobs de la lluvia vivos alrededor de un jugador.")
            .defineInRange("maxRainMobsNearPlayer", 6, 1, 100);

    public static final ForgeConfigSpec.BooleanValue INCLUDE_MONSTERS = BUILDER
            .comment("Permitir tambien mobs hostiles (categoria MONSTER) de otros mods.")
            .define("includeMonsters", false);

    public static final ForgeConfigSpec.BooleanValue INCLUDE_VANILLA = BUILDER
            .comment("Permitir tambien mobs de Minecraft vanilla (util para probar el mod sin otros mods).")
            .define("includeVanillaMobs", false);

    public static final ForgeConfigSpec.BooleanValue DESPAWN_AFTER = BUILDER
            .comment("Al terminar la lluvia, los mobs que aparecieron desaparecen (salvo los que tengan nombre, esten domesticados o atados).")
            .define("despawnMobsWhenRainEnds", true);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST = BUILDER
            .comment("Si NO esta vacia, solo aparecen estos mobs. Formato: \"modid:mob\" o \"modid:*\" para todo un mod.")
            .defineListAllowEmpty("whitelist", List.of(), o -> o instanceof String);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("Mobs que nunca aparecen. Mismo formato que la whitelist.")
            .defineListAllowEmpty("blacklist", List.of(), o -> o instanceof String);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
