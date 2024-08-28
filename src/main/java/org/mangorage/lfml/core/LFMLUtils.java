package org.mangorage.lfml.core;

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
}
