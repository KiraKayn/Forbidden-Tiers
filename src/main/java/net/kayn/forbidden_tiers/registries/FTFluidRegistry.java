package net.kayn.forbidden_tiers.registries;

import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.client.AncientFluidClientExtensions;
import net.kayn.forbidden_tiers.client.MythicFluidClientExtensions;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FTFluidRegistry {

    private static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, ForbiddenTiers.MOD_ID);

    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ForbiddenTiers.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> MYTHIC_INK_TYPE =
            FLUID_TYPES.register("mythic_ink", () -> new FluidType(FluidType.Properties.create()) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new MythicFluidClientExtensions());
                }
            });

    public static final DeferredHolder<FluidType, FluidType> ANCIENT_INK_TYPE =
            FLUID_TYPES.register("ancient_ink", () -> new FluidType(FluidType.Properties.create()) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new AncientFluidClientExtensions());
                }
            });

    public static final DeferredHolder<Fluid, Fluid> MYTHIC_INK  = registerNoop("mythic_ink",  MYTHIC_INK_TYPE);
    public static final DeferredHolder<Fluid, Fluid> ANCIENT_INK = registerNoop("ancient_ink", ANCIENT_INK_TYPE);

    private static DeferredHolder<Fluid, Fluid> registerNoop(String name, DeferredHolder<FluidType, FluidType> typeRef) {
        List<DeferredHolder<Fluid, Fluid>> selfRef = new ArrayList<>();
        DeferredHolder<Fluid, Fluid> obj = FLUIDS.register(name, () ->
                new BaseFlowingFluid.Source(
                        new BaseFlowingFluid.Properties(
                                typeRef,
                                () -> selfRef.get(0).get(),
                                () -> selfRef.get(0).get()
                        ).bucket(() -> Items.AIR)
                )
        );
        selfRef.add(obj);
        return obj;
    }

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
    }
}