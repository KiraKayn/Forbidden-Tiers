package net.kayn.forbidden_tiers.mixin;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SpellRarity.class, remap = false)
public abstract class SpellRarityMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onStaticInit(CallbackInfo ci) {
        SpellRarityExtender.extendAfterClassInit();
    }

    @Inject(method = "getChatFormatting", at = @At("HEAD"), cancellable = true)
    private void onGetChatFormatting(CallbackInfoReturnable<ChatFormatting> cir) {
        if ((Object) this == SpellRarityExtender.MYTHIC) {
            cir.setReturnValue(ChatFormatting.DARK_RED);
        } else if ((Object) this == SpellRarityExtender.ANCIENT) {
            cir.setReturnValue(ChatFormatting.LIGHT_PURPLE);

        }
    }

    @Inject(method = "getRawRarityConfigInternal", at = @At("RETURN"), cancellable = true)
    private static void onGetRawRarityConfig(CallbackInfoReturnable<List<Double>> cir) {
        List<Double> original = cir.getReturnValue();
        if (original != null && original.size() == 5) {
            List<Double> extended = new ArrayList<>();
            for (double w : original) {
                extended.add(w * 5.0 / 7.0);
            }
            extended.add(1.0 / 7.0);
            extended.add(1.0 / 7.0);
            cir.setReturnValue(extended);
        }
    }
}