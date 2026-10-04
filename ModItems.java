package com.lluviamagica.registry;

import com.lluviamagica.LluviaMagicaMod;
import com.lluviamagica.item.MagicRainWandItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LluviaMagicaMod.MODID);

    /** Todas las golosinas que caen del cielo. */
    private static final List<RegistryObject<Item>> RAIN_CANDIES = new ArrayList<>();

    public static final RegistryObject<Item> COTTON_CANDY_PINK = candy("cotton_candy_pink", 3, 0.2F,
            () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 0));
    public static final RegistryObject<Item> COTTON_CANDY_BLUE = candy("cotton_candy_blue", 3, 0.2F,
            () -> new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 0));
    public static final RegistryObject<Item> GUMMY_RED = candy("gummy_red", 2, 0.3F,
            () -> new MobEffectInstance(MobEffects.REGENERATION, 80, 0));
    public static final RegistryObject<Item> GUMMY_GREEN = candy("gummy_green", 2, 0.3F,
            () -> new MobEffectInstance(MobEffects.JUMP, 200, 0));
    public static final RegistryObject<Item> GUMMY_YELLOW = candy("gummy_yellow", 2, 0.3F,
            () -> new MobEffectInstance(MobEffects.LUCK, 600, 0));
    public static final RegistryObject<Item> GUMMY_PURPLE = candy("gummy_purple", 2, 0.3F,
            () -> new MobEffectInstance(MobEffects.ABSORPTION, 200, 0));

    public static final RegistryObject<Item> MAGIC_RAIN_WAND = ITEMS.register("magic_rain_wand",
            () -> new MagicRainWandItem(new Item.Properties().durability(20).rarity(Rarity.RARE)));

    private static RegistryObject<Item> candy(String name, int nutrition, float saturation,
                                              Supplier<MobEffectInstance> effect) {
        FoodProperties food = new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationMod(saturation)
                .alwaysEat()
                .fast()
                .effect(effect, 1.0F)
                .build();
        RegistryObject<Item> object = ITEMS.register(name, () -> new Item(new Item.Properties().food(food)));
        RAIN_CANDIES.add(object);
        return object;
    }

    public static boolean isRainCandy(ItemStack stack) {
        for (RegistryObject<Item> candy : RAIN_CANDIES) {
            if (stack.is(candy.get())) {
                return true;
            }
        }
        return false;
    }

    public static Item randomCandy(net.minecraft.util.RandomSource random) {
        return RAIN_CANDIES.get(random.nextInt(RAIN_CANDIES.size())).get();
    }
}
