package com.crimsonlogic.open_petal_backend.dto.user;

import com.crimsonlogic.open_petal_backend.exception.InvalidateSlotException;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public class AvailabilitySlot implements Serializable {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    /**
     * Default constructor for JSON / Jackson deserialization
     */
    public AvailabilitySlot() {
    }

    /**
     * Primary constructor with automatic constraint validation
     */
    public AvailabilitySlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        validateAndSet(dayOfWeek, startTime, endTime);
    }

    /**
     * Internal validator executed during instantiation
     */
    private void validateAndSet(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("Day of week cannot be null.");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new InvalidateSlotException(
                    String.format("Invalid interval: startTime (%s) must be strictly before endTime (%s).", startTime, endTime)
            );
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 60) {
            throw new InvalidateSlotException(
                    String.format("Invalid duration: Slot is %d minutes. Minimum required duration is 60 minutes (1 hour).", minutes)
            );
        }
        if (minutes > 1440) {
            throw new InvalidateSlotException("Invalid duration: Slot exceeds maximum allowed limit of 24 hours (1440 minutes).");
        }

        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // --- Fluent Builder ---

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

        public Builder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public AvailabilitySlot build() {
            return new AvailabilitySlot(this.dayOfWeek, this.startTime, this.endTime);
        }
    }
}