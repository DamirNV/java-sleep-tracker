package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SleepingSessionTest {

    @Test
    @DisplayName("Должен правильно создать сессию сна")
    void testCreation() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 1, 22, 15);
        LocalDateTime end = LocalDateTime.of(2025, 10, 2, 8, 0);
        SleepingSession session = new SleepingSession(start, end, SleepQuality.GOOD);

        assertEquals(start, session.getSleepStart());
        assertEquals(end, session.getSleepEnd());
        assertEquals(SleepQuality.GOOD, session.getQuality());
    }

    @Test
    @DisplayName("Должен правильно вычислить продолжительность в минутах")
    void testGetDurationInMinutes() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 0),
                LocalDateTime.of(2025, 10, 2, 6, 0),
                SleepQuality.GOOD
        );

        assertEquals(480, session.getDurationInMinutes());
    }

    @Test
    @DisplayName("Должен определить ночной сон при переходе через полночь")
    void testIsNightSleepCrossMidnight() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 1, 30),
                SleepQuality.GOOD
        );

        assertTrue(session.isNightSleep());
    }

    @Test
    @DisplayName("Должен определить ночной сон при начале до 6 утра")
    void testIsNightSleepStartBefore6AM() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 5, 30),
                LocalDateTime.of(2025, 10, 1, 7, 0),
                SleepQuality.GOOD
        );

        assertTrue(session.isNightSleep());
    }

    @Test
    @DisplayName("Должен определить дневной сон как не ночной")
    void testIsNightSleepDaytime() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 14, 0),
                LocalDateTime.of(2025, 10, 1, 15, 0),
                SleepQuality.NORMAL
        );

        assertFalse(session.isNightSleep());
    }

    @Test
    @DisplayName("Должен определить пересечение с ночным интервалом (00:00-06:00)")
    void testOverlapsNightIntervalInsideNight() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 1, 0),
                LocalDateTime.of(2025, 10, 1, 5, 0),
                SleepQuality.GOOD
        );

        assertTrue(session.overlapsNightInterval());
    }

    @Test
    @DisplayName("Должен определить пересечение с ночным интервалом при частичном перекрытии")
    void testOverlapsNightIntervalPartialOverlap() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 5, 30),
                LocalDateTime.of(2025, 10, 1, 7, 0),
                SleepQuality.GOOD
        );

        assertTrue(session.overlapsNightInterval());
    }

    @Test
    @DisplayName("Должен определить отсутствие пересечения с ночным интервалом")
    void testOverlapsNightIntervalNoOverlap() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 14, 0),
                LocalDateTime.of(2025, 10, 1, 15, 0),
                SleepQuality.NORMAL
        );

        assertFalse(session.overlapsNightInterval());
    }

    @Test
    @DisplayName("Должен определить пересечение с ночным интервалом при переходе через полночь")
    void testOverlapsNightIntervalCrossMidnight() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 0, 30),
                SleepQuality.GOOD
        );

        assertTrue(session.overlapsNightInterval());
    }

    @Test
    @DisplayName("Должен вернуть корректное строковое представление")
    void testToString() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 15),
                LocalDateTime.of(2025, 10, 2, 8, 0),
                SleepQuality.GOOD
        );

        String result = session.toString();
        assertTrue(result.contains("start=2025-10-01T22:15"));
        assertTrue(result.contains("end=2025-10-02T08:00"));
        assertTrue(result.contains("quality=GOOD"));
    }
}