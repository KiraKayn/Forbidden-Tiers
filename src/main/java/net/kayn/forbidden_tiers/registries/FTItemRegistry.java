package net.kayn.forbidden_tiers.registries;

import io.redspace.ironsspellbooks.item.InkItem;
import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FTItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ForbiddenTiers.MOD_ID);

    public static final DeferredHolder<Item, Item> INK_MYTHIC = ITEMS.register("mythic_ink", () ->
            new InkItem(SpellRarityExtender.MYTHIC, FTFluidRegistry.MYTHIC_INK, new Item.Properties()));

    public static final DeferredHolder<Item, Item> INK_ANCIENT = ITEMS.register("ancient_ink", () ->
            new InkItem(SpellRarityExtender.ANCIENT, FTFluidRegistry.ANCIENT_INK, new Item.Properties()));

    private FTItemRegistry() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}