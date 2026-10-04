package com.lluviamagica.client;

import net.minecraft.util.Mth;

/** Estado de la Lluvia Magica en el cliente. */
public final class ClientRainState {
    private static boolean active = false;
    private static float intensity = 0.0F;

    private ClientRainState() {}

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active;
    }

    /** 0..1, sube y baja suavemente para tintar el cielo. */
    public static float getIntensity() {
        return intensity;
    }

    public static void tickIntensity(boolean raining) {
        intensity = Mth.clamp(intensity + (raining ? 0.02F : -0.02F), 0.0F, 1.0F);
    }
}
