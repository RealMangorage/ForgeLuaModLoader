package org.mangorage.lfml.core.lua;

import org.luaj.vm2.LuaTable;
import org.mangorage.lfml.core.LFMLUtils;
import java.lang.reflect.InvocationTargetException;

public class LuaWrappedClass {
    private final Class<?> tClass;

    public LuaWrappedClass(Class<?> tClass) {
        this.tClass = tClass;
    }

    public Class<?> gettClass() {
        return tClass;
    }

    public Object invoke(LuaTable table) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var list = LFMLUtils.extractJavaObjects(table);
        var param = list.stream()
                .map(Object::getClass)
                .toArray(Class[]::new);
        return tClass.getDeclaredConstructor(param).newInstance(list.toArray());
    }

    public Object invoke() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return tClass.getDeclaredConstructor().newInstance();
    }
}
