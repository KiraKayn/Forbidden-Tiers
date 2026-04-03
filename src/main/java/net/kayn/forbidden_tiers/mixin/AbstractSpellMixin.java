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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = AbstractSpell.class, remap = false)
public abstract class AbstractSpellMixin {

    private static int levelsPerTier(int originalMaxLevel) {
        return Math.max(1, (int) Math.ceil(originalMaxLevel / 5.0));
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        try {
            int original = self.getDefaultConfig().maxLevel;
            if (original > 0) {
                cir.setReturnValue(original + 2 * levelsPerTier(original));
            }
        } catch (Exception ignored) {}
    }

    @Inject(method = "getMaxRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMaxRarity(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SpellRarityExtender.ANCIENT_VALUE);
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void onGetRarity(int level, CallbackInfoReturnable<SpellRarity> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        int originalMaxLevel;
        try {
            originalMaxLevel = self.getDefaultConfig().maxLevel;
        } catch (Exception e) {
            return;
        }

        int lpt          = levelsPerTier(originalMaxLevel);
        int mythicStart  = originalMaxLevel + 1;
        int ancientStart = originalMaxLevel + lpt + 1;
        int newMax       = originalMaxLevel + 2 * lpt;

        if (level >= newMax)       { cir.setReturnValue(SpellRarityExtender.ANCIENT); return; }
        if (level >= ancientStart) { cir.setReturnValue(SpellRarityExtender.ANCIENT); return; }
        if (level >= mythicStart)  { cir.setReturnValue(SpellRarityExtender.MYTHIC);  return; }
        if (level >= originalMaxLevel) { cir.setReturnValue(SpellRarity.LEGENDARY);   return; }
        if (originalMaxLevel == 1) {
            cir.setReturnValue(SpellRarity.values()[self.getMinRarity()]);
            return;
        }

        double pct         = (double) level / (double) originalMaxLevel;
        List<Double> rawConfig = SpellRarity.getRawRarityConfig();
        int minRarity      = self.getMinRarity();
        int maxRarOrig     = SpellRarity.LEGENDARY.getValue();

        List<Double> cumulative = buildCumulative(rawConfig, minRarity, maxRarOrig);

        int lookupOffset = maxRarOrig + 1 - cumulative.size();
        for (int i = 0; i < cumulative.size(); i++) {
            if (pct <= cumulative.get(i)) {
                cir.setReturnValue(SpellRarity.values()[i + lookupOffset]);
                return;
            }
        }
        cir.setReturnValue(SpellRarity.COMMON);
    }
    @Inject(method = "getMinLevelForRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMinLevelForRarity(SpellRarity rarity,
                                        CallbackInfoReturnable<Integer> cir) {
        AbstractSpell self = (AbstractSpell)(Object)this;
        int originalMaxLevel;
        try {
            originalMaxLevel = self.getDefaultConfig().maxLevel;
        } catch (Exception e) {
            return;
        }

        int lpt       = levelsPerTier(originalMaxLevel);
        int minRarity = self.getMinRarity();
        int rarityVal = rarity.getValue();

        if (rarityVal == SpellRarityExtender.MYTHIC_VALUE) {
            if (minRarity >= SpellRarityExtender.MYTHIC_VALUE) {
                cir.setReturnValue(rarityVal == minRarity ? 1 : 0);
            } else {
                cir.setReturnValue(originalMaxLevel + 1);
            }
            return;
        }
        if (rarityVal == SpellRarityExtender.ANCIENT_VALUE) {
            if (minRarity >= SpellRarityExtender.MYTHIC_VALUE) {
                cir.setReturnValue(rarityVal == minRarity ? 1 : 0);
            } else {
                cir.setReturnValue(originalMaxLevel + lpt + 1);
            }
            return;
        }

        if (rarityVal < minRarity) { cir.setReturnValue(0); return; }
        if (rarityVal == minRarity) { cir.setReturnValue(1); return; }
        if (originalMaxLevel == 1) { cir.setReturnValue(0); return; }

        List<Double> rawConfig = SpellRarity.getRawRarityConfig();
        int maxRarOrig = SpellRarity.LEGENDARY.getValue();
        List<Double> cumulative = buildCumulative(rawConfig, minRarity, maxRarOrig);

        int idx = rarityVal - (1 + minRarity);
        if (idx >= 0 && idx < cumulative.size()) {
            cir.setReturnValue((int)(cumulative.get(idx) * originalMaxLevel) + 1);
        } else {
            cir.setReturnValue(0);
        }
    }
    private static List<Double> buildCumulative(List<Double> rawConfig,
                                                int minRarity, int maxRarOrig) {
        if (minRarity != 0) {
            List<Double> sub      = rawConfig.subList(minRarity, maxRarOrig + 1);
            double       subtotal = sub.stream().mapToDouble(Double::doubleValue).sum();
            List<Double> adjusted = sub.stream()
                    .map(w -> ((w / subtotal) * (1 - subtotal)) + w)
                    .collect(Collectors.toList());
            double counter = 0;
            List<Double> result = new ArrayList<>();
            for (double w : adjusted) { counter += w; result.add(counter); }
            return result;
        }
        return SpellRarity.getRarityConfig();
    }
}