package org.mangorage.lfml.core.lua.helpers;

public class LuaSwapableObject {
    private final Object READ_LOCK = new Object();
    private Object object;

    public LuaSwapableObject() {}

    public Object get() {
        synchronized (READ_LOCK) {
            return object;
        }
    }

    public void set(Object o) {
        this.object = o;
    }
}
