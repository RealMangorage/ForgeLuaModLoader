package org.mangorage.lfml.core.loader;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mangorage.lfml.core.Constants;

@Mod(Constants.MOD_ID)
public class LFMLMod {
    public LFMLMod(FMLJavaModLoadingContext context) {
        try {
            ModInstance.init();
            LFMLInternal.preLoad(context.getModEventBus());
        } catch (Throwable e) {
            throw new IllegalStateException("Error while loading Lua mods...\n" + e.getMessage() );
        }
    }
}
