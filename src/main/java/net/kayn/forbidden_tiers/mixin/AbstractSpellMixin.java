package net.kayn.forbidden_tiers.mixin;

import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;

@Mixin(value = AbstractSpell.class, remap = false)
public abstract class AbstractSpellMixin {

    @Inject(method = "getMaxRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMaxRarity(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SpellRarityExtender.ANCIENT_VALUE);
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        try {
            int original = self.getDefaultConfig().maxLevel;
            if (original > 0) {
                cir.setReturnValue((int) Math.ceil(original * 7.0 / 5.0));
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void onGetRarityHead(int level, CallbackInfoReturnable<SpellRarity> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        int maxLevel = self.getMaxLevel();
        if (level >= maxLevel) {
            cir.setReturnValue(SpellRarityExtender.ANCIENT);
        }
    }

    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void onGetRarityReturn(int level, CallbackInfoReturnable<SpellRarity> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        int maxLevel = self.getMaxLevel();
        if (cir.getReturnValue() == SpellRarity.LEGENDARY && level > 0 && maxLevel > 0) {
            double pct = (double) level / (double) maxLevel;
            if (pct >= 6.0 / 7.0) {
                cir.setReturnValue(SpellRarityExtender.ANCIENT);
            } else if (pct >= 5.0 / 7.0) {
                cir.setReturnValue(SpellRarityExtender.MYTHIC);
            }
        }
    }

    @Inject(method = "getMinLevelForRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMinLevelForRarity(SpellRarity rarity,
                                        CallbackInfoReturnable<Integer> cir) {
        if (rarity.getValue() < SpellRarityExtender.MYTHIC_VALUE) return;

        AbstractSpell self = (AbstractSpell)(Object)this;
        int minRar = self.getMinRarity();

        if (minRar >= SpellRarityExtender.MYTHIC_VALUE) {
            cir.setReturnValue(rarity.getValue() == minRar ? 1 : 0);
        } else {
            cir.setReturnValue(self.getMaxLevel());
        }
    }
}