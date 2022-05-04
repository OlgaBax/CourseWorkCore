package ru.bainc.main.service;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class SearchFiles {
    public static List<Path> searchFiles(File rootFile, List<Path> pathList) {
        if (rootFile.isDirectory()) {
            File[] directoryFiles = rootFile.listFiles();
            if (directoryFiles != null) {
            }
            for (File file : directoryFiles) {
                if (file.isDirectory()) {
                    searchFiles(file, pathList);
                } else pathList.add(file.toPath());
            }
        }
        return pathList;
    }
}





