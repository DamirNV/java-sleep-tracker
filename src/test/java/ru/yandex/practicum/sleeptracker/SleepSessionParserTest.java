package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.util.SleepSessionParser;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class SleepSessionParserTest {

    @Test
    @DisplayName("Должен правильно распарсить валидную строку с GOOD качеством")
    void testParseValidGoodQuality() {
        String line = "01.10.25 22:15;02.10.25 08:00;GOOD";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNotNull(session);
        assertEquals(LocalDateTime.of(2025, 10, 1, 22, 15), session.getSleepStart());
        assertEquals(LocalDateTime.of(2025, 10, 2, 8, 0), session.getSleepEnd());
        assertEquals(SleepQuality.GOOD, session.getQuality());
        assertEquals(585, session.getDurationInMinutes());
    }

    @Test
    @DisplayName("Должен правильно распарсить валидную строку с NORMAL качеством")
    void testParseValidNormalQuality() {
        String line = "02.10.25 23:00;03.10.25 08:00;NORMAL";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNotNull(session);
        assertEquals(LocalDateTime.of(2025, 10, 2, 23, 0), session.getSleepStart());
        assertEquals(LocalDateTime.of(2025, 10, 3, 8, 0), session.getSleepEnd());
        assertEquals(SleepQuality.NORMAL, session.getQuality());
        assertEquals(540, session.getDurationInMinutes());
    }

    @Test
    @DisplayName("Должен правильно распарсить валидную строку с BAD качеством")
    void testParseValidBadQuality() {
        String line = "03.10.25 23:30;04.10.25 06:20;BAD";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNotNull(session);
        assertEquals(LocalDateTime.of(2025, 10, 3, 23, 30), session.getSleepStart());
        assertEquals(LocalDateTime.of(2025, 10, 4, 6, 20), session.getSleepEnd());
        assertEquals(SleepQuality.BAD, session.getQuality());
        assertEquals(410, session.getDurationInMinutes());
    }

    @Test
    @DisplayName("Должен вернуть null при неверном формате строки")
    void testParseInvalidFormat() {
        String line = "01.10.25 22:15;02.10.25 08:00";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null при неверном качестве сна")
    void testParseInvalidQuality() {
        String line = "01.10.25 22:15;02.10.25 08:00;EXCELLENT";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null при неверном формате даты")
    void testParseInvalidDateFormat() {
        String line = "01-10-25 22:15;02-10-25 08:00;GOOD";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null при времени окончания раньше начала")
    void testParseEndTimeBeforeStart() {
        String line = "02.10.25 08:00;01.10.25 22:15;GOOD";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null для null строки")
    void testParseNullString() {
        SleepingSession session = SleepSessionParser.parse(null);

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null для пустой строки")
    void testParseEmptyString() {
        SleepingSession session = SleepSessionParser.parse("");

        assertNull(session);
    }

    @Test
    @DisplayName("Должен вернуть null для строки с пробелами")
    void testParseWhitespaceString() {
        SleepingSession session = SleepSessionParser.parse("   ");

        assertNull(session);
    }

    @Test
    @DisplayName("Должен корректно обработать строку с пробелами вокруг")
    void testParseStringWithSpaces() {
        String line = "  01.10.25 22:15  ;  02.10.25 08:00  ;  GOOD  ";

        SleepingSession session = SleepSessionParser.parse(line);

        assertNotNull(session);
        assertEquals(LocalDateTime.of(2025, 10, 1, 22, 15), session.getSleepStart());
        assertEquals(LocalDateTime.of(2025, 10, 2, 8, 0), session.getSleepEnd());
        assertEquals(SleepQuality.GOOD, session.getQuality());
    }

    @Test
    @DisplayName("Должен вернуть true для валидного формата строки")
    void testIsValidFormatValid() {
        String line = "01.10.25 22:15;02.10.25 08:00;GOOD";

        boolean isValid = SleepSessionParser.isValidFormat(line);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Должен вернуть false для невалидного формата строки")
    void testIsValidFormatInvalid() {
        String line = "01.10.25 22:15;02.10.25 08:00";

        boolean isValid = SleepSessionParser.isValidFormat(line);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Должен вернуть false для null строки")
    void testIsValidFormatNull() {
        boolean isValid = SleepSessionParser.isValidFormat(null);

        assertFalse(isValid);
    }
}
