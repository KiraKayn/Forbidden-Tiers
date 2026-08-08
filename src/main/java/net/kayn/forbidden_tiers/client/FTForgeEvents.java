package net.kayn.forbidden_tiers.client;

import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.color.RainbowColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME, modid = ForbiddenTiers.MOD_ID)
public class FTForgeEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        RainbowColor.ClientTickHolder.increment();
    }
}