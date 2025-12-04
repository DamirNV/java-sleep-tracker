package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.util.NightUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements SleepAnalysisFunction {

    private static final int OWL_SLEEP_HOUR = 23;
    private static final int OWL_WAKE_HOUR = 9;
    private static final int BIRD_SLEEP_HOUR = 22;
    private static final int BIRD_WAKE_HOUR = 7;

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
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
        return NightUtils.overlapsNightInterval(
                session.getSleepStart(),
                session.getSleepEnd()
        );
    }

    private Chronotype determineChronotypeForSession(SleepingSession session) {
        int sleepHour = session.getSleepStart().getHour();
        int wakeHour = session.getSleepEnd().getHour();

        if (!session.getSleepStart().toLocalDate().equals(session.getSleepEnd().toLocalDate())) {
            wakeHour += 24;
        }

        if (sleepHour >= OWL_SLEEP_HOUR && wakeHour >= (OWL_WAKE_HOUR + 24)) {
            return Chronotype.NIGHT_OWL;
        } else if (sleepHour < BIRD_SLEEP_HOUR && wakeHour < (BIRD_WAKE_HOUR + 24)) {
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