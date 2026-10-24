package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** Aggregate kitchen waste record; it is not tied to an individual student. */
public final class WasteRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String foodId;
    private final MealPeriod mealPeriod;
    private final double gramsPrepared;
    private final double gramsServed;
    private final double gramsPlateLeftover;
    private final LocalDateTime recordedAt;

    public WasteRecord(String foodId, MealPeriod mealPeriod, double gramsPrepared,
                       double gramsServed, double gramsPlateLeftover) {
        if (foodId == null || foodId.isBlank()) throw new IllegalArgumentException("Food ID is required.");
        if (mealPeriod == null) throw new IllegalArgumentException("Meal period is required.");
        if (!Double.isFinite(gramsPrepared) || gramsPrepared <= 0) throw new IllegalArgumentException("Prepared grams must be greater than zero.");
        if (!Double.isFinite(gramsServed) || gramsServed < 0 || gramsServed > gramsPrepared) {
            throw new IllegalArgumentException("Served grams must be from zero through the prepared amount.");
        }
        if (!Double.isFinite(gramsPlateLeftover) || gramsPlateLeftover < 0 || gramsPlateLeftover > gramsServed) {
            throw new IllegalArgumentException("Plate leftovers must be from zero through the served amount.");
        }
        this.id = UUID.randomUUID().toString();
        this.foodId = foodId.trim().toUpperCase();
        this.mealPeriod = mealPeriod;
        this.gramsPrepared = gramsPrepared;
        this.gramsServed = gramsServed;
        this.gramsPlateLeftover = gramsPlateLeftover;
        this.recordedAt = LocalDateTime.now();
    }

    /** Total waste = unserved food + returned plate leftovers. */
    public double getTotalWasteGrams() {
        return (gramsPrepared - gramsServed) + gramsPlateLeftover;
    }

    public String getId() { return id; }
    public String getFoodId() { return foodId; }
    public MealPeriod getMealPeriod() { return mealPeriod; }
    public double getGramsPrepared() { return gramsPrepared; }
    public double getGramsServed() { return gramsServed; }
    public double getGramsPlateLeftover() { return gramsPlateLeftover; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
}
