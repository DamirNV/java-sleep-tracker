package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import static org.junit.jupiter.api.Assertions.*;

class SleepQualityTest {

    @Test
    @DisplayName("Должен содержать все три типа качества сна")
    void testEnumValues() {
        SleepQuality[] values = SleepQuality.values();

        assertEquals(3, values.length);
        assertArrayEquals(new SleepQuality[]{
                SleepQuality.GOOD,
                SleepQuality.NORMAL,
                SleepQuality.BAD
        }, values);
    }

    @Test
    @DisplayName("Должен правильно преобразовывать строку в enum")
    void testValueOf() {
        assertEquals(SleepQuality.GOOD, SleepQuality.valueOf("GOOD"));
        assertEquals(SleepQuality.NORMAL, SleepQuality.valueOf("NORMAL"));
        assertEquals(SleepQuality.BAD, SleepQuality.valueOf("BAD"));
    }

    @Test
    @DisplayName("Должен правильно работать с именами значений")
    void testEnumNames() {
        assertEquals("GOOD", SleepQuality.GOOD.name());
        assertEquals("NORMAL", SleepQuality.NORMAL.name());
        assertEquals("BAD", SleepQuality.BAD.name());
    }
}
