package com.smartcampus.rescue;

/** Optional activities and their example points. Values can be changed later. */
public enum PointActivity {
    NUTRITION_CHECK_IN("Weekly nutrition check-in", 10),
    LEARNING_ACTIVITY("Nutrition/wellness learning activity", 10),
    FOOD_FEEDBACK("Cafeteria food feedback survey", 10),
    WELLNESS_SESSION("Voluntary wellness session", 15),
    WASTE_AWARENESS("Food-waste awareness activity", 10);

    private final String label;
    private final int points;

    PointActivity(String label, int points) {
        this.label = label;
        this.points = points;
    }

    public String getLabel() { return label; }
    public int getPoints() { return points; }
}
