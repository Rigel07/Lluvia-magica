package com.lluviamagica.rain;

import com.lluviamagica.Config;
import com.lluviamagica.LluviaMagicaMod;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LluviaMagicaMod.MODID)
public class ServerEvents {

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.level instanceof ServerLevel level
                && level.dimension() == Level.OVERWORLD) {
            MagicRain.tick(level);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MagicRain.syncTo(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MagicRain.syncTo(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MagicRain.syncTo(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lluviamagica")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .executes(context -> start(context.getSource(), Config.DURATION_SECONDS.get()))
                        .then(Commands.argument("segundos", IntegerArgumentType.integer(5, 36000))
                                .executes(context -> start(context.getSource(),
                                        IntegerArgumentType.getInteger(context, "segundos")))))
                .then(Commands.literal("stop").executes(context -> stop(context.getSource())))
                .then(Commands.literal("status").executes(context -> status(context.getSource()))));
    }

    private static int start(CommandSourceStack source, int seconds) {
        ServerLevel overworld = source.getServer().overworld();
        MagicRain.start(overworld, seconds * 20);
        source.sendSuccess(() -> Component.translatable("command.lluviamagica.start", seconds), true);
        return 1;
    }

    private static int stop(CommandSourceStack source) {
        ServerLevel overworld = source.getServer().overworld();
        if (!MagicRain.isActive(overworld)) {
            source.sendFailure(Component.translatable("command.lluviamagica.not_active"));
            return 0;
        }
        MagicRain.stop(overworld);
        source.sendSuccess(() -> Component.translatable("command.lluviamagica.stop"), true);
        return 1;
    }

    private static int status(CommandSourceStack source) {
        ServerLevel overworld = source.getServer().overworld();
        if (MagicRain.isActive(overworld)) {
            int seconds = MagicRain.ticksLeft(overworld) / 20;
            source.sendSuccess(() -> Component.translatable("command.lluviamagica.status_active", seconds), false);
        } else {
            source.sendSuccess(() -> Component.translatable("command.lluviamagica.status_inactive"), false);
        }
        return 1;
    }
}
