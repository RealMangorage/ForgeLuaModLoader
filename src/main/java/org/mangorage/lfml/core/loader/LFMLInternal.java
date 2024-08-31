package org.mangorage.lfml.core.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mangorage.lfml.core.LFMLUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LFMLInternal {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Path LUA_MODS = Path.of("luamods");
    private static final Path LUA_MODS_INFO = Path.of("mods.json");
    private static final Gson GSON = new GsonBuilder().create();

    private static final List<ModInstance> MODS = new ArrayList<>();

    static void preLoad(IEventBus bus) throws LuaModLoadingException {
        var coreDir = FMLPaths.MODSDIR;
        var luaModsDir = coreDir.get().resolve(LUA_MODS);

        if (!Files.exists(luaModsDir.toAbsolutePath())) return;

        var mods = LFMLUtils.traverseFolder(luaModsDir, 1);

        mods.forEach(modDir -> {
            try {
                var modInfoFile = modDir.resolve(LUA_MODS_INFO);
                if (!Files.exists(modInfoFile.toAbsolutePath())) return;
                var modInfo = GSON.fromJson(
                        new FileReader(modInfoFile.toFile()),
                        ModInfo.class
                );

                LOGGER.info("Found and Loading Lua Mod with modId {} with version {}", modInfo.modId(), modInfo.version());
                MODS.add(
                        new ModInstance(modDir, modInfo, bus)
                );
            } catch (LuaModLoadingException | FileNotFoundException throwable) {
                throw new LuaModLoadingException(throwable);
            }
        });
    }
}
