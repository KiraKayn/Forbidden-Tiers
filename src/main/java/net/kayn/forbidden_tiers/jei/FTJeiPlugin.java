package net.kayn.forbidden_tiers.jei;

import io.redspace.ironsspellbooks.jei.AlchemistCauldronJeiRecipe;
import io.redspace.ironsspellbooks.jei.AlchemistCauldronRecipeCategory;
import io.redspace.ironsspellbooks.registries.FluidRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.kayn.forbidden_tiers.ForbiddenTiers;
import net.kayn.forbidden_tiers.registries.FTFluidRegistry;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

@JeiPlugin
public class FTJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(ForbiddenTiers.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        AlchemistCauldronJeiRecipe legendaryToMythic = new AlchemistCauldronJeiRecipe(
                Ingredient.of(new ItemStack(net.minecraft.world.item.Items.NETHERITE_SCRAP)),
                new FluidStack(FluidRegistry.LEGENDARY_INK.get(), 1000),
                List.of(new FluidStack(FTFluidRegistry.MYTHIC_INK.get(), 250)),
                ItemStack.EMPTY
        );

        AlchemistCauldronJeiRecipe mythicToAncient = new AlchemistCauldronJeiRecipe(
                Ingredient.of(new ItemStack(ItemRegistry.MITHRIL_SCRAP.get())),
                new FluidStack(FTFluidRegistry.MYTHIC_INK.get(), 1000),
                List.of(new FluidStack(FTFluidRegistry.ANCIENT_INK.get(), 250)),
                ItemStack.EMPTY
        );

        registration.addRecipes(
                AlchemistCauldronRecipeCategory.ALCHEMIST_CAULDRON_RECIPE_TYPE,
                List.of(legendaryToMythic, mythicToAncient)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

    }
}