package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxDurationAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult("Максимальная продолжительность сессии", "нет данных");
        }

        long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .max()
                .orElse(0);

        return new SleepAnalysisResult(
                "Максимальная продолжительность сессии (в минутах)",
                maxDuration
        );
    }
}
