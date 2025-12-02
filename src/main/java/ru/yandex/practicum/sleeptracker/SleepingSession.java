package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
        this.quality = quality;
    }

    public LocalDateTime getSleepStart() {
        return sleepStart;
    }

    public LocalDateTime getSleepEnd() {
        return sleepEnd;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long getDurationInMinutes() {
        return ChronoUnit.MINUTES.between(sleepStart, sleepEnd);
    }

    public boolean overlapsNightInterval() {
        LocalDateTime nightStart = sleepStart.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6); // 06:00

        boolean overlapsCurrentNight = !sleepEnd.isBefore(nightStart) &&
                !sleepStart.isAfter(nightEnd);

        if (overlapsCurrentNight) {
            return true;
        }

        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            LocalDateTime prevNightStart = nightStart.minusDays(1);
            LocalDateTime prevNightEnd = prevNightStart.plusHours(6);

            return !sleepEnd.isBefore(prevNightStart) &&
                    !sleepStart.isAfter(prevNightEnd);
        }

        return false;
    }

    public boolean isNightSleep() {
        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            return true;
        }
        if (sleepStart.getHour() < 6) {
            return true;
        }
        return overlapsNightInterval();
    }



    @Override
    public String toString() {
        return String.format("SleepSession{start=%s, end=%s, quality=%s}",
                sleepStart, sleepEnd, quality);
    }
}
