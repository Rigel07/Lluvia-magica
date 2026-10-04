package com.lluviamagica.registry;

import com.lluviamagica.LluviaMagicaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LluviaMagicaMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("lluvia_magica",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lluviamagica"))
                    .icon(() -> new ItemStack(ModItems.COTTON_CANDY_PINK.get()))
                    .displayItems((parameters, output) ->
                            ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());
}
