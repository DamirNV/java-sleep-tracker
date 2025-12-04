package ru.yandex.practicum.sleeptracker.app;

import ru.yandex.practicum.sleeptracker.util.SleepSessionParser;
import ru.yandex.practicum.sleeptracker.analyzer.*;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public List<SleepAnalysisFunction> getAnalysisFunctions() {
        return new ArrayList<>(analysisFunctions);
    }

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
        InputStream inputStream = SleepTrackerApp.class.getClassLoader().getResourceAsStream(filePath);

        if (inputStream != null) {
            System.out.println("Файл найден в ресурсах: " + filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .map(line -> {
                            try {
                                return SleepSessionParser.parse(line);
                            } catch (Exception e) {
                                System.err.println("Ошибка парсинга строки: " + line + " - " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath +
                    "\nИскали: " +
                    "\n1. В ресурсах (src/main/resources/" + filePath + ")" +
                    "\n2. По пути: " + path.toAbsolutePath());
        }

        System.out.println("Файл найден по пути: " + path.toAbsolutePath());
        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty())
                .map(line -> {
                    try {
                        return SleepSessionParser.parse(line);
                    } catch (Exception e) {
                        System.err.println("Ошибка парсинга строки: " + line + " - " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String filePath;

        if (args.length > 0) {
            filePath = args[0];
        } else {
            filePath = "sleep_log.txt";
            System.out.println("Используется файл по умолчанию: " + filePath);
            System.out.println("Ищем в: src/main/resources/" + filePath);
            System.out.println("Для использования другого файла: java SleepTrackerApp <путь>");
        }

        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions = loadSleepSessions(filePath);

            if (sessions.isEmpty()) {
                System.out.println("Файл загружен, но не содержит корректных данных о сне.");
                System.out.println("Проверьте формат файла. Каждая строка должна быть в формате:");
                System.out.println("dd.MM.yy HH:mm;dd.MM.yy HH:mm;GOOD/NORMAL/BAD");
                System.out.println("Пример: 01.10.25 22:15;02.10.25 08:00;GOOD");
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
            System.err.println("\nОШИБКА: " + e.getMessage());
            System.err.println("\nВОЗМОЖНЫЕ РЕШЕНИЯ:");
            System.err.println("1. Убедитесь, что файл sleep_log.txt находится в src/main/resources/");
            System.err.println("2. Или укажите полный путь к файлу:");
            System.err.println("   java SleepTrackerApp C:/путь/к/sleep_log.txt");
            System.err.println("3. Или создайте файл в текущей директории: " + Paths.get("").toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}