package com.smartcampus.rescue;

/** Quantity offered on the sick-student meal form. */
public enum MealQuantity {
    HALF("Half serving", 0.5),
    ONE("1 serving", 1.0),
    TWO("2 servings", 2.0);

    private final String label;
    private final double servings;
    MealQuantity(String label, double servings) { this.label = label; this.servings = servings; }
    public String getLabel() { return label; }
    public double getServings() { return servings; }
}
