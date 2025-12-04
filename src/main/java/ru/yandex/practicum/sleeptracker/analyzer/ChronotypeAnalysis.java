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
                .filter(NightUtils::overlapsNightInterval)
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

    private Chronotype determineChronotypeForSession(SleepingSession session) {
        int sleepHour = session.getSleepStart().getHour();
        int sleepMinute = session.getSleepStart().getMinute();
        int wakeHour = session.getSleepEnd().getHour();
        int wakeMinute = session.getSleepEnd().getMinute();

        double sleepTime = sleepHour + sleepMinute / 60.0;
        double wakeTime = wakeHour + wakeMinute / 60.0;

        boolean differentDay = !session.getSleepStart().toLocalDate().equals(session.getSleepEnd().toLocalDate());

        double adjustedWakeTime = wakeTime;
        if (differentDay) {
            adjustedWakeTime = wakeTime + 24;
        }

        if (sleepTime >= OWL_SLEEP_HOUR && adjustedWakeTime > OWL_WAKE_HOUR) {
            return Chronotype.NIGHT_OWL;
        } else if (sleepTime < BIRD_SLEEP_HOUR && wakeTime < BIRD_WAKE_HOUR) {
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