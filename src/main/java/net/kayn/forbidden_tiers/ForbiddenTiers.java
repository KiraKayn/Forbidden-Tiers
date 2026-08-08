package net.kayn.forbidden_tiers;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.kayn.forbidden_tiers.registries.FTFluidRegistry;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(ForbiddenTiers.MOD_ID)
public class ForbiddenTiers {

    public static final String MOD_ID = "forbidden_tiers";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean ironSpellbooksLoaded = false;

    public ForbiddenTiers(IEventBus modBus) {
        ironSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks");

        SpellRarity.values();

        FTFluidRegistry.register(modBus);
        FTItemRegistry.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Forbidden Tiers initialized. ISS loaded: {}", ironSpellbooksLoaded);
    }
}