package ru.yandex.practicum.sleeptracker.analyzer;

import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotalSessionsAnalysisTest {

    private final TotalSessionsAnalysis analyzer = new TotalSessionsAnalysis();

    @Test
    @DisplayName("Должен правильно подсчитать несколько сессий сна")
    void testAnalyzeWithMultipleSessions() {
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

        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(2, result.getResult());
        assertEquals("Общее количество сессий сна: 2", result.toString());
    }

    @Test
    @DisplayName("Должен вернуть 0 для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());

        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(0, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно подсчитать одну сессию сна")
    void testAnalyzeWithSingleSession() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(1, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно подсчитать много сессий сна")
    void testAnalyzeWithManySessions() {
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
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 30),
                        LocalDateTime.of(2025, 10, 4, 5, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 10, 0),
                        LocalDateTime.of(2025, 10, 4, 11, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(5, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно работать с сессиями разного качества")
    void testAnalyzeWithDifferentQualitySessions() {
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
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 1, 0),
                        LocalDateTime.of(2025, 10, 3, 3, 0),
                        SleepQuality.BAD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Общее количество сессий сна", result.getDescription());
        assertEquals(3, result.getResult());
    }
}