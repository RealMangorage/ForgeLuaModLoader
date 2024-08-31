package org.mangorage.lfml.core.loader;

import net.minecraftforge.eventbus.api.IEventBus;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.nio.file.*;

public class LuaScriptManager {
    private final Globals globals;
    private final Path scriptPath;
    private final IEventBus bus;
    private LuaValue chunk;

    public LuaScriptManager(Globals globals, String scriptPath, IEventBus modBus) {
        this.globals = globals;
        this.scriptPath = Paths.get(scriptPath);
        this.bus = modBus;
    }

    public void loadScript() throws LuaModLoadingException {
        if (chunk != null) return;
        try {
            String script = new String(Files.readAllBytes(scriptPath));
            var chunk = globals.load(script);
            this.chunk = chunk.call();
        } catch (Throwable e) {
            throw new LuaModLoadingException(e);
        }
    }
}