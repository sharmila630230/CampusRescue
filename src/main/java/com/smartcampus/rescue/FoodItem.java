package com.smartcampus.rescue;

import java.io.Serializable;

/** A dish in the cafeteria catalogue. Nutrition values are stored per 100 g. */
public final class FoodItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String category;
    private final NutritionFacts nutritionPer100g;
    private boolean available;

    public FoodItem(String id, String name, String category,
                    NutritionFacts nutritionPer100g, boolean available) {
        this.id = requireText("Food ID", id);
        this.name = requireText("Food name", name);
        this.category = requireText("Food category", category);
        if (nutritionPer100g == null) {
            throw new IllegalArgumentException("Nutrition values are required.");
        }
        this.nutritionPer100g = nutritionPer100g;
        this.available = available;
    }

    private static String requireText(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public NutritionFacts getNutritionPer100g() { return nutritionPer100g; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public NutritionFacts estimateForGrams(double grams) {
        return nutritionPer100g.forGrams(grams);
    }
}
