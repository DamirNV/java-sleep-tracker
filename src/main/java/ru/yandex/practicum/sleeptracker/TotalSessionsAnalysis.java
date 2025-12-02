package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class TotalSessionsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        int totalSessions = sessions.size();
        return new SleepAnalysisResult("Общее количество сессий сна", totalSessions);
    }
}
