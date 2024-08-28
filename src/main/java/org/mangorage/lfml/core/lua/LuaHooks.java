package org.mangorage.lfml.core.lua;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.mangorage.lfml.core.LFMLMod;
import org.mangorage.lfml.core.LFMLUtils;
import org.mangorage.lfml.core.ModInstance;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * lmflCore.hooks:method(args)
 */
public class LuaHooks {

    private final ModInstance modInstance;
    private final ClassLoader classLoader = LFMLMod.class.getClassLoader();
    private final LuaWrapHandler wrapHandler = new LuaWrapHandler();

    public LuaHooks(ModInstance modInstance) {
        this.modInstance = modInstance;
        wrapHandler.register(CreativeModeTab.DisplayItemsGenerator.class, lf -> (parameters, output) -> lf.invoke(
                LuaValue.varargsOf(
                        new LuaValue[] {
                                CoerceJavaToLua.coerce(parameters),
                                CoerceJavaToLua.coerce(output)
                        })
        ));
    }

    // REGISTRY START
    public <T> DeferredRegister<T> deferredRegistry(String name, String space) {
        DeferredRegister<T> dr = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(name, space), modInstance.getModInfo().modId());
        dr.register(modInstance.modBus());
        return dr;
    }
    // REGISTRY END


    // BLOCKS START
    public Supplier<Block> createBasicBlock(BlockBehaviour.Properties properties) {
        return () -> new Block(properties);
    }

    public BlockBehaviour.Properties createBlockProperties() {
        return BlockBehaviour.Properties.of();
    }

    public BlockState getBlockState(String modID, String name) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modID, name)).defaultBlockState();
    }
    // BLOCKS END

    // ITEMS START
    public Supplier<Item> createBasicItem(Item.Properties properties) {
        return () -> new Item(properties);
    }

    public Supplier<BlockItem> createBlockItem(Supplier<Block> block, Item.Properties properties) {
        return () -> new BlockItem(block.get(), properties);
    }

    public Item.Properties createItemProperties() {
        return new Item.Properties();
    }

    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return CreativeModeTab.builder();
    }

    public Supplier<CreativeModeTab> createCreativeModeTab(CreativeModeTab.Builder builder) {
        return builder::build;
    }
    // ITEMS END


    // EVENT START
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
    // EVENT END

    // HELPERS START
    public Object wrap(String clazz, LuaFunction luaFunction) {
        return wrapHandler.create(clazz, luaFunction);
    }

    private LuaWrappedMethod getMethodInternal(Class<?> clazz, String method, Class<?>... parameterTypes) throws NoSuchMethodException {
        return new LuaWrappedMethod(clazz.getDeclaredMethod(method, parameterTypes));
    }

    public LuaWrappedMethod getMethod(String clazz, String method, LuaTable typesTable) {
        var types = LFMLUtils.luaTableToStringArray(typesTable);
        try {
            return getMethodInternal(
                    Class.forName(clazz),
                    method,
                    Arrays.stream(types)
                            .map(t -> {
                                try {
                                    return Class.forName(t);
                                } catch (ClassNotFoundException e) {
                                    throw new IllegalStateException(e);
                                }
                            })
                            .toArray(Class[]::new)
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Component literal(String string) {
        return Component.literal(string);
    }

    public ResourceLocation createResourceLocation(String name, String path) {
        return ResourceLocation.fromNamespaceAndPath(name, path);
    }

    public Supplier<Object> getRegistryObject(ResourceLocation registry, ResourceLocation entry) {
        return BuiltInRegistries.REGISTRY.get(registry).getHolder(entry).orElseThrow()::get;
    }
    // HELPERS END

}
