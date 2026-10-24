package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDateTime;

/** A meal request that can be delivered to a student's hostel room. */
public final class SickMealRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String requestId;
    private final String studentId;
    private final String foodId;
    private final String foodName;
    private final MealQuantity quantity;
    private final String hostelBlock;
    private final String roomNumber;
    private final String preferredTime;
    private final String privateFoodNote;
    private final LocalDateTime requestedAt;
    private MealRequestStatus status;
    private String statusNote;

    public SickMealRequest(String requestId, String studentId, String foodId,
                           String foodName, MealQuantity quantity, String hostelBlock,
                           String roomNumber, String preferredTime, String privateFoodNote) {
        this.requestId = requireText("Request ID", requestId);
        this.studentId = requireText("Student ID", studentId).toUpperCase();
        this.foodId = foodId == null || foodId.isBlank() ? "OTHER" : foodId.trim().toUpperCase();
        this.foodName = requireText("Food choice", foodName);
        if (quantity == null) throw new IllegalArgumentException("Quantity is required.");
        this.quantity = quantity;
        this.hostelBlock = requireText("Hostel/block", hostelBlock);
        this.roomNumber = requireText("Room number", roomNumber);
        this.preferredTime = requireText("Preferred delivery time", preferredTime);
        if (privateFoodNote != null && privateFoodNote.length() > 300) {
            throw new IllegalArgumentException("Food preference/allergy note must be 300 characters or fewer.");
        }
        this.privateFoodNote = privateFoodNote == null ? "" : privateFoodNote.trim();
        this.requestedAt = LocalDateTime.now();
        this.status = MealRequestStatus.PENDING;
        this.statusNote = "";
    }

    private static String requireText(String field, String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(field + " cannot be empty.");
        return value.trim();
    }

    public boolean isActive() {
        return status == MealRequestStatus.PENDING
                || status == MealRequestStatus.ACCEPTED
                || status == MealRequestStatus.PREPARING
                || status == MealRequestStatus.OUT_FOR_DELIVERY;
    }

    public boolean canCancel() {
        // Once meal preparation begins, students must contact the canteen to resolve it.
        return status == MealRequestStatus.PENDING || status == MealRequestStatus.ACCEPTED;
    }

    public void updateStatus(MealRequestStatus newStatus, String note) {
        if (newStatus == null) throw new IllegalArgumentException("New status is required.");
        boolean allowed = switch (status) {
            case PENDING -> newStatus == MealRequestStatus.ACCEPTED
                    || newStatus == MealRequestStatus.UNAVAILABLE
                    || newStatus == MealRequestStatus.CANCELLED;
            case ACCEPTED -> newStatus == MealRequestStatus.PREPARING
                    || newStatus == MealRequestStatus.CANCELLED
                    || newStatus == MealRequestStatus.UNAVAILABLE;
            case PREPARING -> newStatus == MealRequestStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> newStatus == MealRequestStatus.DELIVERED;
            case DELIVERED, UNAVAILABLE, CANCELLED -> false;
        };
        if (!allowed) {
            throw new IllegalStateException("Cannot change a request from " + status.getLabel()
                    + " to " + newStatus.getLabel() + ".");
        }
        this.status = newStatus;
        this.statusNote = note == null ? "" : note.trim();
    }

    public String getRequestId() { return requestId; }
    public String getStudentId() { return studentId; }
    public String getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public MealQuantity getQuantity() { return quantity; }
    public String getHostelBlock() { return hostelBlock; }
    public String getRoomNumber() { return roomNumber; }
    public String getPreferredTime() { return preferredTime; }
    public String getPrivateFoodNote() { return privateFoodNote; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public MealRequestStatus getStatus() { return status; }
    public String getStatusNote() { return statusNote; }
}
