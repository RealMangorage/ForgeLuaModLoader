package org.mangorage.lfml.core;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        LuaTable table = new LuaTable();
        for (int i = 0; i < args.length; i++) {
            table.set(i + 1, CoerceJavaToLua.coerce(args[i]));
        }
        return table;
    }


    public static LuaTable deepCopy(LuaTable original) {
        LuaTable copy = new LuaTable(); // Create a new LuaTable for the copy
        copyTable(original, copy);      // Recursively copy contents
        return copy;
    }

    // Recursive helper function to copy contents of one table to another
    private static void copyTable(LuaTable original, LuaTable copy) {
        LuaValue key = LuaValue.NIL;

        // Iterate over the original table to copy each key-value pair
        while (true) {
            Varargs next = original.next(key);  // Get the next key-value pair
            key = next.arg1();
            if (key.isnil()) break;  // Stop when there are no more elements

            LuaValue value = original.get(key);

            // If the value is a table, recursively copy it
            if (value.istable()) {
                LuaTable nestedCopy = new LuaTable();
                copyTable(value.checktable(), nestedCopy);
                copy.set(key, nestedCopy);
            } else {
                // Otherwise, directly set the value
                copy.set(key, value);
            }
        }
    }
}
