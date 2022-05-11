/*Курсовая для ученика прошедшего Java Core и многопоточность.

        Цель: Разработать консольную программу по поиску дубликатов файлов в заданной директории. Параметр программе передается через консоль. Например:

        Windows:
        java -jar duplicate.jar c:\temp
        Linux
        java -jar duplicate.jar /home/user/temp

        Требования к программе:
 1) Проверка идентичности файлов производится методом на усмотрение исполнителя.
 2) проверяются также вложенные поддиректории.
 3) сравнение по имени файла не производится. Интересует только содержимое.
 4) программа должна использовать элементы многопоточности.
 5) при старте программы происходит засечка времени программы. При завершении работы выводится затраченное на работу время в секундах.
 6) При завершении поиска производится расчет места,
    которое освободится при удалении всех дубликатов с оставлением только по одной копии файла имеющего дубликаты.
 7) Программа должна быть разработана с учетом принципов SOLID в ООП стиле
 8) Программа должна компилироваться и собираться в Jar файл

 Исходный код необходимо залить на GitHub и прислать ссылку.
 В данной курсовой проверяются знания и умения пользоваться коллекциями,
 стримами, многопоточкой, потоками ввода-вывода, системой контроля версий Git и сборщиком Java проектов Maven
 На выполнение данной курсовой дается неделя.

 З.Ы: Естественно можно гуглить)*/

 package ru.bainc.main;

import ru.bainc.main.service.FilesWalk;
import ru.bainc.main.service.SHA256;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) throws IOException {
        Map<String, List<Path>> hashFiles = new ConcurrentHashMap<>();
        long startTime = System.currentTimeMillis();
        List<Path> listPaths = FilesWalk.listPaths(Paths.get(args[0]));
        fillMaps(hashFiles, listPaths);
        System.out.println();


        double mesto = (double) calculateSize(hashFiles);
//        System.out.println("Освободится в килобайтах: " + mesto / 1024);
        System.out.println("Освободится в мегабайтах: " + mesto / 1024 / 1024);
        System.out.println("\nвремя  выполнения программы = " + (System.currentTimeMillis() - startTime)/1000 + " seconds ");
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
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (Path path : listFiles) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
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
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}