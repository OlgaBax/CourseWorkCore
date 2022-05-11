package ru.bainc.main.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesWalk {
    public static List<Path> listPaths (Path path) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) { //создаем стрим <Path> walk-это название
            result = walk.filter(path1 -> Files.isRegularFile(path1)) // проверяем является pash1 обычным файлом
                    .collect(Collectors.toList());
        }
        return result;
    }
}