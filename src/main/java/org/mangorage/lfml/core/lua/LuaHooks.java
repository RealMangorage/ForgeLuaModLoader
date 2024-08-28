package org.mangorage.lfml.core.lua;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.mangorage.lfml.core.LFMLMod;
import org.mangorage.lfml.core.ModInstance;

import java.util.function.Supplier;

/**
 * lmflCore.hooks:method(args)
 */
public class LuaHooks {

    private final ModInstance modInstance;
    private final ClassLoader classLoader = LFMLMod.class.getClassLoader();

    public LuaHooks(ModInstance modInstance) {
        this.modInstance = modInstance;
    }

    public <T> DeferredRegister<T> deferredRegistry(String name, String space) {
        DeferredRegister<T> dr = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(name, space), modInstance.getModInfo().modId());
        dr.register(modInstance.modBus());
        return dr;
    }

    public Supplier<Block> createBlock(BlockBehaviour.Properties properties) {
        return () -> new Block(properties);
    }

    public BlockBehaviour.Properties createBlockProperties() {
        return BlockBehaviour.Properties.of();
    }

    public BlockState getBlockState(String modID, String name) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modID, name)).defaultBlockState();
    }


    public void hookEvent(boolean modBus, String eventType, LuaClosure closure) {
        try {
            Class<Event> eventClass = (Class<Event>) Class.forName(eventType);
            IEventBus bus = modBus ? modInstance.modBus() : MinecraftForge.EVENT_BUS;
            bus.addListener(
                    EventPriority.NORMAL,
                    false,
                    eventClass,
                    e -> closure.invoke(LuaValue.varargsOf(new LuaValue[]{CoerceJavaToLua.coerce(e)}))
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
