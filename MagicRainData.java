package com.lluviamagica.rain;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/** Guarda cuanto le queda a la Lluvia Magica (sobrevive a reinicios). */
public class MagicRainData extends SavedData {
    public int ticksLeft = 0;

    public static MagicRainData load(CompoundTag tag) {
        MagicRainData data = new MagicRainData();
        data.ticksLeft = tag.getInt("TicksLeft");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("TicksLeft", this.ticksLeft);
        return tag;
    }
}
