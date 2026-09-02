package com.crimsonlogic.open_petal_backend.enums;

public enum ConflictStatus {
    OPEN,            // Initial state when raised
    UNDER_REVIEW,    // Admin is actively looking at it
    RESOLVED         // Admin has made a decision (refunded or transferred credits)
}