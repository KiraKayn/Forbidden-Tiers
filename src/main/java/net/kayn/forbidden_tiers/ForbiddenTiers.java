package net.kayn.forbidden_tiers;

import com.mojang.logging.LogUtils;
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
        modBus.addListener(this::commonSetup);
        ironSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Forbidden Tiers initialized. Iron's Spellbooks loaded: {}", ironSpellbooksLoaded);
    }
}