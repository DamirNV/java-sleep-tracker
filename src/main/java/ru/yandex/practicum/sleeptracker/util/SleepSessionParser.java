package ru.yandex.practicum.sleeptracker.util;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SleepSessionParser {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static SleepingSession parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = line.split(";");
            if (parts.length != 3) {
                System.err.println("Неверный формат строки: " + line);
                return null;
            }

            LocalDateTime sleepStart = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime sleepEnd = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            if (sleepEnd.isBefore(sleepStart)) {
                System.err.println("Время окончания раньше начала: " + line);
                return null;
            }

            return new SleepingSession(sleepStart, sleepEnd, quality);

        } catch (Exception e) {
            System.err.println("Ошибка парсинга строки: " + line + " - " + e.getMessage());
            return null;
        }
    }

    public static boolean isValidFormat(String line) {
        if (line == null) return false;
        String[] parts = line.split(";");
        return parts.length == 3;
    }
}