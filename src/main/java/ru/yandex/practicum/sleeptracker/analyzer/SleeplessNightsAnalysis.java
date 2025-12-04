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

        LocalDate firstNight = getNightDate(sessions.get(0).getSleepStart());
        LocalDate lastNight = getNightDate(sessions.get(sessions.size() - 1).getSleepStart());

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

    private LocalDate getNightDate(java.time.LocalDateTime timestamp) {
        LocalDate date = timestamp.toLocalDate();
        LocalTime time = timestamp.toLocalTime();

        if (time.isAfter(LocalTime.NOON)) {
            return date;
        } else {
            return date.minusDays(1);
        }
    }
}