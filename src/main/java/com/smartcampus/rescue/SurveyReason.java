package com.smartcampus.rescue;

/** Optional reason given when a student does not eat or finish a dish. */
public enum SurveyReason {
    TASTE("Taste"),
    FOOD_QUALITY("Food quality or temperature"),
    NOT_ENOUGH_TIME("Not enough time"),
    NOT_HUNGRY("Not hungry"),
    PORTION_SIZE("Portion size"),
    OTHER("Other"),
    NONE("No reason selected");

    private final String label;
    SurveyReason(String label) { this.label = label; }
    public String getLabel() { return label; }
}
