package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** Personal meal record. Nutrients are estimates based on known portion weights. */
public final class MealRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final String foodId;
    private final double gramsServed;
    private final double gramsLeftover;
    private final double detectionConfidence;
    private final String source;
    private final LocalDateTime recordedAt;

    public MealRecord(String studentId, String foodId, double gramsServed,
                      double gramsLeftover, double detectionConfidence, String source) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required.");
        if (foodId == null || foodId.isBlank()) throw new IllegalArgumentException("Food ID is required.");
        if (!Double.isFinite(gramsServed) || gramsServed <= 0) throw new IllegalArgumentException("Served grams must be greater than zero.");
        if (!Double.isFinite(gramsLeftover) || gramsLeftover < 0 || gramsLeftover > gramsServed) {
            throw new IllegalArgumentException("Leftover grams must be between zero and the served amount.");
        }
        if (!Double.isFinite(detectionConfidence) || detectionConfidence < 0 || detectionConfidence > 1) {
            throw new IllegalArgumentException("Detection confidence must be between 0 and 1.");
        }
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId.trim().toUpperCase();
        this.foodId = foodId.trim().toUpperCase();
        this.gramsServed = gramsServed;
        this.gramsLeftover = gramsLeftover;
        this.detectionConfidence = detectionConfidence;
        this.source = source == null || source.isBlank() ? "MANUAL" : source.trim();
        this.recordedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getFoodId() { return foodId; }
    public double getGramsServed() { return gramsServed; }
    public double getGramsLeftover() { return gramsLeftover; }
    public double getConsumedGramsEstimate() { return Math.max(0, gramsServed - gramsLeftover); }
    public double getDetectionConfidence() { return detectionConfidence; }
    public String getSource() { return source; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public NutritionFacts estimateConsumedNutrition(FoodItem food) {
        return food.estimateForGrams(getConsumedGramsEstimate());
    }
}
