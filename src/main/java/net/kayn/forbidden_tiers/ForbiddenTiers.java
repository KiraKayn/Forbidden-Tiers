package net.kayn.forbidden_tiers;

import com.mojang.logging.LogUtils;
import net.kayn.forbidden_tiers.registries.FTFluidRegistry;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ForbiddenTiers.MOD_ID)
public class ForbiddenTiers {

    public static final String MOD_ID = "forbidden_tiers";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean ironSpellbooksLoaded = false;

    public ForbiddenTiers() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ironSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks");

        FTFluidRegistry.register(modBus);
        FTItemRegistry.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Forbidden Tiers initialized. ISS loaded: {}", ironSpellbooksLoaded);
    }
}