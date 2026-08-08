package net.kayn.forbidden_tiers.event;

import io.redspace.ironsspellbooks.item.InkItem;
import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

@EventBusSubscriber(
        modid = ForbiddenTiers.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public final class FTWanderingTradeEvents {

    private static final int INK_BUY_PRICE_PER_RARITY = 5;
    private static final int INK_SALE_PRICE_PER_RARITY = 8;

    private FTWanderingTradeEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addWanderingTrades(WandererTradesEvent event) {
        InkItem mythicInk = (InkItem) FTItemRegistry.INK_MYTHIC.get();
        InkItem ancientInk = (InkItem) FTItemRegistry.INK_ANCIENT.get();

        event.getGenericTrades().add(inkBuyTrade(mythicInk));
        event.getGenericTrades().add(inkBuyTrade(ancientInk));

        event.getGenericTrades().add(inkSellTrade(mythicInk));
        event.getGenericTrades().add(inkSellTrade(ancientInk));
    }

    private static VillagerTrades.ItemListing inkBuyTrade(InkItem ink) {
        return (trader, random) -> {
            int emeraldCount =
                    INK_BUY_PRICE_PER_RARITY * ink.getRarity().getValue()
                            + random.nextIntBetweenInclusive(2, 3);

            return new MerchantOffer(
                    new ItemCost(ink, 1),
                    new ItemStack(Items.EMERALD, emeraldCount),
                    8,
                    1,
                    0.05F
            );
        };
    }

    private static VillagerTrades.ItemListing inkSellTrade(InkItem ink) {
        return (trader, random) -> {
            int emeraldCost =
                    INK_SALE_PRICE_PER_RARITY * ink.getRarity().getValue()
                            + random.nextIntBetweenInclusive(2, 3);

            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, emeraldCost),
                    new ItemStack(ink, 1),
                    4,
                    1,
                    0.05F
            );
        };
    }
}