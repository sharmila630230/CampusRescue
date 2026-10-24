package com.smartcampus.rescue;

/** Status values used by the meal-request workflow. */
public enum MealRequestStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    PREPARING("Preparing"),
    OUT_FOR_DELIVERY("Out for delivery"),
    DELIVERED("Delivered"),
    UNAVAILABLE("Unavailable"),
    CANCELLED("Cancelled");

    private final String label;
    MealRequestStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
