package net.kayn.forbidden_tiers.util;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ForbiddenTiersTags {
    public static final ResourceKey<Registry<AbstractSpell>> SPELL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("irons_spellbooks", "spells"));

    public static final TagKey<AbstractSpell> MYTHIC_BLACKLIST = TagKey.create(
            SPELL_REGISTRY_KEY,
            new ResourceLocation("forbidden_tiers", "mythic_blacklist"));

    public static final TagKey<AbstractSpell> ANCIENT_BLACKLIST = TagKey.create(
            SPELL_REGISTRY_KEY,
            new ResourceLocation("forbidden_tiers", "ancient_blacklist"));
}