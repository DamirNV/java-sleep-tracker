package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", "недостаточно данных");
        }

        Map<Chronotype, Long> chronotypeCounts = sessions.stream()
                .filter(this::isNightSessionForChronotype)
                .map(this::determineChronotypeForSession)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        Chronotype dominantChronotype = determineDominantChronotype(chronotypeCounts);

        return new SleepAnalysisResult(
                "Хронотип пользователя",
                dominantChronotype.getDisplayName()
        );
    }

    private boolean isNightSessionForChronotype(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        if (sleepStart.toLocalDate().equals(sleepEnd.toLocalDate()) &&
                sleepStart.getHour() >= 6) {
            return false;
        }

        long durationHours = java.time.Duration.between(sleepStart, sleepEnd).toHours();
        if (durationHours < 4) {
            return false;
        }

        return true;
    }

    private Chronotype determineChronotypeForSession(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        int sleepHour = sleepStart.getHour();
        int wakeHour = sleepEnd.getHour();

        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            if (wakeHour < sleepHour) {
                wakeHour += 24;
            }
        }

        if (sleepHour >= 23 && wakeHour >= 9) {
            return Chronotype.NIGHT_OWL;
        } else if (sleepHour < 22 && wakeHour < 7) {
            return Chronotype.EARLY_BIRD;
        } else {
            return Chronotype.DOVE;
        }
    }

    private Chronotype determineDominantChronotype(Map<Chronotype, Long> counts) {
        if (counts.isEmpty()) {
            return Chronotype.DOVE;
        }

        long maxCount = counts.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        List<Chronotype> dominantTypes = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (dominantTypes.size() > 1) {
            return Chronotype.DOVE;
        }

        return dominantTypes.get(0);
    }
}