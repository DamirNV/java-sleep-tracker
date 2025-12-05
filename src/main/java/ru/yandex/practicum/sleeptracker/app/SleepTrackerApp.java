package ru.yandex.practicum.sleeptracker.app;

import ru.yandex.practicum.sleeptracker.util.SleepSessionParser;
import ru.yandex.practicum.sleeptracker.analyzer.*;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private List<SleepAnalysisFunction> analysisFunctions;

    public List<SleepAnalysisFunction> getAnalysisFunctions() {
        return Collections.unmodifiableList(analysisFunctions);
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
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .map(SleepSessionParser::parse)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл не найден: " + filePath);
        }

        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty())
                .map(SleepSessionParser::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String filePath;

        if (args.length > 0) {
            filePath = args[0];
        } else {
            filePath = "sleep_log.txt";
            System.out.println("✨ Используется файл по умолчанию из ресурсов: " + filePath);
        }

        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions = loadSleepSessions(filePath);

            if (sessions.isEmpty()) {
                System.out.println("📭 Файл не содержит корректных данных о сне");
                return;
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println("           📊 АНАЛИЗ КАЧЕСТВА СНА");
            System.out.println("=".repeat(60));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
            System.out.printf("📅 Период анализа: %s - %s%n",
                    sessions.get(0).getSleepStart().format(dateFormatter),
                    sessions.get(sessions.size() - 1).getSleepEnd().format(dateFormatter));

            long goodCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.GOOD).count();
            long normalCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.NORMAL).count();
            long badCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.BAD).count();

            System.out.println("\n📈 КАЧЕСТВО СНА:");
            System.out.printf("   ✅ Хорошее:      %s%n", formatSessions(goodCount));
            System.out.printf("   ⚠️ Нормальное:   %s%n", formatSessions(normalCount));
            System.out.printf("   ❌ Плохое:       %s%n", formatSessions(badCount));

            double avgDuration = sessions.stream()
                    .mapToLong(SleepingSession::getDurationInMinutes)
                    .average()
                    .orElse(0);
            System.out.printf("\n⏱️  Средняя продолжительность сна: %.1f мин (%.1f часов)%n",
                    avgDuration, avgDuration / 60);

            System.out.println("\n" + "-".repeat(60));
            System.out.println("           📋 РЕЗУЛЬТАТЫ АНАЛИЗА");
            System.out.println("-".repeat(60));

            List<SleepAnalysisResult> results = app.getAnalysisFunctions().stream()
                    .map(function -> function.analyze(sessions))
                    .collect(Collectors.toList());

            results.forEach(result -> {
                String emoji = getEmojiForResult(result.getDescription(), result.getResult());
                System.out.printf("%s %s%n", emoji, result);
            });

            System.out.println("\n" + "=".repeat(60));
            System.out.println("           💡 РЕКОМЕНДАЦИИ");
            System.out.println("=".repeat(60));

            printSleepTips(app, sessions);

            System.out.println("\n" + "✨".repeat(30));
            System.out.println("          Анализ завершен успешно!");
            System.out.println("✨".repeat(30));

        } catch (IOException e) {
            System.err.println("\n❌ ОШИБКА ЧТЕНИЯ ФАЙЛА: " + e.getMessage());
            System.err.println("\n🔧 Проверьте:");
            System.err.println("   1. Файл находится в src/main/resources/");
            System.err.println("   2. Или укажите полный путь к файлу");
            System.err.println("      java SleepTrackerApp /полный/путь/к/файлу.txt");
        } catch (Exception e) {
            System.err.println("\n💥 НЕПРЕДВИДЕННАЯ ОШИБКА: " + e.getMessage());
        }
    }

    private static String formatSessions(long count) {
        if (count % 10 == 1 && count % 100 != 11) {
            return count + " сессия";
        } else if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) {
            return count + " сессии";
        } else {
            return count + " сессий";
        }
    }

    private static String getEmojiForResult(String description, Object result) {
        if (description.contains("Общее количество")) return "🔢";
        if (description.contains("Минимальная")) return "📉";
        if (description.contains("Максимальная")) return "📈";
        if (description.contains("Средняя")) return "📊";
        if (description.contains("плохим качеством")) return "⚠️";
        if (description.contains("бессонных")) return "🌙";
        if (description.contains("Хронотип")) {
            String resultStr = result.toString();
            if (resultStr.contains("Сова")) return "🦉";
            if (resultStr.contains("Жаворонок")) return "🐦";
            if (resultStr.contains("Голубь")) return "🕊️";
        }
        return "•";
    }

    private static void printSleepTips(SleepTrackerApp app, List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return;
        }

        List<SleepAnalysisResult> analysisResults = app.getAnalysisFunctions().stream()
                .map(function -> function.analyze(sessions))
                .collect(Collectors.toList());

        long badSessionsCount = analysisResults.stream()
                .filter(r -> r.getDescription().contains("плохим качеством"))
                .findFirst()
                .map(r -> convertToLong(r.getResult()))
                .orElse(0L);

        long sleeplessNights = analysisResults.stream()
                .filter(r -> r.getDescription().contains("бессонных"))
                .findFirst()
                .map(r -> convertToLong(r.getResult()))
                .orElse(0L);

        String chronotype = analysisResults.stream()
                .filter(r -> r.getDescription().contains("Хронотип"))
                .findFirst()
                .map(r -> r.getResult().toString())
                .orElse("Голубь");

        double badPercentage = (double) badSessionsCount / sessions.size() * 100;
        if (badPercentage > 30) {
            System.out.println("⚠️  Более 30% сна плохого качества - обратите внимание на режим!");
        }

        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0);

        if (avgDuration < 420) {
            System.out.println("😴 Средняя продолжительность сна меньше 7 часов");
            System.out.println("💡 Рекомендация: старайтесь спать 7-9 часов в сутки");
        } else if (avgDuration > 540) {
            System.out.println("⏳ Слишком долгий сон (более 9 часов)");
            System.out.println("💡 Рекомендация: избыток сна может снижать продуктивность");
        } else {
            System.out.println("✅ Продолжительность сна в норме (7-9 часов)");
        }

        long nightSessionsCount = sessions.stream()
                .filter(s -> {
                    int hour = s.getSleepStart().getHour();
                    return hour >= 22 || hour < 6;
                })
                .count();

        double nightSessionsPercentage = (double) nightSessionsCount / sessions.size() * 100;
        if (nightSessionsPercentage < 70) {
            System.out.println("🌜 Много дневного сна");
            System.out.println("💡 Рекомендация: старайтесь ложиться до 23:00");
        }

        if (sleeplessNights > 0) {
            System.out.printf("🌙 Обнаружено %d бессонных ночей%n", sleeplessNights);
            System.out.println("💡 Рекомендация: соблюдайте режим сна и отдыха");
        }

        System.out.printf("👤 Ваш хронотип: %s%n", chronotype);

        Map<String, String> chronotypeTips = Map.of(
                "Сова", "вечерние тренировки, яркий свет утром",
                "Жаворонок", "утренние тренировки, избегайте света вечером",
                "Голубь", "гибкий график, слушайте свой организм"
        );

        chronotypeTips.entrySet().stream()
                .filter(entry -> chronotype.contains(entry.getKey()))
                .findFirst()
                .ifPresent(entry ->
                        System.out.println("💡 Совет: " + entry.getValue())
                );

        System.out.println("\n💭 Помните: качественный сон - основа здоровья и продуктивности!");
    }

    private static long convertToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}