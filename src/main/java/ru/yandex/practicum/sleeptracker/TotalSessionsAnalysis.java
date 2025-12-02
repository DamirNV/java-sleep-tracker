package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class TotalSessionsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult("Общее количество сессий сна", "нет данных");
        }
        int totalSessions = sessions.size();
        return new SleepAnalysisResult("Общее количество сессий сна", totalSessions);
    }
}
