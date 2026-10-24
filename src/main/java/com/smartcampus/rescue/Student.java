package com.smartcampus.rescue;

import java.io.Serializable;

/** A demo student profile. It is not a secure campus login account. */
public final class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String displayName;

    public Student(String id, String displayName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be empty.");
        }
        this.id = id.trim().toUpperCase();
        this.displayName = displayName.trim();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() {
        return id + " - " + displayName;
    }
}
