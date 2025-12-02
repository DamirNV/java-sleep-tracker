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
                throw new IllegalArgumentException("Неверный формат строки: " + line);
            }

            LocalDateTime sleepStart = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime sleepEnd = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            if (sleepEnd.isBefore(sleepStart)) {
                throw new IllegalArgumentException(
                        "Время окончания раньше начала: " + sleepStart + " - " + sleepEnd);
            }

            return new SleepingSession(sleepStart, sleepEnd, quality);

        } catch (Exception e) {
            System.err.println("Ошибка парсинга: " + line + " - " + e.getMessage());
            return null;
        }
    }

    public static boolean isValidFormat(String line) {
        return line != null && line.split(";").length == 3;
    }
}
