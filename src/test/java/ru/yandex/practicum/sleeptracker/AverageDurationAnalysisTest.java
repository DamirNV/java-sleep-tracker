package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.AverageDurationAnalysis;
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

class AverageDurationAnalysisTest {

    private final AverageDurationAnalysis analyzer = new AverageDurationAnalysis();

    @Test
    @DisplayName("Должен правильно вычислить среднюю продолжительность")
    void testAnalyzeWithMultipleSessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Средняя продолжительность сессии (в минутах)", result.getDescription());
        assertTrue(result.getResult() instanceof String);

        String actual = (String) result.getResult();
        assertEquals("340.0", actual.replace(',', '.'));
    }

    @Test
    @DisplayName("Должен вернуть среднее для одной сессии")
    void testAnalyzeWithSingleSession() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Средняя продолжительность сессии (в минутах)", result.getDescription());
        assertTrue(result.getResult() instanceof String);

        String actual = (String) result.getResult();
        assertEquals("480.0", actual.replace(',', '.'));
    }

    @Test
    @DisplayName("Должен вернуть 'нет данных' для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());

        assertEquals("Средняя продолжительность сессии", result.getDescription());
        assertEquals("нет данных", result.getResult());
    }

    @Test
    @DisplayName("Должен корректно обработать null входные данные")
    void testAnalyzeWithNullInput() {
        SleepAnalysisResult result = analyzer.analyze(null);

        assertEquals("Средняя продолжительность сессии", result.getDescription());
        assertEquals("нет данных", result.getResult());
    }

    @Test
    @DisplayName("Должен правильно округлять среднее значение")
    void testAnalyzeWithDecimalAverage() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 7, 15),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);

        assertEquals("Средняя продолжительность сессии (в минутах)", result.getDescription());
        assertTrue(result.getResult() instanceof String);

        String actual = (String) result.getResult();
        assertEquals("502.5", actual.replace(',', '.'));
    }
}