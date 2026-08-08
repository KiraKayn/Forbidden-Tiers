package net.kayn.forbidden_tiers.client;

import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.color.RainbowColor;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = ForbiddenTiers.MOD_ID)
public class FTClientEvents {

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> tintIndex == 0
                        ? (0xFF000000 | (RainbowColor.INSTANCE.getValue() & 0xFFFFFF))
                        : 0xFFFFFF,
                FTItemRegistry.INK_ANCIENT.get()
        );
    }
}