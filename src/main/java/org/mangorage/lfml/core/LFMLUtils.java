package org.mangorage.lfml.core;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

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
}
