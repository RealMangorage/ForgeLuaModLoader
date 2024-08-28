package org.mangorage.lfml.core;

import net.minecraftforge.eventbus.api.IEventBus;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.mangorage.lfml.core.lua.LuaHooks;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ModInstance {
    private static final Path SCRIPTS_HOME = Path.of("scripts");

    private final ModInfo modInfo;
    private final IEventBus modBus;

    public ModInfo getModInfo() {
        return modInfo;
    }

    public IEventBus modBus() {
        return modBus;
    }

    public ModInstance(Path modDir, ModInfo modInfo, IEventBus bus) {
        this.modInfo = modInfo;
        this.modBus = bus;

        // Create the Lua environment with standard libraries
        Globals globals = JsePlatform.standardGlobals();

        // Set the Lua package path to include the scripts directory
        String scriptsPath = modDir.resolve(SCRIPTS_HOME).toAbsolutePath().toString().replace("\\", "/") + "/?.lua;";
        globals.get("package").set("path", scriptsPath);

        // Load the main script that may require other scripts
        File mainScript = modDir.resolve(SCRIPTS_HOME).resolve("main.lua").toFile();

        // Creating a Lua table to hold the Java object
        LuaTable table = new LuaTable();
        table.set("hooks", CoerceJavaToLua.coerce(new LuaHooks(this)));
        globals.set("lmflCore", table);


        try (FileReader fileReader = new FileReader(mainScript)) {
            System.out.println("Loading: " + mainScript.getName());
            LuaValue chunk = globals.load(fileReader, mainScript.getName());
            chunk.call(); // Execute the main script
        } catch (IOException e) {
            System.out.println("Error loading file: " + mainScript.getName());
            throw new IllegalStateException(e);
        }
    }
}
