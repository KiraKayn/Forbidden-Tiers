package net.kayn.forbidden_tiers.client;

import net.kayn.forbidden_tiers.color.RainbowColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class AncientFluidClientExtensions implements IClientFluidTypeExtensions {

    private static final ResourceLocation STILL =
            new ResourceLocation("forbidden_tiers", "block/ancient_ink_still");
    private static final ResourceLocation FLOW =
            new ResourceLocation("forbidden_tiers", "block/ancient_ink_flow");

    @Override
    public ResourceLocation getStillTexture() { return STILL; }

    @Override
    public ResourceLocation getFlowingTexture() { return FLOW; }

    @Override
    public int getTintColor() {
        int rgb = RainbowColor.INSTANCE.getValue();

        return 0xFF000000 | (rgb & 0xFFFFFF);
    }
}