package org.mangorage.lfml.core.lua;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.mangorage.lfml.core.LFMLMod;
import org.mangorage.lfml.core.LFMLUtils;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * lmflCore.hooks:method(args)
 */
public class LuaModCore {

    private final ClassLoader classLoader = LFMLMod.class.getClassLoader();
    private final LuaWrapHandler wrapHandler = new LuaWrapHandler();

    public LuaModCore() {
        wrapHandler.register(CreativeModeTab.DisplayItemsGenerator.class, lf -> (parameters, output) -> lf.invoke(
                LuaValue.varargsOf(
                        new LuaValue[] {
                                CoerceJavaToLua.coerce(parameters),
                                CoerceJavaToLua.coerce(output)
                        })
        ));
    }

    // EVENT START
    public void hookEvent(IEventBus modBus, String eventType, LuaClosure closure) {
        try {
            Class<Event> eventClass = (Class<Event>) Class.forName(eventType);
            modBus.addListener(
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
        return new LuaWrappedMethod(clazz.getMethod(method, parameterTypes));
    }

    public Object getClass(String clazz) throws ClassNotFoundException {
        return getClass(clazz, false);
    }

    public Object getClass(String clazz, boolean wrap) throws ClassNotFoundException {
        var a = Class.forName(clazz);
        return wrap ? new LuaWrappedClass(a) : a;
    }

    public LuaValue JavaToLua(Object o) {
        return CoerceJavaToLua.coerce(o);
    }

    public Object LuaToJava(LuaValue luaValue, String clazz) throws ClassNotFoundException {
        return CoerceLuaToJava.coerce(luaValue, Class.forName(clazz));
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

    public Supplier<Object> createSupplier(LuaFunction function) {
        return () -> CoerceLuaToJava.coerce(function.invoke().arg1(), Object.class);
    }
    // HELPERS END

}
