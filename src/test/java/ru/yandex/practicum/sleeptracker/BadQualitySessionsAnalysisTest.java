package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.BadQualitySessionsAnalysis;
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

class BadQualitySessionsAnalysisTest {

    private final BadQualitySessionsAnalysis analyzer = new BadQualitySessionsAnalysis();

    @Test
    @DisplayName("Должен правильно подсчитать сессии с плохим качеством")
    void testAnalyzeWithMixedQualitySessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 5, 0),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 30),
                        LocalDateTime.of(2025, 10, 4, 4, 30),
                        SleepQuality.BAD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals(2L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 0 если нет сессий с плохим качеством")
    void testAnalyzeWithNoBadQualitySessions() {
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

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 0 для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 'нет данных' для null входных данных")
    void testAnalyzeWithNullInput() {
        SleepAnalysisResult result = analyzer.analyze(null);

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals("нет данных", result.getResult());
    }

    @Test
    @DisplayName("Должен правильно подсчитать все плохие сессии")
    void testAnalyzeWithAllBadQualitySessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 3, 0),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 2, 0),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 0, 30),
                        LocalDateTime.of(2025, 10, 3, 4, 30),
                        SleepQuality.BAD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Количество сессий с плохим качеством сна", result.getDescription());
        assertEquals(3L, result.getResult());
    }
}