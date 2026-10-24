package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/** Food quality survey. Staff reports should aggregate these records by dish. */
public final class FoodSurvey implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final String foodId;
    private final int tasteRating;
    private final int freshnessRating;
    private final int portionRating;
    private final SurveyReason reason;
    private final String comment;
    private final LocalDateTime submittedAt;

    public FoodSurvey(String studentId, String foodId, int tasteRating,
                      int freshnessRating, int portionRating,
                      SurveyReason reason, String comment) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required.");
        if (foodId == null || foodId.isBlank()) throw new IllegalArgumentException("Food ID is required.");
        checkRating("Taste", tasteRating);
        checkRating("Freshness/temperature", freshnessRating);
        checkRating("Portion suitability", portionRating);
        if (reason == null) throw new IllegalArgumentException("Survey reason is required.");
        if (comment != null && comment.length() > 300) throw new IllegalArgumentException("Comment must be 300 characters or fewer.");
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId.trim().toUpperCase();
        this.foodId = foodId.trim().toUpperCase();
        this.tasteRating = tasteRating;
        this.freshnessRating = freshnessRating;
        this.portionRating = portionRating;
        this.reason = reason;
        this.comment = comment == null ? "" : comment.trim();
        this.submittedAt = LocalDateTime.now();
    }

    private static void checkRating(String name, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(name + " rating must be from 1 to 5.");
        }
    }

    public boolean isLowRating() {
        return tasteRating <= 2 || freshnessRating <= 2 || portionRating <= 2;
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getFoodId() { return foodId; }
    public int getTasteRating() { return tasteRating; }
    public int getFreshnessRating() { return freshnessRating; }
    public int getPortionRating() { return portionRating; }
    public SurveyReason getReason() { return reason; }
    public String getComment() { return comment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}
