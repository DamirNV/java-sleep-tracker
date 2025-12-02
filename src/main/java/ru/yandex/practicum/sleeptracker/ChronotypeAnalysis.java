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

        // Фильтруем только ночные сессии (игнорируем дневные и бессонные)
        Map<Chronotype, Long> chronotypeCounts = sessions.stream()
                .filter(this::isNightSessionForChronotype)
                .map(this::determineChronotypeForSession)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        // Определяем доминирующий хронотип
        Chronotype dominantChronotype = determineDominantChronotype(chronotypeCounts);

        return new SleepAnalysisResult(
                "Хронотип пользователя",
                dominantChronotype.getDisplayName()
        );
    }

    // Проверяет, подходит ли сессия для определения хронотипа
    private boolean isNightSessionForChronotype(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        // Игнорируем дневные сессии (начались и закончились в один день после 6 утра)
        if (sleepStart.toLocalDate().equals(sleepEnd.toLocalDate()) &&
                sleepStart.getHour() >= 6) {
            return false;
        }

        // Игнорируем слишком короткие сессии (менее 4 часов)
        long durationHours = java.time.Duration.between(sleepStart, sleepEnd).toHours();
        if (durationHours < 4) {
            return false;
        }

        return true;
    }

    // Определяет хронотип для одной сессии
    private Chronotype determineChronotypeForSession(SleepingSession session) {
        int sleepHour = session.getSleepStart().getHour();
        int wakeHour = session.getSleepEnd().getHour();

        // Если сон переходит через полночь, корректируем время пробуждения
        if (!session.getSleepStart().toLocalDate().equals(session.getSleepEnd().toLocalDate())) {
            wakeHour += 24;
        }

        // "Сова" - засыпает после 23:00, просыпается после 9:00
        if (sleepHour >= 23 && wakeHour >= 9) {
            return Chronotype.NIGHT_OWL;
        }
        // "Жаворонок" - засыпает до 22:00, просыпается до 7:00
        else if (sleepHour < 22 && wakeHour < 7) {
            return Chronotype.EARLY_BIRD;
        }
        // "Голубь" - во всех остальных случаях
        else {
            return Chronotype.DOVE;
        }
    }

    // Определяет доминирующий хронотип
    private Chronotype determineDominantChronotype(Map<Chronotype, Long> counts) {
        if (counts.isEmpty()) {
            return Chronotype.DOVE; // по умолчанию
        }

        // Находим максимальное количество
        long maxCount = counts.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        // Проверяем, есть ли несколько хронотипов с максимальным количеством
        List<Chronotype> dominantTypes = counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Если есть сомнения или несколько типов - возвращаем "Голубя"
        if (dominantTypes.size() > 1) {
            return Chronotype.DOVE;
        }

        return dominantTypes.get(0);
    }
}