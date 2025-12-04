package ru.yandex.practicum.sleeptracker.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.LocalDateTime;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class NightUtilsTest {

    @Test
    @DisplayName("Должен определить пересечение с ночью для ночной сессии")
    void testOverlapsNightInterval_WithNightSession() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 0),
                LocalDateTime.of(2025, 10, 2, 7, 0),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить отсутствие пересечения для дневной сессии")
    void testOverlapsNightInterval_WithDaySession() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 14, 0),
                LocalDateTime.of(2025, 10, 1, 15, 0),
                SleepQuality.NORMAL
        );

        assertFalse(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить пересечение для сессии через полночь")
    void testOverlapsNightInterval_CrossMidnight() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 30),
                LocalDateTime.of(2025, 10, 2, 0, 30),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить пересечение для сессии рано утром")
    void testOverlapsNightInterval_EarlyMorning() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 5, 30),
                LocalDateTime.of(2025, 10, 1, 6, 30),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить ночь для времени до 12:00")
    void testGetNightDate_BeforeNoon() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 2, 8, 0);
        LocalDate nightDate = NightUtils.getNightDate(time);

        assertEquals(LocalDate.of(2025, 10, 1), nightDate);
    }

    @Test
    @DisplayName("Должен определить ночь для времени после 12:00")
    void testGetNightDate_AfterNoon() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 2, 14, 0);
        LocalDate nightDate = NightUtils.getNightDate(time);

        assertEquals(LocalDate.of(2025, 10, 2), nightDate);
    }

    @Test
    @DisplayName("Должен определить ночь для времени ровно в 12:00")
    void testGetNightDate_ExactlyNoon() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 2, 12, 0);
        LocalDate nightDate = NightUtils.getNightDate(time);

        assertEquals(LocalDate.of(2025, 10, 2), nightDate);
    }

    @Test
    @DisplayName("Должен определить ночь для сессии с началом до 6 утра")
    void testOverlapsNightInterval_StartBefore6AM() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 4, 0),
                LocalDateTime.of(2025, 10, 1, 8, 0),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить отсутствие пересечения для сессии после 6 утра")
    void testOverlapsNightInterval_StartAfter6AM() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 6, 1),
                LocalDateTime.of(2025, 10, 1, 7, 0),
                SleepQuality.GOOD
        );

        assertFalse(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить пересечение для длинной сессии через несколько дней")
    void testOverlapsNightInterval_LongSessionMultipleDays() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 20, 0),
                LocalDateTime.of(2025, 10, 3, 10, 0),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить ночь для сессии из SleepingSession")
    void testGetNightDate_FromSleepingSession() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 2, 8, 0),
                LocalDateTime.of(2025, 10, 2, 9, 0),
                SleepQuality.GOOD
        );

        LocalDate nightDate = NightUtils.getNightDate(session);
        assertEquals(LocalDate.of(2025, 10, 1), nightDate);
    }

    @Test
    @DisplayName("Должен определить пересечение для сессии граничащей с 00:00")
    void testOverlapsNightInterval_BorderMidnight() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 23, 59),
                LocalDateTime.of(2025, 10, 2, 0, 1),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить отсутствие пересечения для сессии граничащей с 06:00")
    void testOverlapsNightInterval_Border6AM() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 5, 59),
                LocalDateTime.of(2025, 10, 1, 6, 0),
                SleepQuality.GOOD
        );

        assertTrue(NightUtils.overlapsNightInterval(session));
    }

    @Test
    @DisplayName("Должен определить ночь для времени ровно в 11:59")
    void testGetNightDate_ExactlyBeforeNoon() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 2, 11, 59);
        LocalDate nightDate = NightUtils.getNightDate(time);

        assertEquals(LocalDate.of(2025, 10, 1), nightDate);
    }

    @Test
    @DisplayName("Должен определить ночь для времени ровно в 12:01")
    void testGetNightDate_ExactlyAfterNoon() {
        LocalDateTime time = LocalDateTime.of(2025, 10, 2, 12, 1);
        LocalDate nightDate = NightUtils.getNightDate(time);

        assertEquals(LocalDate.of(2025, 10, 2), nightDate);
    }
}