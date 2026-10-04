package com.lluviamagica.rain;

import com.lluviamagica.Config;
import com.lluviamagica.LluviaMagicaMod;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/** Elige que mob aparece: por defecto, cualquiera de otros mods. */
public final class MobPicker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<EntityType<?>> CANDIDATES = new ArrayList<>();
    private static boolean built = false;

    private MobPicker() {}

    public static void rebuild() {
        CANDIDATES.clear();
        List<? extends String> whitelist = Config.WHITELIST.get();
        List<? extends String> blacklist = Config.BLACKLIST.get();
        boolean useWhitelist = !whitelist.isEmpty();

        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (id == null || id.getNamespace().equals(LluviaMagicaMod.MODID)) {
                continue;
            }
            if (!type.canSummon() || type == EntityType.PLAYER
                    || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER) {
                continue;
            }
            MobCategory category = type.getCategory();
            if (category == MobCategory.MISC
                    || category == MobCategory.WATER_CREATURE
                    || category == MobCategory.WATER_AMBIENT
                    || category == MobCategory.UNDERGROUND_WATER_CREATURE
                    || category == MobCategory.AXOLOTLS) {
                continue;
            }
            if (matchesAny(blacklist, id)) {
                continue;
            }
            if (useWhitelist) {
                if (!matchesAny(whitelist, id)) {
                    continue;
                }
            } else {
                if (id.getNamespace().equals("minecraft") && !Config.INCLUDE_VANILLA.get()) {
                    continue;
                }
                if (category == MobCategory.MONSTER && !Config.INCLUDE_MONSTERS.get()) {
                    continue;
                }
            }
            CANDIDATES.add(type);
        }
        built = true;
        LOGGER.info("[Lluvia Magica] {} tipos de mob pueden aparecer en la lluvia.", CANDIDATES.size());
    }

    private static boolean matchesAny(List<? extends String> patterns, ResourceLocation id) {
        for (String pattern : patterns) {
            if (pattern.equals(id.toString())) {
                return true;
            }
            if (pattern.endsWith(":*") && pattern.substring(0, pattern.length() - 2).equals(id.getNamespace())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static EntityType<?> pick(RandomSource random) {
        if (!built) {
            rebuild();
        }
        if (CANDIDATES.isEmpty()) {
            return null;
        }
        return CANDIDATES.get(random.nextInt(CANDIDATES.size()));
    }

    /** Quita un tipo que resulto no servir (no es un Mob o fallo al crearlo). */
    public static void reject(EntityType<?> type) {
        CANDIDATES.remove(type);
    }
}
