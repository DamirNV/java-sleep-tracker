package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public SleepTrackerApp() {
        this.analysisFunctions = new ArrayList<>();
        // Позже добавим функции
    }

    public void addAnalysisFunction(SleepAnalysisFunction function) {
        analysisFunctions.add(function);
    }

    public static void main(String[] args) {
        // Проверяем аргументы командной строки
        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу>");
            System.out.println("Пример: java SleepTrackerApp sleep_log.txt");
            return;
        }

        String filePath = args[0];
        System.out.println("Загружаем данные сна из файла: " + filePath);

        try {
            // Позже добавим загрузку файла
            System.out.println("Функциональность загрузки файла будет добавлена на следующем шаге");

        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}