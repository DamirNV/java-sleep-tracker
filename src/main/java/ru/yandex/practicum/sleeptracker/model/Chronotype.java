package ru.yandex.practicum.sleeptracker.model;

public enum Chronotype {
    EARLY_BIRD("Жаворонок"),
    NIGHT_OWL("Сова"),
    DOVE("Голубь");

    private final String displayName;

    Chronotype(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}