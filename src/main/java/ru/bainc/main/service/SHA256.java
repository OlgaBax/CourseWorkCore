package ru.bainc.main.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


// Java-программа для вычисления значения хэша SHA256

public class SHA256 {

    public static String getFileChecksum(Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        //Получаем поток ввода бинарного файла для чтения содержимого файла
        FileInputStream fis = new FileInputStream(path.toFile());
        //Создаем массив байтов для чтения данных частями
        byte[] byteArray = new byte[8192];
        int bytesCount = 0;
        // Считывание данных и обновление в дайджесте сообщений Обновляет дайджест,
        // используя указанный массив байтов, начиная с заданного смещения.
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        //закрываем поток
        fis.close();
        //Получаем байты хэша
        byte[] bytes = digest.digest();
        //Этот байт[] содержит байты в десятичном формате;
        //Преобразуйте его в шестнадцатеричный формат
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //возвращаем полный хэш
        return sb.toString();
    }
}

