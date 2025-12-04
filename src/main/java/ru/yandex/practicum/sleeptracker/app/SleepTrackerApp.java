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
            System.out.println("📁 Файл найден в ресурсах: " + filePath);
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
            throw new IOException("Файл не найден: " + filePath +
                    "\nИскали: " +
                    "\n1. В ресурсах (src/main/resources/" + filePath + ")" +
                    "\n2. По пути: " + path.toAbsolutePath());
        }

        System.out.println("📁 Файл найден по пути: " + path.toAbsolutePath());
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
            System.out.println("💤 Используется файл по умолчанию: " + filePath);
            System.out.println("🔍 Ищем в: src/main/resources/" + filePath);
            System.out.println("💡 Для использования другого файла: java SleepTrackerApp <путь>");
        }

        try {
            SleepTrackerApp app = new SleepTrackerApp();
            List<SleepingSession> sessions = loadSleepSessions(filePath);

            if (sessions.isEmpty()) {
                System.out.println("\n⚠️  Файл загружен, но не содержит корректных данных о сне.");
                System.out.println("📋 Проверьте формат файла. Каждая строка должна быть в формате:");
                System.out.println("   dd.MM.yy HH:mm;dd.MM.yy HH:mm;GOOD/NORMAL/BAD");
                System.out.println("📝 Пример: 01.10.25 22:15;02.10.25 08:00;GOOD");
                return;
            }

            System.out.println("\n" + "*".repeat(60));
            System.out.println("                     АНАЛИЗ КАЧЕСТВА СНА 💤");
            System.out.println("*".repeat(60));

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
            System.out.printf("📊 Проанализировано сессий сна: %d%n", sessions.size());
            System.out.printf("📅 Период анализа: %s - %s%n%n",
                    sessions.get(0).getSleepStart().format(dateFormatter),
                    sessions.get(sessions.size() - 1).getSleepEnd().format(dateFormatter));

            long goodCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.GOOD).count();
            long normalCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.NORMAL).count();
            long badCount = sessions.stream().filter(s -> s.getQuality() == SleepQuality.BAD).count();

            System.out.println("📈 КАЧЕСТВО СНА:");
            System.out.println("   ✅ Хорошее: " + goodCount + " сессий");
            System.out.println("   ⚠️ Нормальное: " + normalCount + " сессий");
            System.out.println("   ❌ Плохое: " + badCount + " сессий");

            System.out.println("\n" + "*".repeat(60));
            System.out.println("                     РЕЗУЛЬТАТЫ АНАЛИЗА 📋");
            System.out.println("*".repeat(60));

            app.analysisFunctions.forEach(function -> {
                SleepAnalysisResult result = function.analyze(sessions);
                String emoji = getEmojiForResult(result);
                System.out.printf("%s %s%n", emoji, result);
            });

            System.out.println("\n" + "*".repeat(60));
            System.out.println("                    АНАЛИЗ ЗАВЕРШЕН! 🎉");
            System.out.println("*".repeat(60));

            printSleepTips(sessions);

        } catch (IOException e) {
            System.err.println("\n❌ ОШИБКА: " + e.getMessage());
            System.err.println("\n🔧 ВОЗМОЖНЫЕ РЕШЕНИЯ:");
            System.err.println("1. Убедитесь, что файл sleep_log.txt находится в src/main/resources/");
            System.err.println("2. Или укажите полный путь к файлу:");
            System.err.println("   java SleepTrackerApp C:/путь/к/sleep_log.txt");
            System.err.println("3. Или создайте файл в текущей директории: " + Paths.get("").toAbsolutePath());
        } catch (Exception e) {
            System.err.println("💥 Произошла непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getEmojiForResult(SleepAnalysisResult result) {
        String description = result.getDescription();

        if (description.contains("Общее количество")) return "📊";
        if (description.contains("Минимальная")) return "⬇️";
        if (description.contains("Максимальная")) return "⬆️";
        if (description.contains("Средняя")) return "📐";
        if (description.contains("плохим качеством")) return "⚠️";
        if (description.contains("бессонных")) return "🌙";
        if (description.contains("Хронотип")) {
            String value = result.getResult().toString();
            if (value.contains("Сова")) return "🦉";
            if (value.contains("Жаворонок")) return "🐦";
            if (value.contains("Голубь")) return "🕊️";
        }
        return "•";
    }

    private static void printSleepTips(List<SleepingSession> sessions) {
        System.out.println("\n💡 РЕКОМЕНДАЦИИ ДЛЯ УЛУЧШЕНИЯ СНА:");

        long badSessions = sessions.stream()
                .filter(s -> s.getQuality() == SleepQuality.BAD)
                .count();

        if (badSessions > sessions.size() * 0.3) {
            System.out.println("⏰ Более 30% сна плохого качества - обратите внимание на режим!");
        }

        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0);

        if (avgDuration < 420) {
            System.out.println("😴 Средняя продолжительность сна меньше 7 часов - старайтесь спать дольше");
        } else if (avgDuration > 540) {
            System.out.println("⏳ Слишком долгий сон (более 9 часов) может быть признаком проблем");
        }

        long nightSessions = sessions.stream()
                .filter(s -> {
                    int hour = s.getSleepStart().getHour();
                    return hour >= 22 || hour < 6;
                })
                .count();

        if (nightSessions < sessions.size() * 0.7) {
            System.out.println("🌜 Старайтесь ложиться спать до 23:00 для лучшего качества сна");
        }

        System.out.println("\n💭 Помните: качественный сон - залог здоровья и продуктивности!");
    }
}