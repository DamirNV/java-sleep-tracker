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
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "нет данных");
        }

        // Получаем диапазон всех ночей
        LocalDate firstNight = getFirstNightDate(sessions.get(0));
        LocalDate lastNight = sessions.get(sessions.size() - 1).getSleepEnd().toLocalDate();

        // Собираем все ночи со сном
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .filter(this::coversNight)
                .map(this::getNightDate)
                .collect(Collectors.toSet());

        // Считаем все ночи в диапазоне
        long totalNights = countNightsBetween(firstNight, lastNight);

        // Бессонные ночи = все ночи - ночи со сном
        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                Math.max(0, sleeplessNights)
        );
    }

    // Определяет первую ночь для анализа согласно ТЗ
    private LocalDate getFirstNightDate(SleepingSession firstSession) {
        LocalDate sessionDate = firstSession.getSleepStart().toLocalDate();
        LocalTime sessionTime = firstSession.getSleepStart().toLocalTime();

        // Если первая сессия началась после 12 дня, начинаем со следующей ночи
        if (sessionTime.isAfter(LocalTime.NOON)) {
            return sessionDate.plusDays(1);
        }
        // Иначе начинаем с ночи этого дня
        return sessionDate;
    }

    // Проверяет, покрывает ли сессия ночь (0:00-6:00)
    private boolean coversNight(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        // Случай 1: Сон переходит через полночь
        if (!sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            return true;
        }

        // Случай 2: Сон начался до 6 утра
        if (sleepStart.getHour() < 6) {
            return true;
        }

        // Случай 3: Сон начался вечером и закончился после полуночи
        // (уже покрыт случаем 1)

        // Случай 4: Сон полностью внутри интервала 0:00-6:00
        LocalDateTime nightStart = sleepStart.toLocalDate().atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);

        return sleepStart.isBefore(nightEnd) && sleepEnd.isAfter(nightStart);
    }

    // Определяет, к какой ночи относится сессия сна
    private LocalDate getNightDate(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();

        // Если сон начался до 6 утра, это ночь предыдущего дня
        if (sleepStart.getHour() < 6) {
            return sleepStart.toLocalDate().minusDays(1);
        }
        // Иначе это ночь дня начала сна
        return sleepStart.toLocalDate();
    }

    // Считает количество ночей между двумя датами (включительно)
    private long countNightsBetween(LocalDate start, LocalDate end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }
}