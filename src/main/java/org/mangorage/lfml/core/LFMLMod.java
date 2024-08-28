package org.mangorage.lfml.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class LFMLMod {
    public LFMLMod(FMLJavaModLoadingContext context) {
        try {
            LFMLInternal.preLoad(context.getModEventBus());
        } catch (Throwable e) {
            throw e;
        }
    }
}
