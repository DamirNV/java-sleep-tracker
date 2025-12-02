package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public SleepTrackerApp() {
        this.analysisFunctions = new ArrayList<>();
        this.analysisFunctions.add(new TotalSessionsAnalysis());
        this.analysisFunctions.add(new MinDurationAnalysis());
        this.analysisFunctions.add(new MaxDurationAnalysis());
        this.analysisFunctions.add(new AverageDurationAnalysis());
        this.analysisFunctions.add(new BadQualitySessionsAnalysis());
        this.analysisFunctions.add(new SleeplessNightsAnalysis());
        this.analysisFunctions.add(new ChronotypeAnalysis());
    }

    public void addAnalysisFunction(SleepAnalysisFunction function) {
        analysisFunctions.add(function);
    }

    public static List<SleepingSession> loadSleepSessions(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // Используем Stream API вместо цикла
        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty()) // Пропускаем пустые строки
                .map(SleepTrackerApp::parseSleepSession) // Парсим каждую строку
                .filter(session -> session != null) // Убираем неудачные парсинги
                .collect(Collectors.toList()); // Собираем в список
    }

    private static SleepingSession parseSleepSession(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length != 3) {
                System.err.println("Неверный формат строки: " + line);
                return null;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
            LocalDateTime sleepStart = LocalDateTime.parse(parts[0].trim(), formatter);
            LocalDateTime sleepEnd = LocalDateTime.parse(parts[1].trim(), formatter);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            return new SleepingSession(sleepStart, sleepEnd, quality);

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге строки: " + line + " - " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
//        if (args.length < 1) {
//            System.out.println("Использование: java SleepTrackerApp <путь_к_файлу>");
//            System.out.println("Пример: java SleepTrackerApp sleep_log.txt");
//            return;
//        }

        String filePath = "src/main/resources/sleep_log.txt";

        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions = loadSleepSessions(filePath);

            System.out.println("=== Анализ сна ===");
            System.out.println("Всего сессий: " + sessions.size());
            System.out.println();

            app.analysisFunctions.stream()
                    .map(function -> function.analyze(sessions))
                    .forEach(result -> System.out.println("• " + result));

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}