package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    EARLY_BIRD("Жаворонок"),     // рано ложится, рано встает
    NIGHT_OWL("Сова"),           // поздно ложится, поздно встает
    DOVE("Голубь");              // промежуточный тип

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