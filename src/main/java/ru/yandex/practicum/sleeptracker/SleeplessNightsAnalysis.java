package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleeplessNightsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "нет данных");
        }

        List<LocalDate> nightsWithSleep = sessions.stream()
                .filter(SleepingSession::isNightSleep)
                .flatMap(session -> getNightDates(session))
                .distinct()
                .collect(Collectors.toList());

        LocalDate startDate = sessions.get(0).getSleepStart().toLocalDate();
        LocalDate endDate = sessions.get(sessions.size() - 1).getSleepEnd().toLocalDate();

        long totalNights = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                Math.max(0, sleeplessNights) // не может быть отрицательным
        );
    }

    private Stream<LocalDate> getNightDates(SleepingSession session) {
        LocalDate startDate = session.getSleepStart().toLocalDate();
        LocalDate endDate = session.getSleepEnd().toLocalDate();

        if (startDate.equals(endDate)) {
            return Stream.of(startDate);
        }

        return startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList())
                .stream();
    }
}
