package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.SleeplessNightsAnalysis;
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
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 4, 23, 0),
                        LocalDateTime.of(2025, 10, 5, 5, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(1, result.getResult());
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
        assertEquals(0, result.getResult());
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
        assertEquals(0, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать дневной сон как бессонную ночь")
    void testAnalyzeWithDaytimeSleepOnly() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertTrue((Integer) result.getResult() > 0);
    }

    @Test
    @DisplayName("Должен вернуть 'нет данных' для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals("нет данных", result.getResult());
    }

    @Test
    @DisplayName("Должен корректно обработать null входные данные")
    void testAnalyzeWithNullInput() {
        SleepAnalysisResult result = analyzer.analyze(null);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals("нет данных", result.getResult());
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
        assertEquals(1, result.getResult());
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
        assertEquals(0, result.getResult());
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
        assertEquals(2, result.getResult());
    }
}
