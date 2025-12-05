package ru.yandex.practicum.sleeptracker.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SleepingSession {
    private final LocalDateTime sleepStart;
    private final LocalDateTime sleepEnd;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime sleepStart, LocalDateTime sleepEnd, SleepQuality quality) {
        if (sleepEnd.isBefore(sleepStart)) {
            throw new IllegalArgumentException("Время окончания сна не может быть раньше времени начала");
        }
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

    @Override
    public String toString() {
        return String.format("SleepSession{start=%s, end=%s, quality=%s}",
                sleepStart, sleepEnd, quality);
    }
}