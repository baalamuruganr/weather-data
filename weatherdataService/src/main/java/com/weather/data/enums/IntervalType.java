package com.weather.data.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interval type Enum.
 */
@Getter
public enum IntervalType {

    /**
     * Daily.
     */
    DAILY,

    /**
     * Weekly.
     */
    WEEKLY,

    /**
     * Monthly.
     */
    MONTHLY;

    /**
     * List of all types.
     */
    public static final List<String> INTERVAL_TYPES = EnumSet.allOf(IntervalType.class).stream()
            .map(IntervalType::name)
            .collect(Collectors.toList());
}
