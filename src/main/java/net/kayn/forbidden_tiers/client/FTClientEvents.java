package net.kayn.forbidden_tiers.client;

import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.color.RainbowColor;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD,
        modid = ForbiddenTiers.MOD_ID)
public class FTClientEvents {

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        if (FTItemRegistry.INK_ANCIENT != null && FTItemRegistry.INK_ANCIENT.isPresent()) {
            event.register(
                    (stack, tintIndex) -> tintIndex == 0 ? RainbowColor.INSTANCE.getValue() : 0xFFFFFF,
                    FTItemRegistry.INK_ANCIENT.get()
            );
        }
    }
}