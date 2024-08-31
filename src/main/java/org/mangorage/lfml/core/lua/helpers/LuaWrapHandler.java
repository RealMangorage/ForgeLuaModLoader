package org.mangorage.lfml.core.lua.helpers;

import org.luaj.vm2.LuaFunction;

import java.util.HashMap;
import java.util.Map;

public class LuaWrapHandler {

    public interface Wrapper<T> {
        T create(LuaFunction luaFunction);
    }

    private final Map<Class<?>, Wrapper<?>> REGISTERED = new HashMap<>();

    public <T> void register(Class<T> tClass, Wrapper<T> wrapper) {
        if (REGISTERED.put(tClass, wrapper) != null)
            throw new IllegalArgumentException("Already registered %s to LuaWrapHandler".formatted(tClass));
    }

    public <T> T create(String clazz, LuaFunction luaFunction) {
        try {
            var clz = Class.forName(clazz);
            if (!REGISTERED.containsKey(clz)) return null;
            return (T) REGISTERED.get(clz).create(luaFunction);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
