package net.kayn.forbidden_tiers.registries;

import io.redspace.ironsspellbooks.item.InkItem;
import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FTItemRegistry {

    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForbiddenTiers.MOD_ID);

    public static final RegistryObject<Item> INK_MYTHIC = ITEMS.register("mythic_ink", () ->
            new InkItem(
                    SpellRarityExtender.MYTHIC,
                    ForgeRegistries.FLUIDS
                            .getHolder(new ResourceLocation(ForbiddenTiers.MOD_ID, "mythic_ink"))
                            .orElseThrow()
            )
    );

    public static final RegistryObject<Item> INK_ANCIENT = ITEMS.register("ancient_ink", () ->
            new InkItem(
                    SpellRarityExtender.ANCIENT,
                    ForgeRegistries.FLUIDS
                            .getHolder(new ResourceLocation(ForbiddenTiers.MOD_ID, "ancient_ink"))
                            .orElseThrow()
            )
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}