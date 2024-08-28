package org.mangorage.lfml.core.lua;

import org.luaj.vm2.LuaTable;
import org.mangorage.lfml.core.LFMLUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LuaWrappedMethod {
    private final Method method;
    public LuaWrappedMethod(Method method) {
        this.method = method;
    }

    public void invoke(Object instance, LuaTable table) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, LFMLUtils.extractJavaObjects(table));
    }
}
