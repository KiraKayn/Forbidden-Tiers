package net.kayn.forbidden_tiers.registries;

import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.client.AncientFluidClientExtensions;
import net.kayn.forbidden_tiers.client.MythicFluidClientExtensions;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FTFluidRegistry {

    private static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ForbiddenTiers.MOD_ID);

    private static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ForbiddenTiers.MOD_ID);

    public static final RegistryObject<FluidType> MYTHIC_INK_TYPE =
            FLUID_TYPES.register("mythic_ink", () -> new FluidType(FluidType.Properties.create()) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new MythicFluidClientExtensions());
                }
            });

    public static final RegistryObject<FluidType> ANCIENT_INK_TYPE =
            FLUID_TYPES.register("ancient_ink", () -> new FluidType(FluidType.Properties.create()) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new AncientFluidClientExtensions());
                }
            });

    public static final RegistryObject<Fluid> MYTHIC_INK  = registerNoop("mythic_ink",  MYTHIC_INK_TYPE);
    public static final RegistryObject<Fluid> ANCIENT_INK = registerNoop("ancient_ink", ANCIENT_INK_TYPE);

    private static RegistryObject<Fluid> registerNoop(String name, RegistryObject<FluidType> typeRef) {
        List<RegistryObject<Fluid>> selfRef = new ArrayList<>();
        RegistryObject<Fluid> obj = FLUIDS.register(name, () ->
                new ForgeFlowingFluid.Source(
                        new ForgeFlowingFluid.Properties(
                                typeRef::get,
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