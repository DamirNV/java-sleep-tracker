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
    @DisplayName("Должен выбросить исключение если время окончания раньше начала")
    void testConstructor_EndBeforeStart_ThrowsException() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 2, 8, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 1, 22, 15);

        assertThrows(IllegalArgumentException.class, () -> {
            new SleepingSession(start, end, SleepQuality.GOOD);
        });
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
    @DisplayName("Должен правильно вычислить продолжительность короткого сна")
    void testGetDurationInMinutesShort() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 14, 0),
                LocalDateTime.of(2025, 10, 1, 14, 30),
                SleepQuality.NORMAL
        );

        assertEquals(30, session.getDurationInMinutes());
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