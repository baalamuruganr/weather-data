package com.weather.data.validator;

import com.weather.data.enums.IntervalType;
import com.weather.data.exception.BusinessException;

import javax.inject.Named;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Historical weather validator.
 */
@Named
public class HistoricalWeatherValidator {
    /**
     * Validate the required fields.
     */
    public void validate(final LocalDate startDate, final LocalDate endDate, String interval) {
        final LocalDate today = LocalDate.now();

        // Start date cannot be in future
        if (startDate.isAfter(today)) {
            throw new BusinessException("Start date cannot be in future", "startDate", startDate.toString());
        }

        // End date cannot be in future
        if (endDate.isAfter(today)) {
            throw new BusinessException("End date cannot be in future", "endDate", endDate.toString());
        }

        // Start date cannot be after end date
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date", "startDate", startDate.toString());
        }

        // Interval types should be one of daily,
        if (!IntervalType.INTERVAL_TYPES.contains(interval.toUpperCase(Locale.ENGLISH))) {
            throw new BusinessException(
                    String.format("Interval %s not supported. Allowed interval types are %s", interval,IntervalType.INTERVAL_TYPES) ,
                    "interval",
                    interval);
        }
    }
}
