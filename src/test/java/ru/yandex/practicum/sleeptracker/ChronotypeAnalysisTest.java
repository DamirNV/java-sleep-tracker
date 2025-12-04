package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
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

class ChronotypeAnalysisTest {

    private final ChronotypeAnalysis analyzer = new ChronotypeAnalysis();

    @Test
    @DisplayName("Должен определить СОВУ при позднем засыпании и пробуждении")
    void testAnalyzeNightOwl() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Сова", result.getResult());
    }

    @Test
    @DisplayName("Должен определить ЖАВОРОНКА при раннем засыпании и пробуждении")
    void testAnalyzeEarlyBird() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Жаворонок", result.getResult());
    }

    @Test
    @DisplayName("Должен определить ГОЛУБЯ при смешанном режиме")
    void testAnalyzeDove() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 30),
                        LocalDateTime.of(2025, 10, 2, 7, 30),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Голубь", result.getResult());
    }

    @Test
    @DisplayName("Должен игнорировать дневные сессии сна")
    void testAnalyzeIgnoreDaytimeSessions() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 9, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Сова", result.getResult());
    }

    @Test
    @DisplayName("Должен игнорировать короткие сессии сна (<4 часов)")
    void testAnalyzeIgnoreShortSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Жаворонок", result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть ГОЛУБЯ при равенстве типов")
    void testAnalyzeEqualTypes() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 0),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("Голубь", result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть 'недостаточно данных' для пустого списка")
    void testAnalyzeWithEmptyList() {
        SleepAnalysisResult result = analyzer.analyze(Collections.emptyList());
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("недостаточно данных", result.getResult());
    }

    @Test
    @DisplayName("Должен корректно обработать null входные данные")
    void testAnalyzeWithNullInput() {
        SleepAnalysisResult result = analyzer.analyze(null);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals("недостаточно данных", result.getResult());
    }

    @Test
    @DisplayName("Должен правильно обработать сон через полночь")
    void testAnalyzeCrossMidnightSleep() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 45),
                        LocalDateTime.of(2025, 10, 2, 9, 15), // изменил с 8:15 на 9:15
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result = analyzer.analyze(sessions);
        assertEquals("Сова", result.getResult());
    }

    @Test
    @DisplayName("Должен правильно определить хронотип на граничных значениях времени")
    void testAnalyzeBorderlineCases() {
        List<SleepingSession> earlyBirdBorderline = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 59),
                        LocalDateTime.of(2025, 10, 2, 6, 59),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result1 = analyzer.analyze(earlyBirdBorderline);
        assertEquals("Жаворонок", result1.getResult());

        List<SleepingSession> nightOwlBorderline = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 1),
                        LocalDateTime.of(2025, 10, 2, 9, 1),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult result2 = analyzer.analyze(nightOwlBorderline);
        assertEquals("Сова", result2.getResult());
    }
}