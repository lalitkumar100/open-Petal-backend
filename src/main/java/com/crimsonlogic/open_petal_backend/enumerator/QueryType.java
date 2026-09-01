package com.crimsonlogic.open_petal_backend.enumerator;

public enum QueryType {
    ADD_NEW_SKILL,        // Request admin to add a new skill to the master catalog
    SESSION_CONFLICT,     // Dispute/conflict during a session (no-show, double booking, credit mismatch)
    GENERAL_ADVICE        // General suggestions, platform feedback, or general help
}