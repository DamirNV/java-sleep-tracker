package ru.yandex.practicum.sleeptracker.model;

public class SleepAnalysisResult {
    private final String description;
    private final Object result;

    public SleepAnalysisResult(String description, Object result) {
        this.description = description;
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        if (result instanceof String) {
            return String.format("%s: %s", description, result);
        }
        return String.format("%s: %s", description, result.toString());
    }
}
