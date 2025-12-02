package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinDurationAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Минимальная продолжительность сессии", "нет данных");
        }

        long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .min()
                .orElse(0);

        return new SleepAnalysisResult(
                "Минимальная продолжительность сессии (в минутах)",
                minDuration
        );
    }
}