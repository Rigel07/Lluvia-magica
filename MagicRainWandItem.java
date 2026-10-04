package com.lluviamagica.item;

import com.lluviamagica.Config;
import com.lluviamagica.rain.MagicRain;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Clic derecho: empieza la Lluvia Magica. Agachado + clic derecho: la para. */
public class MagicRainWandItem extends Item {
    public MagicRainWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            if (level.dimension() != Level.OVERWORLD) {
                player.displayClientMessage(Component.translatable("message.lluviamagica.only_overworld"), true);
                return InteractionResultHolder.fail(stack);
            }
            ServerLevel overworld = serverLevel.getServer().overworld();
            boolean active = MagicRain.isActive(overworld);
            if (player.isShiftKeyDown()) {
                if (active) {
                    MagicRain.stop(overworld);
                }
            } else if (active) {
                player.displayClientMessage(Component.translatable("message.lluviamagica.already_active"), true);
            } else {
                MagicRain.start(overworld, Config.DURATION_SECONDS.get() * 20);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            player.getCooldowns().addCooldown(this, 100);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
