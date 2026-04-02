package net.kayn.forbidden_tiers.util;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.kayn.forbidden_tiers.color.RainbowColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SpellRarityExtender {

    public static final Logger LOGGER = LoggerFactory.getLogger("ForbiddenTiers/Extender");

    public static SpellRarity MYTHIC  = null;
    public static SpellRarity ANCIENT = null;

    public static final int MYTHIC_VALUE  = 5;
    public static final int ANCIENT_VALUE = 6;

    public static final int MYTHIC_COLOR = 0xCC0000;

    public static void extendAfterClassInit() {
        try {
            Unsafe unsafe = getUnsafe();

            MYTHIC  = createConstant(unsafe, "MYTHIC",  5, MYTHIC_VALUE);
            ANCIENT = createConstant(unsafe, "ANCIENT", 6, ANCIENT_VALUE);

            initMythicDisplay(unsafe, MYTHIC);
            initAncientDisplay(unsafe, ANCIENT);

            appendToValues(unsafe, MYTHIC, ANCIENT);
            clearEnumCaches(unsafe);
            rebuildCodec(unsafe);

            LOGGER.info("Successfully added MYTHIC({}) and ANCIENT({}) to SpellRarity",
                    MYTHIC_VALUE, ANCIENT_VALUE);
        } catch (Throwable t) {
            LOGGER.error("Failed to extend SpellRarity — Forbidden Tiers will not work!", t);
        }
    }

    private static void initMythicDisplay(Unsafe unsafe, SpellRarity target) throws Exception {
        Field displaysField = SpellRarity.class.getDeclaredField("DISPLAYS");
        long offset = unsafe.objectFieldOffset(displaysField);

        MutableComponent[] base   = (MutableComponent[]) unsafe.getObject(SpellRarity.COMMON, offset);
        MutableComponent[] cloned = base.clone();
        cloned[MYTHIC_VALUE] = Component.translatable("rarity.irons_spellbooks.mythic")
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(MYTHIC_COLOR)));

        unsafe.putObject(target, offset, cloned);
    }

    private static void initAncientDisplay(Unsafe unsafe, SpellRarity target) throws Exception {
        Field displaysField = SpellRarity.class.getDeclaredField("DISPLAYS");
        long offset = unsafe.objectFieldOffset(displaysField);

        MutableComponent[] base   = (MutableComponent[]) unsafe.getObject(SpellRarity.COMMON, offset);
        MutableComponent[] cloned = base.clone();

        cloned[ANCIENT_VALUE] = Component.translatable("rarity.irons_spellbooks.ancient")
                .withStyle(Style.EMPTY.withColor(RainbowColor.INSTANCE));

        unsafe.putObject(target, offset, cloned);
    }

    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

    private static SpellRarity createConstant(Unsafe unsafe, String name, int ordinal, int value)
            throws Exception {
        SpellRarity instance = (SpellRarity) unsafe.allocateInstance(SpellRarity.class);

        Field nameField = Enum.class.getDeclaredField("name");
        unsafe.putObject(instance, unsafe.objectFieldOffset(nameField), name);

        Field ordinalField = Enum.class.getDeclaredField("ordinal");
        unsafe.putInt(instance, unsafe.objectFieldOffset(ordinalField), ordinal);

        Field valueField = SpellRarity.class.getDeclaredField("value");
        unsafe.putInt(instance, unsafe.objectFieldOffset(valueField), value);

        return instance;
    }

    private static void appendToValues(Unsafe unsafe, SpellRarity... toAdd) throws Exception {
        Field valuesField = SpellRarity.class.getDeclaredField("$VALUES");
        long  offset      = unsafe.staticFieldOffset(valuesField);
        Object base       = unsafe.staticFieldBase(valuesField);

        SpellRarity[] current  = (SpellRarity[]) unsafe.getObject(base, offset);
        SpellRarity[] extended = Arrays.copyOf(current, current.length + toAdd.length);
        System.arraycopy(toAdd, 0, extended, current.length, toAdd.length);
        unsafe.putObject(base, offset, extended);
    }

    private static void clearEnumCaches(Unsafe unsafe) {
        for (String fieldName : new String[]{"enumConstantDirectory", "enumConstants"}) {
            try {
                Field f = Class.class.getDeclaredField(fieldName);
                unsafe.putObject(SpellRarity.class,
                        unsafe.objectFieldOffset(f), null);
            } catch (Throwable ignored) {}
        }
    }

    private static void rebuildCodec(Unsafe unsafe) throws Exception {
        Codec<SpellRarity> newCodec = StringRepresentable.fromEnum(SpellRarity::values);
        Field codecField = SpellRarity.class.getDeclaredField("CODEC");
        long  offset     = unsafe.staticFieldOffset(codecField);
        Object base      = unsafe.staticFieldBase(codecField);
        unsafe.putObject(base, offset, newCodec);
    }
}