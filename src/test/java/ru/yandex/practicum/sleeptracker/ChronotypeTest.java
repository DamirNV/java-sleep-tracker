package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.Chronotype;

import static org.junit.jupiter.api.Assertions.*;

class ChronotypeTest {

    @Test
    @DisplayName("Должен вернуть правильное отображаемое имя для СОВЫ")
    void testNightOwlDisplayName() {
        assertEquals("Сова", Chronotype.NIGHT_OWL.getDisplayName());
    }

    @Test
    @DisplayName("Должен вернуть правильное отображаемое имя для ЖАВОРОНКА")
    void testEarlyBirdDisplayName() {
        assertEquals("Жаворонок", Chronotype.EARLY_BIRD.getDisplayName());
    }

    @Test
    @DisplayName("Должен вернуть правильное отображаемое имя для ГОЛУБЯ")
    void testDoveDisplayName() {
        assertEquals("Голубь", Chronotype.DOVE.getDisplayName());
    }

    @Test
    @DisplayName("Должен вернуть правильное строковое представление")
    void testToString() {
        assertEquals("Сова", Chronotype.NIGHT_OWL.toString());
        assertEquals("Жаворонок", Chronotype.EARLY_BIRD.toString());
        assertEquals("Голубь", Chronotype.DOVE.toString());
    }

    @Test
    @DisplayName("Должен содержать все три типа хронотипов")
    void testEnumValues() {
        Chronotype[] values = Chronotype.values();

        assertEquals(3, values.length);
        assertArrayEquals(new Chronotype[]{
                Chronotype.EARLY_BIRD,
                Chronotype.NIGHT_OWL,
                Chronotype.DOVE
        }, values);
    }

    @Test
    @DisplayName("Должен правильно преобразовывать строку в enum")
    void testValueOf() {
        assertEquals(Chronotype.EARLY_BIRD, Chronotype.valueOf("EARLY_BIRD"));
        assertEquals(Chronotype.NIGHT_OWL, Chronotype.valueOf("NIGHT_OWL"));
        assertEquals(Chronotype.DOVE, Chronotype.valueOf("DOVE"));
    }
}