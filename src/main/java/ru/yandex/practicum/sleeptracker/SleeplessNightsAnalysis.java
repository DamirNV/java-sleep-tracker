package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SleeplessNightsAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", "нет данных");
        }

        // Определяем диапазон дат
        LocalDate firstDate = sessions.get(0).getSleepStart().toLocalDate();
        LocalDate lastDate = sessions.get(sessions.size() - 1).getSleepEnd().toLocalDate();

        // Корректируем первую дату согласно ТЗ
        if (sessions.get(0).getSleepStart().toLocalTime().isAfter(LocalTime.NOON)) {
            firstDate = firstDate.plusDays(1); // начали после 12 дня - следующая ночь
        }

        // Собираем все ночи со сном
        Set<LocalDate> nightsWithSleep = new HashSet<>();

        // ВРЕМЕННО используем цикл для отладки, потом заменим на Stream API
        for (SleepingSession session : sessions) {
            if (isNightSleepSession(session)) {
                // Добавляем ночь сна
                LocalDate nightDate = getNightDateForSession(session);
                nightsWithSleep.add(nightDate);
            }
        }

        // Считаем все ночи в диапазоне
        long totalNights = 0;
        LocalDate currentDate = firstDate;

        while (!currentDate.isAfter(lastDate)) {
            totalNights++;
            currentDate = currentDate.plusDays(1);
        }

        // Бессонные ночи = все ночи - ночи со сном
        long sleeplessNights = totalNights - nightsWithSleep.size();

        return new SleepAnalysisResult(
                "Количество бессонных ночей",
                Math.max(0, sleeplessNights)
        );
    }

    private boolean isNightSleepSession(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();
        LocalDateTime sleepEnd = session.getSleepEnd();

        // Если сон начался и закончился в один день
        if (sleepStart.toLocalDate().equals(sleepEnd.toLocalDate())) {
            // Проверяем пересечение с интервалом 0:00-6:00
            LocalDateTime nightStart = sleepStart.toLocalDate().atStartOfDay(); // 00:00
            LocalDateTime nightEnd = nightStart.plusHours(6); // 06:00

            return !sleepEnd.isBefore(nightStart) && !sleepStart.isAfter(nightEnd);
        }
        // Если сон переходит через полночь
        else {
            return true; // любой сон через полночь покрывает ночь
        }
    }

    private LocalDate getNightDateForSession(SleepingSession session) {
        LocalDateTime sleepStart = session.getSleepStart();

        // Если сон начался до 6 утра, это ночь предыдущего дня
        if (sleepStart.getHour() < 6) {
            return sleepStart.toLocalDate().minusDays(1);
        }
        // Иначе это ночь дня начала сна
        else {
            return sleepStart.toLocalDate();
        }
    }
}