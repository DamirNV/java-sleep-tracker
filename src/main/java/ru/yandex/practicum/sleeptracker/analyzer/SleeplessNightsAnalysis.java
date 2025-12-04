package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.util.NightUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SleeplessNightsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "нет данных");
        }

        LocalDate firstNight = getFirstNightDate(sessions.get(0));
        LocalDate lastNight = getLastNightDate(sessions.get(sessions.size() - 1));

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(NightUtils::overlapsNightInterval)
                .map(NightUtils::getNightDate)
                .collect(Collectors.toSet());

        long totalNights = NightUtils.countNightsBetween(firstNight, lastNight);
        long sleeplessNights = Math.max(0, totalNights - nightsWithSleep.size());

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                sleeplessNights
        );
    }

    private LocalDate getFirstNightDate(SleepingSession firstSession) {
        LocalDate sessionDate = firstSession.getSleepStart().toLocalDate();
        LocalTime sessionTime = firstSession.getSleepStart().toLocalTime();

        if (sessionTime.isAfter(LocalTime.NOON)) {
            return sessionDate;
        } else {
            return sessionDate.minusDays(1);
        }
    }

    private LocalDate getLastNightDate(SleepingSession lastSession) {
        LocalDate sessionDate = lastSession.getSleepEnd().toLocalDate();
        LocalTime sessionTime = lastSession.getSleepEnd().toLocalTime();

        if (sessionTime.isBefore(LocalTime.NOON)) {
            return sessionDate.minusDays(1);
        } else {
            return sessionDate;
        }
    }
}