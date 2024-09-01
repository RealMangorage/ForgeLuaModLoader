package org.mangorage.lfml.core;

import net.minecraft.world.level.block.Blocks;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LFMLUtils {

    /**
     * Traverses a folder up to the specified depth and returns a list of paths.
     *
     * @param folderPath The path to the folder to traverse.
     * @param maxDepth   The maximum depth to traverse.
     * @return A list of paths found within the specified depth.
     */
    public static List<Path> traverseFolder(Path folderPath, int maxDepth) {
        if (maxDepth == -1) maxDepth = 1000;
        List<Path> pathList = new ArrayList<>();
        try {
            // Traverse the directory and collect paths into the list
            Files.find(folderPath, maxDepth, (path, basicFileAttributes) -> path != folderPath).forEach(pathList::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    // Method to convert LuaTable to String[]
    public static String[] luaTableToStringArray(LuaTable luaTable) {
        // Determine the size of the LuaTable
        int size = 0;
        for (LuaValue key : luaTable.keys()) {
            size++;
        }

        // Create a String array with the determined size
        String[] stringArray = new String[size];
        int index = 0;

        // Iterate over the LuaTable and populate the String array
        for (LuaValue key : luaTable.keys()) {
            LuaValue value = luaTable.get(key);
            // Convert the LuaValue to String and add it to the array
            stringArray[index++] = value.tojstring();
        }

        return stringArray;
    }

    public static List<Object> extractJavaObjects(LuaTable luaTable) {
        List<Object> javaObjects = new ArrayList<>();

        // Iterate over the LuaTable
        for (LuaValue key : luaTable.keys()) {
            LuaValue value = luaTable.get(key);

            // Check if the value is a Java object wrapped in LuaUserdata
            if (value instanceof LuaUserdata) {
                // Extract the Java object from LuaUserdata
                Object javaObject = ((LuaUserdata) value).userdata();
                javaObjects.add(javaObject);
            }
        }

        return javaObjects;
    }

    public static Varargs convertToVarargs(Object... args) {
        return LuaValue.varargsOf(
                Stream.of(args)
                        .map(CoerceJavaToLua::coerce)
                        .toArray(LuaValue[]::new)
                );
    }

    /**
     * Recursively makes a Lua table and all its subtables read-only.
     *
     * @param originalTable The original Lua table to be made read-only.
     * @return A read-only proxy of the original Lua table.
     */
    public static LuaTable makeReadOnly(LuaTable originalTable) {
        // Create a proxy table that will act as the read-only table
        LuaTable proxyTable = new LuaTable();

        // Metatable to define the read-only behavior
        LuaTable metatable = new LuaTable();

        // __index to allow read access to the original table
        metatable.set("__index", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue key) {
                LuaValue value = originalTable.get(key);
                if (value.istable()) {
                    // Recursively apply read-only to subtables
                    return makeReadOnly(value.checktable());
                }
                return value;
            }
        });

        // __newindex to prevent any modifications
        metatable.set("__newindex", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                throw new LuaError("Attempt to modify a read-only table");
            }

            @Override
            public LuaValue call(LuaValue self, LuaValue key, LuaValue value) {
                throw new LuaError("Attempt to modify a read-only table");
            }
        });

        // Set the metatable on the proxy table
        proxyTable.setmetatable(metatable);

        return proxyTable;
    }
}