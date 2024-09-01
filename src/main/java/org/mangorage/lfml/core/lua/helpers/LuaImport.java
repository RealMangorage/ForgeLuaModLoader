package org.mangorage.lfml.core.lua.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LuaImport {
    private final Map<String, Class<?>> CLASSES = new HashMap<>();
    private final Map<String, Class<?>> CACHE = new HashMap<>();

    public void register(Class<?> clz, String id) {
        if (CLASSES.put(id, clz) != null)
            throw new IllegalStateException("Cannot register %s as its already taken".formatted(id));
    }

    public Optional<Class<?>> getDefinedClass(String clazz) {
        return CLASSES.containsKey(clazz) ? Optional.of(CLASSES.get(clazz)) : Optional.empty();
    }

    public Class<?> getDefinedClassOrCache(String clazz) {
        return getDefinedClass(clazz).orElseGet(() -> CACHE.computeIfAbsent(clazz, key -> {
            try {
                return Class.forName(key);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
