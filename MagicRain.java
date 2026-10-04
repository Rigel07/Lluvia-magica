package com.lluviamagica.rain;

import com.lluviamagica.Config;
import com.lluviamagica.network.MagicRainPacket;
import com.lluviamagica.network.ModNetwork;
import com.lluviamagica.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

/** Logica de la Lluvia Magica (solo en el Overworld). */
public final class MagicRain {
    public static final String MOB_TAG = "lluviamagica_spawned";

    private static boolean wasRaining = false;

    private MagicRain() {}

    public static MagicRainData data(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(MagicRainData::load, MagicRainData::new, "lluviamagica");
    }

    public static boolean isActive(ServerLevel overworld) {
        return data(overworld).ticksLeft > 0;
    }

    public static int ticksLeft(ServerLevel overworld) {
        return data(overworld).ticksLeft;
    }

    // ------------------------------------------------------------ Inicio / fin

    public static void start(ServerLevel overworld, int ticks) {
        MagicRainData data = data(overworld);
        data.ticksLeft = ticks;
        data.setDirty();
        // Sin lluvia de agua mientras dure la magica
        overworld.setWeatherParameters(ticks + 200, 0, false, false);
        MobPicker.rebuild();
        broadcast(true);
        for (ServerPlayer player : overworld.players()) {
            player.displayClientMessage(Component.translatable("message.lluviamagica.started"), true);
            overworld.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.WEATHER, 1.0F, 1.2F);
        }
    }

    public static void stop(ServerLevel overworld) {
        MagicRainData data = data(overworld);
        data.ticksLeft = 0;
        data.setDirty();
        broadcast(false);
        for (ServerPlayer player : overworld.players()) {
            player.displayClientMessage(Component.translatable("message.lluviamagica.ended"), true);
        }
        if (Config.DESPAWN_AFTER.get()) {
            despawnRainMobs(overworld);
        }
    }

    private static void broadcast(boolean active) {
        ModNetwork.CHANNEL.send(PacketDistributor.DIMENSION.with(() -> Level.OVERWORLD), new MagicRainPacket(active));
    }

    /** Sincroniza a un jugador que entra o cambia de dimension. */
    public static void syncTo(ServerPlayer player) {
        if (player.getServer() == null) {
            return;
        }
        boolean active = isActive(player.getServer().overworld());
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MagicRainPacket(active));
    }

    // ------------------------------------------------------------------- Tick

    public static void tick(ServerLevel level) {
        MagicRainData data = data(level);
        long time = level.getGameTime();
        List<ServerPlayer> players = new ArrayList<>();
        for (ServerPlayer player : level.players()) {
            if (!player.isSpectator()) {
                players.add(player);
            }
        }

        if (data.ticksLeft > 0) {
            data.ticksLeft--;
            if (data.ticksLeft <= 0) {
                stop(level);
                return;
            }
            if (time % 100 == 0) {
                data.setDirty();
            }
            if (level.isRaining() || level.isThundering()) {
                level.setWeatherParameters(data.ticksLeft + 200, 0, false, false);
            }
            if (time % Config.CANDY_INTERVAL.get() == 0) {
                for (ServerPlayer player : players) {
                    dropCandy(level, player);
                }
            }
            if (Config.SPAWN_MOBS.get() && time % Config.MOB_INTERVAL.get() == 0) {
                for (ServerPlayer player : players) {
                    trySpawnMob(level, player);
                }
            }
            return;
        }

        // Inactiva: ¿empieza de golpe?
        boolean raining = level.isRaining();
        if (raining && !wasRaining && roll(level.random, Config.CONVERT_RAIN_PERCENT.get())) {
            start(level, Config.DURATION_SECONDS.get() * 20);
        } else if (time % 1200 == 0 && !players.isEmpty() && roll(level.random, Config.RANDOM_START_PERCENT.get())) {
            start(level, Config.DURATION_SECONDS.get() * 20);
        }
        wasRaining = level.isRaining();
    }

    private static boolean roll(RandomSource random, double percent) {
        return percent > 0.0D && random.nextDouble() * 100.0D < percent;
    }

    // -------------------------------------------------------------- Golosinas

    private static void dropCandy(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        BlockPos center = player.blockPosition();
        AABB box = new AABB(center).inflate(24.0D);
        int near = level.getEntitiesOfClass(ItemEntity.class, box, e -> ModItems.isRainCandy(e.getItem())).size();
        if (near >= Config.MAX_CANDY_NEAR.get()) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            int x = center.getX() + random.nextInt(33) - 16;
            int z = center.getZ() + random.nextInt(33) - 16;
            BlockPos probe = new BlockPos(x, center.getY(), z);
            if (!level.hasChunkAt(probe)) {
                continue;
            }
            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);
            double y = Math.min(top.getY() + 10 + random.nextInt(6), level.getMaxBuildHeight() - 2);
            ItemEntity drop = new ItemEntity(level, x + 0.5D, y, z + 0.5D,
                    new ItemStack(ModItems.randomCandy(random)));
            drop.setDeltaMovement(0.0D, 0.0D, 0.0D);
            level.addFreshEntity(drop);
        }
    }

    // ------------------------------------------------------------------- Mobs

    private static void trySpawnMob(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        if (!roll(random, Config.MOB_CHANCE_PERCENT.get())) {
            return;
        }
        AABB box = player.getBoundingBox().inflate(48.0D);
        int near = level.getEntitiesOfClass(Mob.class, box, m -> m.getPersistentData().getBoolean(MOB_TAG)).size();
        if (near >= Config.MAX_MOBS_NEAR.get()) {
            return;
        }
        EntityType<?> type = MobPicker.pick(random);
        if (type == null) {
            return;
        }

        double angle = random.nextDouble() * Math.PI * 2.0D;
        double distance = 10.0D + random.nextInt(18);
        int x = player.getBlockX() + (int) Math.round(Math.cos(angle) * distance);
        int z = player.getBlockZ() + (int) Math.round(Math.sin(angle) * distance);
        BlockPos probe = new BlockPos(x, player.getBlockY(), z);
        if (!level.hasChunkAt(probe)) {
            return;
        }
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, probe);
        BlockPos ground = top.below();
        if (!level.canSeeSky(top)
                || !level.getFluidState(ground).isEmpty()
                || !level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)) {
            return;
        }

        Entity entity;
        try {
            entity = type.create(level);
        } catch (Throwable error) {
            MobPicker.reject(type);
            return;
        }
        if (!(entity instanceof Mob mob)) {
            MobPicker.reject(type);
            if (entity != null) {
                entity.discard();
            }
            return;
        }

        mob.moveTo(x + 0.5D, top.getY(), z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        if (!mob.checkSpawnObstruction(level)) {
            mob.discard();
            return;
        }
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(top), MobSpawnType.EVENT, null, null);
        mob.getPersistentData().putBoolean(MOB_TAG, true);
        level.addFreshEntityWithPassengers(mob);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, mob.getX(), mob.getY() + 0.8D, mob.getZ(),
                12, 0.4D, 0.5D, 0.4D, 0.0D);
    }

    private static void despawnRainMobs(ServerLevel level) {
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof Mob mob
                    && mob.getPersistentData().getBoolean(MOB_TAG)
                    && !mob.hasCustomName()
                    && !mob.isLeashed()
                    && !(mob instanceof TamableAnimal tamable && tamable.isTame())) {
                toRemove.add(mob);
            }
        }
        for (Entity entity : toRemove) {
            level.sendParticles(ParticleTypes.POOF, entity.getX(), entity.getY() + 0.5D, entity.getZ(),
                    8, 0.3D, 0.3D, 0.3D, 0.02D);
            entity.discard();
        }
    }
}
