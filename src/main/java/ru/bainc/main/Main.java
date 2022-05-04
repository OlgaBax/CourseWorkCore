package ru.bainc.main;

import ru.bainc.main.service.FilesWalk;
import ru.bainc.main.service.SHA256;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Map<String, List<Path>> hashFiles = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            try {
                FilesWalk.listPaths(Paths.get(args[0]))
                        .stream()
                        .map(element -> {
                            try {
                                String hash = SHA256.getFileChecksum(element);
                                List<Path> pathList;

                                if (hashFiles.containsKey(hash)) {
                                    pathList = hashFiles.get(hash);
                                    pathList.add(element);
                                } else {
                                    pathList = new ArrayList<>();
                                    pathList.add(element);
                                    hashFiles.put(hash, pathList);
                                }
                                return hash;
                            } catch (IOException | NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                return null;
                            }
                        }).count();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();

        double mesto = (double) calculateSize(hashFiles);
        System.out.println("Освободится в байтах: " + mesto);
        System.out.println("Освободится в килобайтах: " + mesto / 1024);
        System.out.println("Освободится в мегабайтах: " + mesto / 1024 / 1024);
        System.out.println("\nвремя  выполнения программы = " + (System.currentTimeMillis() - startTime) + " milliseconds ");
    }

    public static long calculateSize(Map<String, List<Path>> hashFiles) throws IOException {
        long result = 0;
        for (String hash : hashFiles.keySet()) {
            List<Path> dublicateList = hashFiles.get(hash);
            if (dublicateList.size() > 1) {
                result += Files.size(dublicateList.get(0)) * (dublicateList.size() - 1);
            }
        }
        return result;
    }
}
