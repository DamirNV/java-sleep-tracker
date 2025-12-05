package ru.yandex.practicum.sleeptracker.analyzer;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MaxDurationAnalysisTest {

    private final MaxDurationAnalysis analyzer = new MaxDurationAnalysis();

    @Test
    @DisplayName("Должен найти максимальную продолжительность среди нескольких сессий")
    void testAnalyzeWithMultipleSessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 14, 45),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 9, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(600L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть правильное значение для одной сессии сна")
    void testAnalyzeWithSingleSession() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(480L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 0 для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно считать продолжительность ночного сна")
    void testAnalyzeWithNightSleep() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 30),
                        LocalDateTime.of(2025, 10, 3, 8, 45),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(555L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать одинаковые продолжительности")
    void testAnalyzeWithEqualDurations() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(480L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать очень короткие сессии")
    void testAnalyzeWithShortSessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 14, 30),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 10, 0),
                        LocalDateTime.of(2025, 10, 2, 10, 15),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Максимальная продолжительность сессии (в минутах)", result.getDescription());
        assertEquals(30L, result.getResult());
    }
}