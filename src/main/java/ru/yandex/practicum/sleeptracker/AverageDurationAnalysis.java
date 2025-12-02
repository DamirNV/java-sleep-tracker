package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class AverageDurationAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult("Средняя продолжительность сессии", "нет данных");
        }

        double averageDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult(
                "Средняя продолжительность сессии (в минутах)",
                String.format("%.1f", averageDuration)
        );
    }
}
