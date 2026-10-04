package com.lluviamagica.network;

import com.lluviamagica.client.ClientRainState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Avisa a los clientes de si la Lluvia Magica esta activa. */
public record MagicRainPacket(boolean active) {

    public static void encode(MagicRainPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.active());
    }

    public static MagicRainPacket decode(FriendlyByteBuf buf) {
        return new MagicRainPacket(buf.readBoolean());
    }

    public static void handle(MagicRainPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientRainState.setActive(packet.active())));
        context.get().setPacketHandled(true);
    }
}
