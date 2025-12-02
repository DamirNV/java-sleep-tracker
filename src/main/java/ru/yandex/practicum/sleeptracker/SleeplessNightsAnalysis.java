package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
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
        LocalDate lastNight = sessions.get(sessions.size() - 1).getSleepEnd().toLocalDate();

        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(this::coversNight)
                .map(this::getNightDate)
                .collect(Collectors.toSet());

        long totalNights = countNightsBetween(firstNight, lastNight);

        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                Math.max(0, sleeplessNights)
        );
    }

    private LocalDate getFirstNightDate(SleepingSession firstSession) {
        LocalDate sessionDate = firstSession.getSleepStart().toLocalDate();
        LocalTime sessionTime = firstSession.getSleepStart().toLocalTime();

        if (sessionTime.isAfter(LocalTime.NOON)) {
            return sessionDate.plusDays(1);
        }
        return sessionDate;
    }

    private boolean coversNight(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            return true;
        }

        if (sleepStart.getHour() < 6) {
            return true;
        }

        LocalDateTime nightStart = sleepStart.toLocalDate().atStartOfDay(); // 00:00
        LocalDateTime nightEnd = nightStart.plusHours(6); // 06:00

        boolean startsBeforeNightEnds = sleepStart.isBefore(nightEnd);
        boolean endsAfterNightStarts = sleepEnd.isAfter(nightStart);

        return startsBeforeNightEnds && endsAfterNightStarts;
    }

    private LocalDate getNightDate(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();

        if (sleepStart.getHour() < 6) {
            return sleepStart.toLocalDate().minusDays(1);
        }
        return sleepStart.toLocalDate();
    }

    private long countNightsBetween(LocalDate start, LocalDate end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
}