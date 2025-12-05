package ru.yandex.practicum.sleeptracker.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class SleepQualityTest {

    @Test
    @DisplayName("Должен содержать все три типа качества сна")
    void testEnumValues() {
        SleepQuality[] values = SleepQuality.values();

        assertEquals(3, values.length);
        assertTrue(contains(values, SleepQuality.GOOD));
        assertTrue(contains(values, SleepQuality.NORMAL));
        assertTrue(contains(values, SleepQuality.BAD));
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

    private boolean contains(SleepQuality[] values, SleepQuality type) {
        for (SleepQuality value : values) {
            if (value == type) {
                return true;
            }
        }
        return false;
    }
}