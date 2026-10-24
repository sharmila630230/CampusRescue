package com.smartcampus.rescue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A private student check-in. Staff can see it only when the student explicitly
 * agrees to share it; "Prefer not to share" always stays private in this prototype.
 */
public final class StudentCheckIn implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String studentId;
    private final StudentFeeling feeling;
    private final String optionalComment;
    private final boolean consentToShare;
    private final LocalDateTime createdAt;
    private boolean followUpDone;

    public StudentCheckIn(String studentId, StudentFeeling feeling,
                          String optionalComment, boolean consentToShare) {
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required.");
        if (feeling == null) throw new IllegalArgumentException("Please select how you are feeling.");
        if (optionalComment != null && optionalComment.length() > 300) {
            throw new IllegalArgumentException("The optional comment must be 300 characters or fewer.");
        }
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.studentId = studentId.trim().toUpperCase();
        this.feeling = feeling;
        this.optionalComment = optionalComment == null ? "" : optionalComment.trim();
        // "Prefer not to say" must never be shared with staff in this prototype.
        this.consentToShare = consentToShare && feeling != StudentFeeling.PREFER_NOT_TO_SAY;
        this.createdAt = LocalDateTime.now();
        this.followUpDone = false;
    }

    public boolean isVisibleToSupportStaff() {
        return consentToShare && feeling != StudentFeeling.PREFER_NOT_TO_SAY;
    }

    public void markFollowUpDone() { this.followUpDone = true; }

    public String getId() { return id; }
    public String getStudentId() { return studentId; }
    public StudentFeeling getFeeling() { return feeling; }
    public String getOptionalComment() { return optionalComment; }
    public boolean isConsentToShare() { return consentToShare; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isFollowUpDone() { return followUpDone; }
}
