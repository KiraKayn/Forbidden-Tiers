package net.kayn.forbidden_tiers.event;

import io.redspace.ironsspellbooks.item.InkItem;
import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ForbiddenTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FTWanderingTradeEvents {

    private static final int INK_BUY_PRICE_PER_RARITY = 5;
    private static final int INK_SALE_PRICE_PER_RARITY = 8;

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
        return (trader, random) -> new MerchantOffer(
                new ItemStack(ink, 1),
                new ItemStack(Items.EMERALD,
                        INK_BUY_PRICE_PER_RARITY * ink.getRarity().getValue()
                                + random.nextIntBetweenInclusive(2, 3)),
                8, 1, .05f
        );
    }

    private static VillagerTrades.ItemListing inkSellTrade(InkItem ink) {
        return (trader, random) -> new MerchantOffer(
                new ItemStack(Items.EMERALD,
                        INK_SALE_PRICE_PER_RARITY * ink.getRarity().getValue()
                                + random.nextIntBetweenInclusive(2, 3)),
                new ItemStack(ink),
                4, 1, .05f
        );
    }
}