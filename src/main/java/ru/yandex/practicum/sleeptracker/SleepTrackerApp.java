package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public SleepTrackerApp() {
        this.analysisFunctions = new ArrayList<>();
        // Добавляем первую функцию
        this.analysisFunctions.add(new TotalSessionsAnalysis());
    }

    public void addAnalysisFunction(SleepAnalysisFunction function) {
        analysisFunctions.add(function);
    }

    // Метод для чтения файла (пока с циклом, потом заменим)
    public static List<SleepingSession> loadSleepSessions(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);
        List<SleepingSession> sessions = new ArrayList<>();

        // ВРЕМЕННО используем цикл - потом заменим на Stream API
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length != 3) {
                System.err.println("Неверный формат: " + line);
                continue;
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
                LocalDateTime sleepStart = LocalDateTime.parse(parts[0].trim(), formatter);
                LocalDateTime sleepEnd = LocalDateTime.parse(parts[1].trim(), formatter);
                SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

                sessions.add(new SleepingSession(sleepStart, sleepEnd, quality));
            } catch (Exception e) {
                System.err.println("Ошибка парсинга: " + line);
            }
        }

        return sessions;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу>");
            System.out.println("Пример: java SleepTrackerApp sleep_log.txt");
            return;
        }

        String filePath = args[0];

        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions = loadSleepSessions(filePath);

            System.out.println("Загружено сессий: " + sessions.size());

            // Запускаем анализы
            for (SleepAnalysisFunction function : app.analysisFunctions) {
                SleepAnalysisResult result = function.analyze(sessions);
                System.out.println(result);
            }

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}