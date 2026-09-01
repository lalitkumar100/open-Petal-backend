package com.crimsonlogic.open_petal_backend.util;

import com.crimsonlogic.open_petal_backend.dto.user.AvailabilitySlot;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class AvailabilitySlotManager {

    /**
     * Adds or overrides an availability slot on a given day of the week,
     * slices overlapping intervals, and merges touching/continuous slots.
     */
    public static List<AvailabilitySlot> addOrMergeSlot(
            List<AvailabilitySlot> existingSlots,
            AvailabilitySlot newSlot) {

        if (newSlot == null) {
            throw new IllegalArgumentException("New slot cannot be null.");
        }

        if (existingSlots == null) {
            existingSlots = new ArrayList<>();
        }

        DayOfWeek targetDay = newSlot.getDayOfWeek();

        // 1. Separate slots on other days from target day
        List<AvailabilitySlot> otherDaySlots = existingSlots.stream()
                .filter(s -> s.getDayOfWeek() != targetDay)
                .collect(Collectors.toList());

        List<AvailabilitySlot> sameDaySlots = existingSlots.stream()
                .filter(s -> s.getDayOfWeek() == targetDay)
                .collect(Collectors.toList());

        // 2. Remove / Slice overlapping ranges
        List<AvailabilitySlot> remainingFragments = new ArrayList<>();
        for (AvailabilitySlot existing : sameDaySlots) {
            if (isOverlapping(existing, newSlot)) {
                // Keep slice before newSlot start (if >= 60 min)
                if (existing.getStartTime().isBefore(newSlot.getStartTime())) {
                    long minsBefore = Duration.between(existing.getStartTime(), newSlot.getStartTime()).toMinutes();
                    if (minsBefore >= 60) {
                        remainingFragments.add(new AvailabilitySlot(targetDay, existing.getStartTime(), newSlot.getStartTime()));
                    }
                }
                // Keep slice after newSlot end (if >= 60 min)
                if (existing.getEndTime().isAfter(newSlot.getEndTime())) {
                    long minsAfter = Duration.between(newSlot.getEndTime(), existing.getEndTime()).toMinutes();
                    if (minsAfter >= 60) {
                        remainingFragments.add(new AvailabilitySlot(targetDay, newSlot.getEndTime(), existing.getEndTime()));
                    }
                }
            } else {
                remainingFragments.add(existing);
            }
        }

        // 3. Add the validated new slot
        remainingFragments.add(newSlot);

        // 4. Merge adjacent/touching slots on the same day
        List<AvailabilitySlot> mergedDaySlots = mergeTouchingSlots(remainingFragments, targetDay);

        // 5. Combine with other days and sort by day of week & time
        List<AvailabilitySlot> finalResult = new ArrayList<>(otherDaySlots);
        finalResult.addAll(mergedDaySlots);
        finalResult.sort(Comparator.comparing(AvailabilitySlot::getDayOfWeek)
                .thenComparing(AvailabilitySlot::getStartTime));

        return finalResult;
    }

    private static boolean isOverlapping(AvailabilitySlot a, AvailabilitySlot b) {
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    private static List<AvailabilitySlot> mergeTouchingSlots(List<AvailabilitySlot> slots, DayOfWeek day) {
        if (slots.isEmpty()) return slots;

        slots.sort(Comparator.comparing(AvailabilitySlot::getStartTime));
        List<AvailabilitySlot> merged = new ArrayList<>();

        AvailabilitySlot current = slots.get(0);

        for (int i = 1; i < slots.size(); i++) {
            AvailabilitySlot next = slots.get(i);

            // If current touches or overlaps next slot
            if (!current.getEndTime().isBefore(next.getStartTime())) {
                LocalTime maxEnd = current.getEndTime().isAfter(next.getEndTime())
                        ? current.getEndTime()
                        : next.getEndTime();

                long totalMins = Duration.between(current.getStartTime(), maxEnd).toMinutes();
                if (totalMins <= 1440) {
                    current.setEndTime(maxEnd);
                } else {
                    merged.add(current);
                    current = next;
                }
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }
}