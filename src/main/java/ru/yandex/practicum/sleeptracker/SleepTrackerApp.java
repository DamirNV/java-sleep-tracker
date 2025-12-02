package ru.yandex.practicum.sleeptracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        InputStream inputStream = SleepTrackerApp.class
                .getClassLoader()
                .getResourceAsStream(filePath);

        if (inputStream != null) {
            System.out.println("Файл найден в ресурсах: " + filePath);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream))) {

                return reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .map(SleepTrackerApp::parseSleepSession)
                        .filter(session -> session != null)
                        .collect(Collectors.toList());
            }
        }

        System.out.println("Пробуем найти как обычный файл: " + filePath);
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath +
                    "\nИскали в ресурсах и по пути: " + path.toAbsolutePath());
        }

        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty())
                .map(SleepTrackerApp::parseSleepSession)
                .filter(session -> session != null)
                .collect(Collectors.toList());
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
        String filePath;

        if (args.length > 0) {
            filePath = args[0];
        } else {
            filePath = "sleep_log.txt";
            System.out.println("Используется файл по умолчанию: " + filePath);
            System.out.println("Для использования другого файла: java SleepTrackerApp <путь>");
        }

        try {
            SleepTrackerApp app = new SleepTrackerApp();

            List<SleepingSession> sessions = loadSleepSessions(filePath);

            if (sessions.isEmpty()) {
                System.out.println("В файле нет данных о сне или файл пуст.");
                return;
            }

            System.out.println("=".repeat(60));
            System.out.println("АНАЛИЗ КАЧЕСТВА СНА");
            System.out.println("=".repeat(60));
            System.out.printf("Проанализировано сессий сна: %d%n", sessions.size());
            System.out.printf("Период анализа: %s - %s%n%n",
                    sessions.get(0).getSleepStart().toLocalDate(),
                    sessions.get(sessions.size() - 1).getSleepEnd().toLocalDate());

            System.out.println("РЕЗУЛЬТАТЫ АНАЛИЗА:");
            System.out.println("-".repeat(60));

            app.analysisFunctions.forEach(function -> {
                SleepAnalysisResult result = function.analyze(sessions);
                System.out.printf("• %s%n", result);
            });

            System.out.println("=".repeat(60));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Проверьте путь и формат файла.");
            System.err.println("\nПоместите файл sleep_log.txt в:");
            System.err.println("1. src/main/resources/ (как ресурс)");
            System.err.println("2. Или укажите полный путь к файлу");
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}