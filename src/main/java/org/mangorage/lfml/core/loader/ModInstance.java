package org.mangorage.lfml.core.loader;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.common.extensions.IForgePackResources;
import net.minecraftforge.eventbus.api.IEventBus;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.mangorage.lfml.core.lua.helpers.LuaCore;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class ModInstance {
    private static final Globals GAME;
    private static final LuaTable MODS = new LuaTable();

    private static final Path SCRIPTS_HOME = Path.of("scripts");
    private static final Path RESOURCES_PATH = Path.of("resources");

    static {
        GAME = JsePlatform.standardGlobals();
        GAME.set("mods", MODS);
        GAME.set("core", CoerceJavaToLua.coerce(new LuaCore()));


        GAME.load("""
                _G.import = function(class)
                    return core:JavaToLua(core:getClass(class))
                end
                _G.init = function(mod, module, id)
                    print(module)
                    print(id)
                    mod.mod.modules[id] = module
                end
                """
        ).call();
    }

    public static void init() {}

    public ModInstance(Path modDir, ModInfo modInfo, IEventBus bus) throws LuaModLoadingException {
        try {
            Globals MOD = JsePlatform.standardGlobals();
            MOD.set("game", GAME);

            LuaTable modTable = new LuaTable();
            LuaTable modulesTable = new LuaTable();

            modTable.set("modId", modInfo.modId());
            modTable.set("modBus", CoerceJavaToLua.coerce(bus));
            modTable.set("modules", modulesTable);

            MOD.set("mod", modTable);
            MOD.set("import", GAME.get("import"));
            MOD.set("init", GAME.get("init"));

            MODS.set(modInfo.modId(), MOD);


            String scriptsPath = modDir.resolve(SCRIPTS_HOME).toAbsolutePath().toString().replace("\\", "/") + "/?.lua;";
            MOD.get("package").set("path", scriptsPath);
            String mainScript = modDir.resolve(SCRIPTS_HOME).resolve("main.lua").toString();

            var manager = new LuaScriptManager(MOD, mainScript, bus);
            manager.loadScript();

            // Add the resource pack to the repository
            PackRepository resourcePacks = Minecraft.getInstance().getResourcePackRepository();
            resourcePacks.addPackFinder((consumer) -> {
                Pack pack = Pack.readMetaAndCreate(
                        new PackLocationInfo(
                                modInfo.modId(),
                                Component.literal(
                                        modInfo.modId()
                                ),
                                PackSource.DEFAULT,
                                Optional.of(
                                    KnownPack.vanilla("test")
                                )
                        ),
                        new PathPackResources.PathResourcesSupplier(
                                modDir.resolve(RESOURCES_PATH)
                        ),
                        PackType.CLIENT_RESOURCES,
                        new PackSelectionConfig(true, Pack.Position.TOP, false)
                );
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        } catch (Throwable e) {
            throw new LuaModLoadingException(e);
        }
    }
}
