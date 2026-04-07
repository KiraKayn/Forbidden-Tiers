package net.kayn.forbidden_tiers.mixin;

import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = SpellConfigManager.class, remap = false)
public interface SpellConfigManagerAccessor {
    @Accessor("ALL_TYPES")
    static Set<SpellConfigParameter<?>> getAllTypes() {
        throw new AssertionError();
    }
}