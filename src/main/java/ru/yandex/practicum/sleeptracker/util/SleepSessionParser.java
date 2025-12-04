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

        String[] parts = line.split(";");
        if (parts.length != 3) {
            return null;
        }

        try {
            LocalDateTime sleepStart = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime sleepEnd = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            return new SleepingSession(sleepStart, sleepEnd, quality);
        } catch (Exception e) {
            return null;
        }
    }
}