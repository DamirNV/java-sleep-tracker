package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;

import static org.junit.jupiter.api.Assertions.*;

class SleepAnalysisResultTest {

    @Test
    @DisplayName("Должен правильно создать результат с числовым значением")
    void testCreationWithNumber() {
        SleepAnalysisResult result = new SleepAnalysisResult("Тест", 42);
        assertEquals("Тест", result.getDescription());
        assertEquals(42, result.getResult());
    }

    @Test
    @DisplayName("Должен правильно создать результат со строковым значением")
    void testCreationWithString() {
        SleepAnalysisResult result = new SleepAnalysisResult("Тест", "значение");
        assertEquals("Тест", result.getDescription());
        assertEquals("значение", result.getResult());
    }

    @Test
    @DisplayName("Должен вернуть корректное строковое представление для числа")
    void testToStringWithNumber() {
        SleepAnalysisResult result = new SleepAnalysisResult("Количество сессий", 10);
        assertEquals("Количество сессий: 10", result.toString());
    }

    @Test
    @DisplayName("Должен вернуть корректное строковое представление для строки")
    void testToStringWithString() {
        SleepAnalysisResult result = new SleepAnalysisResult("Хронотип", "Сова");
        assertEquals("Хронотип: Сова", result.toString());
    }

    @Test
    @DisplayName("Должен вернуть корректное строковое представление для null")
    void testToStringWithNull() {
        SleepAnalysisResult result = new SleepAnalysisResult("Тест", null);
        assertEquals("Тест: нет данных", result.toString()); // Исправлено согласно коду
    }

    @Test
    @DisplayName("Должен вернуть корректное строковое представление для объекта")
    void testToStringWithObject() {
        SleepAnalysisResult result = new SleepAnalysisResult("Тест", new Object() {
            @Override
            public String toString() {
                return "объект";
            }
        });
        assertEquals("Тест: объект", result.toString());
    }

    @Test
    @DisplayName("Должен правильно работать с разными типами данных")
    void testDifferentDataTypes() {
        SleepAnalysisResult intResult = new SleepAnalysisResult("Целое", 100);
        SleepAnalysisResult doubleResult = new SleepAnalysisResult("Дробное", 3.14);
        SleepAnalysisResult boolResult = new SleepAnalysisResult("Булево", true);
        SleepAnalysisResult nullResult = new SleepAnalysisResult("Пусто", null);

        assertEquals(100, intResult.getResult());
        assertEquals(3.14, doubleResult.getResult());
        assertEquals(true, boolResult.getResult());
        assertNull(nullResult.getResult());
    }

    @Test
    @DisplayName("Должен вернуть строковое представление с форматированием для double")
    void testToStringWithDouble() {
        SleepAnalysisResult result = new SleepAnalysisResult("Среднее", 123.456);
        assertEquals("Среднее: 123.456", result.toString());
    }
}