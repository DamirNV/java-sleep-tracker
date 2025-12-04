package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzer.*;
import ru.yandex.practicum.sleeptracker.app.SleepTrackerApp;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Должен правильно загрузить сессии из файла")
    void testLoadSleepSessionsFromFile() throws IOException {
        String testData = "01.10.25 22:15;02.10.25 08:00;GOOD\n" +
                "02.10.25 23:00;03.10.25 08:00;NORMAL";

        Path testFile = tempDir.resolve("test_sleep.txt");
        Files.writeString(testFile, testData);

        List<SleepingSession> sessions = SleepTrackerApp.loadSleepSessions(testFile.toString());

        assertNotNull(sessions);
        assertEquals(2, sessions.size());
        assertEquals(SleepQuality.GOOD, sessions.get(0).getQuality());
        assertEquals(SleepQuality.NORMAL, sessions.get(1).getQuality());
    }

    @Test
    @DisplayName("Должен вернуть пустой список для пустого файла")
    void testLoadSleepSessionsEmptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.writeString(emptyFile, "");

        List<SleepingSession> sessions = SleepTrackerApp.loadSleepSessions(emptyFile.toString());

        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }

    @Test
    @DisplayName("Должен проигнорировать строки с неверным форматом")
    void testLoadSleepSessionsWithInvalidLines() throws IOException {
        String testData = "01.10.25 22:15;02.10.25 08:00;GOOD\n" +
                "invalid line\n" +
                "02.10.25 23:00;03.10.25 08:00;NORMAL";

        Path testFile = tempDir.resolve("test_invalid.txt");
        Files.writeString(testFile, testData);

        List<SleepingSession> sessions = SleepTrackerApp.loadSleepSessions(testFile.toString());

        assertNotNull(sessions);
        assertEquals(2, sessions.size());
    }

    @Test
    @DisplayName("Должен выбросить исключение при отсутствии файла")
    void testLoadSleepSessionsFileNotFound() {
        String nonExistentFile = tempDir.resolve("non_existent.txt").toString();

        assertThrows(IOException.class, () -> {
            SleepTrackerApp.loadSleepSessions(nonExistentFile);
        });
    }

    @Test
    @DisplayName("Должен правильно добавить новую аналитическую функцию")
    void testAddAnalysisFunction() {
        SleepTrackerApp app = new SleepTrackerApp();

        int initialCount = app.getAnalysisFunctions().size();

        SleepAnalysisFunction customFunction = sessions ->
                new SleepAnalysisResult("Тестовая функция", "тест");

        app.addAnalysisFunction(customFunction);

        assertEquals(initialCount + 1, app.getAnalysisFunctions().size());
    }

    @Test
    @DisplayName("Должен содержать все стандартные аналитические функции")
    void testDefaultAnalysisFunctions() {
        SleepTrackerApp app = new SleepTrackerApp();
        List<SleepAnalysisFunction> functions = app.getAnalysisFunctions();

        assertFalse(functions.isEmpty());
        assertEquals(7, functions.size());

        long totalSessionsCount = functions.stream()
                .filter(f -> f instanceof TotalSessionsAnalysis)
                .count();
        long minDurationCount = functions.stream()
                .filter(f -> f instanceof MinDurationAnalysis)
                .count();
        long maxDurationCount = functions.stream()
                .filter(f -> f instanceof MaxDurationAnalysis)
                .count();
        long averageDurationCount = functions.stream()
                .filter(f -> f instanceof AverageDurationAnalysis)
                .count();
        long badQualityCount = functions.stream()
                .filter(f -> f instanceof BadQualitySessionsAnalysis)
                .count();
        long sleeplessNightsCount = functions.stream()
                .filter(f -> f instanceof SleeplessNightsAnalysis)
                .count();
        long chronotypeCount = functions.stream()
                .filter(f -> f instanceof ChronotypeAnalysis)
                .count();

        assertEquals(1, totalSessionsCount);
        assertEquals(1, minDurationCount);
        assertEquals(1, maxDurationCount);
        assertEquals(1, averageDurationCount);
        assertEquals(1, badQualityCount);
        assertEquals(1, sleeplessNightsCount);
        assertEquals(1, chronotypeCount);
    }

    @Test
    @DisplayName("Должен правильно загрузить файл с различными форматами качеств сна")
    void testLoadSleepSessionsWithDifferentQualities() throws IOException {
        String testData = "01.10.25 22:15;02.10.25 08:00;GOOD\n" +
                "02.10.25 23:00;03.10.25 08:00;NORMAL\n" +
                "03.10.25 23:30;04.10.25 06:20;BAD";

        Path testFile = tempDir.resolve("test_qualities.txt");
        Files.writeString(testFile, testData);

        List<SleepingSession> sessions = SleepTrackerApp.loadSleepSessions(testFile.toString());

        assertEquals(3, sessions.size());
        assertEquals(SleepQuality.GOOD, sessions.get(0).getQuality());
        assertEquals(SleepQuality.NORMAL, sessions.get(1).getQuality());
        assertEquals(SleepQuality.BAD, sessions.get(2).getQuality());
    }

    @Test
    @DisplayName("Должен правильно обработать файл с пробельными строками")
    void testLoadSleepSessionsWithWhitespace() throws IOException {
        String testData = "  01.10.25 22:15  ;  02.10.25 08:00  ;  GOOD  \n" +
                "\n" +
                "  02.10.25 23:00  ;  03.10.25 08:00  ;  NORMAL  ";

        Path testFile = tempDir.resolve("test_whitespace.txt");
        Files.writeString(testFile, testData);

        List<SleepingSession> sessions = SleepTrackerApp.loadSleepSessions(testFile.toString());

        assertEquals(2, sessions.size());
        assertEquals(SleepQuality.GOOD, sessions.get(0).getQuality());
        assertEquals(SleepQuality.NORMAL, sessions.get(1).getQuality());
    }

    @Test
    @DisplayName("Геттер должен возвращать неизменяемый список")
    void testGetAnalysisFunctionsReturnsUnmodifiableList() {
        SleepTrackerApp app = new SleepTrackerApp();
        List<SleepAnalysisFunction> functions = app.getAnalysisFunctions();

        assertThrows(UnsupportedOperationException.class, () -> {
            functions.add(sessions -> new SleepAnalysisResult("Новая", "функция"));
        });
    }

    @Test
    @DisplayName("Должен корректно работать с геттером после добавления функции")
    void testGetAnalysisFunctionsAfterAddingFunction() {
        SleepTrackerApp app = new SleepTrackerApp();
        int initialSize = app.getAnalysisFunctions().size();

        SleepAnalysisFunction newFunction = sessions ->
                new SleepAnalysisResult("Дополнительная", "функция");

        app.addAnalysisFunction(newFunction);

        assertEquals(initialSize + 1, app.getAnalysisFunctions().size());

        SleepAnalysisFunction lastFunction = app.getAnalysisFunctions()
                .get(app.getAnalysisFunctions().size() - 1);

        SleepAnalysisResult result = lastFunction.analyze(List.of());
        assertEquals("Дополнительная", result.getDescription());
        assertEquals("функция", result.getResult());
    }
}