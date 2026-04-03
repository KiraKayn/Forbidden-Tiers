package net.kayn.forbidden_tiers.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class MythicFluidClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL =
            new ResourceLocation("forbidden_tiers", "block/mythic_ink_still");
    private static final ResourceLocation FLOW =
            new ResourceLocation("forbidden_tiers", "block/mythic_ink_flow");

    @Override
    public ResourceLocation getStillTexture() { return STILL; }

    @Override
    public ResourceLocation getFlowingTexture() { return FLOW; }

    @Override
    public int getTintColor() { return 0xFF8B0000; }
}