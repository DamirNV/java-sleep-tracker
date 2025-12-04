package ru.yandex.practicum.sleeptracker.util;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;

public class NightUtils {
    private static final LocalTime NIGHT_START = LocalTime.MIDNIGHT;
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.NOON;

    public static boolean overlapsNightInterval(SleepingSession session) {
        return overlapsNightInterval(session.getSleepStart(), session.getSleepEnd());
    }

    public static boolean overlapsNightInterval(LocalDateTime start, LocalDateTime end) {
        long daysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        return LongStream.rangeClosed(0, daysBetween)
                .mapToObj(i -> start.toLocalDate().plusDays(i))
                .anyMatch(date -> {
                    LocalDateTime nightStart = date.atStartOfDay();
                    LocalDateTime nightEnd = nightStart.plusHours(6);
                    return !end.isBefore(nightStart) && !start.isAfter(nightEnd);
                });
    }

    public static LocalDate getNightDate(SleepingSession session) {
        return getNightDate(session.getSleepStart());
    }

    public static LocalDate getNightDate(LocalDateTime timestamp) {
        LocalDate date = timestamp.toLocalDate();
        LocalTime time = timestamp.toLocalTime();

        if (time.isBefore(NOON)) {
            return date.minusDays(1);
        } else {
            return date;
        }
    }

    public static long countNightsBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end) + 1;
    }

    public static boolean isNightTime(LocalTime time) {
        return !time.isBefore(NIGHT_START) && time.isBefore(NIGHT_END);
    }
}