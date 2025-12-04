package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.util.NightUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", Chronotype.DOVE);
        }

        Map<Chronotype, Long> chronotypeCounts = sessions.stream()
                .filter(NightUtils::overlapsNightInterval)
                .map(this::determineChronotypeForSession)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        Chronotype dominantChronotype = determineDominantChronotype(chronotypeCounts);
        return new SleepAnalysisResult("Хронотип пользователя", dominantChronotype);
    }

    private Chronotype determineChronotypeForSession(SleepingSession session) {
        LocalTime sleepTime = session.getSleepStart().toLocalTime();
        LocalTime wakeTime = session.getSleepEnd().toLocalTime();

        LocalTime owlSleepThreshold = LocalTime.of(23, 0);
        LocalTime owlWakeThreshold = LocalTime.of(9, 0);
        LocalTime birdSleepThreshold = LocalTime.of(22, 0);
        LocalTime birdWakeThreshold = LocalTime.of(7, 0);

        boolean sleepAfter23 = sleepTime.isAfter(owlSleepThreshold);
        boolean wakeAfter9 = wakeTime.isAfter(owlWakeThreshold);
        boolean differentDay = session.getSleepEnd().toLocalDate().isAfter(session.getSleepStart().toLocalDate());

        boolean isOwl = sleepAfter23 && wakeAfter9;

        boolean isBird = sleepTime.isBefore(birdSleepThreshold) &&
                wakeTime.isBefore(birdWakeThreshold);

        if (isOwl) {
            return Chronotype.NIGHT_OWL;
        } else if (isBird) {
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
                .toList();

        if (dominantTypes.size() > 1) {
            return Chronotype.DOVE;
        }

        return dominantTypes.get(0);
    }
}