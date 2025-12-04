package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.util.NightUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SleeplessNightsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0);
        }

        LocalDate firstNight = NightUtils.getNightDate(sessions.get(0).getSleepStart());
        LocalDate lastNight = NightUtils.getNightDate(sessions.get(sessions.size() - 1).getSleepEnd());

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(NightUtils::overlapsNightInterval)
                .map(NightUtils::getNightDate)
                .collect(Collectors.toSet());

        long totalNights = countNightsBetween(firstNight, lastNight);
        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep.size());

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }

    private long countNightsBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
}