package net.kayn.forbidden_tiers.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = "forbidden_tiers")
public class BlacklistManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ForbiddenTiers/Blacklist");
    private static final Gson GSON = new Gson();

    private static Set<ResourceLocation> mythicBlacklist = Collections.emptySet();
    private static Set<ResourceLocation> ancientBlacklist = Collections.emptySet();

    public static boolean isMythicBlacklisted(ResourceLocation id) { return mythicBlacklist.contains(id); }
    public static boolean isAncientBlacklisted(ResourceLocation id) { return ancientBlacklist.contains(id); }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void object, ResourceManager manager, ProfilerFiller profiler) {
                mythicBlacklist = loadTagManual(manager, "mythic_blacklist");
                ancientBlacklist = loadTagManual(manager, "ancient_blacklist");

                LOGGER.info("Forbidden Tiers: Loaded {} mythic and {} ancient spells from files.",
                        mythicBlacklist.size(), ancientBlacklist.size());
            }
        });
    }

    private static Set<ResourceLocation> loadTagManual(ResourceManager manager, String fileName) {
        Set<ResourceLocation> ids = new HashSet<>();
        ResourceLocation location = new ResourceLocation("forbidden_tiers", "tags/irons_spellbooks/spells/" + fileName + ".json");

        manager.getResource(location).ifPresent(resource -> {
            try (Reader reader = resource.openAsReader()) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json.has("values")) {
                    JsonArray values = json.getAsJsonArray("values");
                    for (int i = 0; i < values.size(); i++) {
                        String spellId = values.get(i).getAsString();
                        ids.add(new ResourceLocation(spellId));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error reading blacklist file: " + location, e);
            }
        });
        return Collections.unmodifiableSet(ids);
    }
}