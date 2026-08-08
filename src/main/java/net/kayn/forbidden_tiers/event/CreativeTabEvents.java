package net.kayn.forbidden_tiers.event;

import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CreativeTabEvents {

    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == CreativeTabRegistry.MATERIALS_TAB.get()) {
            event.accept(FTItemRegistry.INK_MYTHIC.get());
            event.accept(FTItemRegistry.INK_ANCIENT.get());
        }
    }
}