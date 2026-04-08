package net.kayn.forbidden_tiers.mixin;

import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.kayn.forbidden_tiers.util.BlacklistManager;
import net.kayn.forbidden_tiers.util.SpellRarityExtender;
import net.minecraft.resources.ResourceLocation;
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static SpellConfigParameter<Integer> getMaxLevelParam() {
        for (SpellConfigParameter<?> p : SpellConfigManagerAccessor.getAllTypes()) {
            if ("irons_spellbooks:max_level".equals(p.key().toString())) {
                return (SpellConfigParameter<Integer>) p;
            }
        }
        return null;
    }

    private static int getConfiguredMaxLevel(AbstractSpell self) {
        try {
            var param = getMaxLevelParam();
            if (param != null) {
                return SpellConfigManager.getSpellConfigValue(self, param);
            }
        } catch (Exception ignored) {
        }

        try {
            return self.getDefaultConfig().maxLevel;
        } catch (Exception e) {
            return 1;
        }
    }

    private static int levelsPerTier(int configMaxLevel) {
        return Math.max(1, (int) Math.ceil(configMaxLevel / 5.0));
    }

    private static List<Double> buildCumulative(List<Double> rawConfig, int minRarity, int maxRarOrig) {
        if (minRarity >= maxRarOrig) {
            return List.of(1.0);
        }
        if (minRarity > 0) {
            List<Double> sub = rawConfig.subList(minRarity, maxRarOrig + 1);
            double subtotal = sub.stream().mapToDouble(Double::doubleValue).sum();
            if (subtotal <= 0) return List.of(1.0);
            List<Double> adjusted = sub.stream().map(w -> ((w / subtotal) * (1 - subtotal)) + w).collect(Collectors.toList());
            double counter = 0;
            List<Double> result = new ArrayList<>();
            for (double w : adjusted) {
                counter += w;
                result.add(counter);
            }
            return result;
        }
        return SpellRarity.getRarityConfig();
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        AbstractSpell self = (AbstractSpell) (Object) this;
        int base = getConfiguredMaxLevel(self);
        if (base > 0) {
            int lpt = levelsPerTier(base);
            ResourceLocation spellId = self.getSpellResource();
            boolean mb = BlacklistManager.isMythicBlacklisted(spellId);
            boolean ab = BlacklistManager.isAncientBlacklisted(spellId);
            int extra = 2 * lpt;
            if (ab) extra -= lpt;
            if (mb && ab) extra -= lpt;
            cir.setReturnValue(base + extra);
        }
    }

    @Inject(method = "getMaxRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMaxRarity(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SpellRarityExtender.ANCIENT_VALUE);
    }

    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void onGetRarity(int level, CallbackInfoReturnable<SpellRarity> cir) {
        AbstractSpell self = (AbstractSpell) (Object) this;

        int configMaxLevel = getConfiguredMaxLevel(self);
        int configMinRarity = self.getMinRarity();
        if (configMaxLevel <= 0) return;

        int lpt = levelsPerTier(configMaxLevel);
        int mythicStart = configMaxLevel + 1;
        int ancientStart = configMaxLevel + lpt + 1;
        int newMax = configMaxLevel + 2 * lpt;

        ResourceLocation spellId = self.getSpellResource();
        boolean mythicBlacklisted = BlacklistManager.isMythicBlacklisted(spellId);
        boolean ancientBlacklisted = BlacklistManager.isAncientBlacklisted(spellId);

        if (level >= newMax) {
            cir.setReturnValue(SpellRarityExtender.ANCIENT);
            return;
        }
        if (level >= ancientStart) {
            cir.setReturnValue(ancientBlacklisted ? SpellRarity.LEGENDARY : SpellRarityExtender.ANCIENT);
            return;
        }

        if (level >= mythicStart) {
            if (mythicBlacklisted) {
                cir.setReturnValue(ancientBlacklisted ? SpellRarity.LEGENDARY : SpellRarityExtender.ANCIENT);
            } else {
                cir.setReturnValue(configMinRarity >= SpellRarityExtender.ANCIENT_VALUE ? SpellRarityExtender.ANCIENT : SpellRarityExtender.MYTHIC);
            }
            return;
        }

        if (configMinRarity >= SpellRarityExtender.ANCIENT_VALUE) {
            cir.setReturnValue(SpellRarityExtender.ANCIENT);
            return;
        }

        if (configMinRarity >= SpellRarityExtender.MYTHIC_VALUE) {
            cir.setReturnValue(SpellRarityExtender.MYTHIC);
            return;
        }

        if (configMaxLevel == 1) {
            int idx = Math.min(configMinRarity, SpellRarity.LEGENDARY.getValue());
            cir.setReturnValue(SpellRarity.values()[idx]);
            return;
        }

        if (level >= configMaxLevel) {
            cir.setReturnValue(SpellRarity.LEGENDARY);
            return;
        }

        double pct = (double) level / (double) configMaxLevel;
        List<Double> raw = SpellRarity.getRawRarityConfig();
        int maxRarOrig = SpellRarity.LEGENDARY.getValue();
        int effectiveMin = Math.min(configMinRarity, maxRarOrig);

        List<Double> cum = buildCumulative(raw, effectiveMin, maxRarOrig);
        int lookupOffset = maxRarOrig + 1 - cum.size();

        for (int i = 0; i < cum.size(); i++) {
            if (pct <= cum.get(i)) {
                cir.setReturnValue(SpellRarity.values()[i + lookupOffset]);
                return;
            }
        }
        cir.setReturnValue(SpellRarity.LEGENDARY);
    }

    @Inject(method = "getMinLevelForRarity", at = @At("HEAD"), cancellable = true)
    private void onGetMinLevelForRarity(SpellRarity rarity, CallbackInfoReturnable<Integer> cir) {
        AbstractSpell self = (AbstractSpell) (Object) this;

        int configMaxLevel = getConfiguredMaxLevel(self);
        int configMinRarity = self.getMinRarity();
        if (configMaxLevel <= 0) {
            cir.setReturnValue(0);
            return;
        }

        int lpt = levelsPerTier(configMaxLevel);
        int rarityVal = rarity.getValue();

        if (rarityVal == SpellRarityExtender.ANCIENT_VALUE) {
            if (configMinRarity >= SpellRarityExtender.ANCIENT_VALUE) {
                cir.setReturnValue(1);
            } else {
                cir.setReturnValue(configMaxLevel + lpt + 1);
            }
            return;
        }

        if (rarityVal == SpellRarityExtender.MYTHIC_VALUE) {
            if (configMinRarity >= SpellRarityExtender.ANCIENT_VALUE) {
                cir.setReturnValue(0);
            } else if (configMinRarity >= SpellRarityExtender.MYTHIC_VALUE) {
                cir.setReturnValue(1);
            } else {
                cir.setReturnValue(configMaxLevel + 1);
            }
            return;
        }

        if (rarityVal < configMinRarity) {
            cir.setReturnValue(0);
            return;
        }
        if (rarityVal == configMinRarity) {
            cir.setReturnValue(1);
            return;
        }

        if (configMinRarity >= SpellRarityExtender.MYTHIC_VALUE) {
            cir.setReturnValue(0);
            return;
        }

        if (configMaxLevel == 1) {
            cir.setReturnValue(0);
            return;
        }

        List<Double> raw = SpellRarity.getRawRarityConfig();
        int maxRarOrig = SpellRarity.LEGENDARY.getValue();
        int effectiveMin = Math.min(configMinRarity, maxRarOrig);

        if (rarityVal > maxRarOrig) {
            cir.setReturnValue(0);
            return;
        }

        List<Double> cum = buildCumulative(raw, effectiveMin, maxRarOrig);
        int idx = rarityVal - (1 + effectiveMin);

        if (idx >= 0 && idx < cum.size()) {
            cir.setReturnValue((int) (cum.get(idx) * configMaxLevel) + 1);
        } else {
            cir.setReturnValue(0);
        }
    }
}