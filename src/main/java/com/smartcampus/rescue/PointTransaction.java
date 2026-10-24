package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** A single points award. One award per activity per student per week is allowed. */
public final class PointTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final PointActivity activity;
    private final int points;
    private final LocalDate weekStart;
    private final LocalDateTime awardedAt;

    public PointTransaction(String studentId, PointActivity activity, LocalDate weekStart) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required.");
        if (activity == null) throw new IllegalArgumentException("Activity is required.");
        if (weekStart == null) throw new IllegalArgumentException("Week start date is required.");
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId.trim().toUpperCase();
        this.activity = activity;
        this.points = activity.getPoints();
        this.weekStart = weekStart;
        this.awardedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public PointActivity getActivity() { return activity; }
    public int getPoints() { return points; }
    public LocalDate getWeekStart() { return weekStart; }
    public LocalDateTime getAwardedAt() { return awardedAt; }
}
