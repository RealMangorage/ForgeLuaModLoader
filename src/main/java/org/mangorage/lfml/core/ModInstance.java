package org.mangorage.lfml.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.mangorage.lfml.core.lua.LuaModCore;

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


        Globals globals = JsePlatform.standardGlobals();

        String scriptsPath = modDir.resolve(SCRIPTS_HOME).toAbsolutePath().toString().replace("\\", "/") + "/?.lua;";
        globals.get("package").set("path", scriptsPath);


        File mainScript = modDir.resolve(SCRIPTS_HOME).resolve("main.lua").toFile();

        LuaTable modInfoTable = new LuaTable();
        modInfoTable.set("modBus", CoerceJavaToLua.coerce(bus));
        globals.set("modId", modInfo.modId());
        globals.set("modInfo", modInfoTable);
        globals.set("modCore", CoerceJavaToLua.coerce(new LuaModCore()));

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
