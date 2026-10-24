package com.smartcampus.rescue;

/** Choices on the optional private check-in form. */
public enum StudentFeeling {
    BUSY_WITH_STUDIES("Busy with classes or assignments"),
    UNHAPPY_WITH_FOOD("Unhappy with available food"),
    TIRED_OR_LOW_ENERGY("Feeling tired or low on energy"),
    WANT_SUPPORT("Would like advice or support"),
    PREFER_NOT_TO_SAY("Prefer not to share");

    private final String label;
    StudentFeeling(String label) { this.label = label; }
    public String getLabel() { return label; }
}
