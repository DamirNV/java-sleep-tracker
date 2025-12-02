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
        // Определяем путь к файлу
        String filePath;

        if (args.length > 0) {
            // Используем переданный аргумент
            filePath = args[0];
        } else {
            // По умолчанию ищем в корне
            filePath = "sleep_log.txt";
            System.out.println("Используется файл по умолчанию: " + filePath);
            System.out.println("Для использования другого файла: java SleepTrackerApp <путь>");
        }

        try {
            SleepTrackerApp app = new SleepTrackerApp();

            // Проверяем существует ли файл
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (!java.nio.file.Files.exists(path)) {
                System.err.println("Файл не найден: " + filePath);
                System.err.println("Текущая директория: " + System.getProperty("user.dir"));
                System.err.println("\nПоместите файл sleep_log.txt в корень проекта");
                return;
            }

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

            // Запускаем все аналитические функции
            app.analysisFunctions.forEach(function -> {
                SleepAnalysisResult result = function.analyze(sessions);
                System.out.printf("• %s%n", result);
            });

            System.out.println("=".repeat(60));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Проверьте путь и формат файла.");
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}