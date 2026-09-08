package com.crimsonlogic.open_petal_backend.util;

/**
 * Utility class for slug.
 */
public class SlugUtil {
    public static String toSlug(String input) {
        if (input == null) return null;
        return input.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
