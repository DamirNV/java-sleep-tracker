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

class SleeplessNightsAnalysisTest {

    private final SleeplessNightsAnalysis analyzer = new SleeplessNightsAnalysis();

    @Test
    @DisplayName("Должен правильно определить бессонные ночи")
    void testAnalyzeWithSleeplessNights() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 14, 0),
                        LocalDateTime.of(2025, 10, 4, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 5, 23, 0),
                        LocalDateTime.of(2025, 10, 6, 5, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(3L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 0 если все ночи со сном")
    void testAnalyzeWithAllNightsWithSleep() {
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
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать сон через полночь")
    void testAnalyzeWithCrossMidnightSleep() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 1, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 0 для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно определить первую ночь при начале после 12 дня")
    void testAnalyzeWithStartAfterNoon() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(1L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно определить первую ночь при начале до 12 дня")
    void testAnalyzeWithStartBeforeNoon() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 8, 0),
                        LocalDateTime.of(2025, 10, 1, 9, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(1L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать короткий ночной сон")
    void testAnalyzeWithShortNightSleep() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 5, 30),
                        LocalDateTime.of(2025, 10, 1, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 0, 30),
                        LocalDateTime.of(2025, 10, 2, 1, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно посчитать несколько бессонных ночей подряд")
    void testAnalyzeWithMultipleSleeplessNights() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 10, 0),
                        LocalDateTime.of(2025, 10, 3, 11, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(2L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать сессию на границе 6:00")
    void testAnalyzeSessionAt6AMBoundary() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 5, 59),
                        LocalDateTime.of(2025, 10, 1, 6, 1),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать несколько сессий в одну ночь")
    void testAnalyzeMultipleSessionsSameNight() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 1, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 2, 0),
                        LocalDateTime.of(2025, 10, 2, 4, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно определить бессонную ночь между сессиями")
    void testAnalyzeSleeplessNightBetweenSessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(2L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать переход через месяц")
    void testAnalyzeCrossMonthBoundary() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 2, 28, 23, 0),
                        LocalDateTime.of(2025, 3, 1, 7, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 3, 2, 14, 0),
                        LocalDateTime.of(2025, 3, 2, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(2L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно определить ночь для сессии точно в 12:00")
    void testAnalyzeSessionExactlyAtNoon() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 12, 0),
                        LocalDateTime.of(2025, 10, 1, 13, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(1L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать одну ночную сессию")
    void testAnalyzeSingleNightSession() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать сессию заканчивающуюся точно в 6:00")
    void testAnalyzeSessionEndingExactlyAt6AM() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 4, 0),
                        LocalDateTime.of(2025, 10, 1, 6, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(0L, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать сессию начинающуюся точно в 0:00")
    void testAnalyzeSessionStartingExactlyAtMidnight() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 0, 0),
                        LocalDateTime.of(2025, 10, 1, 4, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals(0L, result.getResult());
    }
}