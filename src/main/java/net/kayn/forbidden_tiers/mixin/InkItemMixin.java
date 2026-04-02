package net.kayn.forbidden_tiers.mixin;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import net.kayn.forbidden_tiers.registries.FTItemRegistry;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InkItem.class, remap = false)
public class InkItemMixin {

    @Inject(method = "getInkForRarity", at = @At("HEAD"), cancellable = true)
    private static void onGetInkForRarity(SpellRarity rarity,
                                          CallbackInfoReturnable<InkItem> cir) {
        if (rarity == SpellRarityExtender.MYTHIC) {
            cir.setReturnValue((InkItem) FTItemRegistry.INK_MYTHIC.get());
        } else if (rarity == SpellRarityExtender.ANCIENT) {
            cir.setReturnValue((InkItem) FTItemRegistry.INK_ANCIENT.get());
        }
    }
}