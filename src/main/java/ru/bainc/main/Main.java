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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, List<Path>> hashFiles = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();
        List<Path> listPaths = FilesWalk.listPaths(Paths.get(args[0]));
        fillMaps(hashFiles, listPaths);
        System.out.println();


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

    public static void fillMaps(Map<String, List<Path>> hashFiles, List<Path> listFiles) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (Path path : listFiles) {
            executorService.submit(() -> {
                try {
                    String hash = SHA256.getFileChecksum(path);
                    List<Path> pathList;
                    if (hashFiles.containsKey(hash)) {
                        pathList = hashFiles.get(hash);
                        pathList.add(path);
                    } else {
                        pathList = new ArrayList<>();
                        pathList.add(path);
                        hashFiles.put(hash, pathList);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
    }
}