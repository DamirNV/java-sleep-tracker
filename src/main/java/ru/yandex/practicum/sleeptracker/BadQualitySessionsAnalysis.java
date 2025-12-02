package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class BadQualitySessionsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null) {
            return new SleepAnalysisResult("Количество сессий с плохим качеством сна", "нет данных");
        }
        long badSessionsCount = sessions.stream()
                .filter(session -> session.getQuality() == SleepQuality.BAD)
                .count();

        return new SleepAnalysisResult(
                "Количество сессий с плохим качеством сна",
                badSessionsCount
        );
    }
}
