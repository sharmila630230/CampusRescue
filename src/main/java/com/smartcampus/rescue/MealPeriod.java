package com.smartcampus.rescue;

/** Campus meal times used for waste reports. */
public enum MealPeriod {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    EVENING_SNACK("Evening snack"),
    DINNER("Dinner");

    private final String label;
    MealPeriod(String label) { this.label = label; }
    public String getLabel() { return label; }
}
