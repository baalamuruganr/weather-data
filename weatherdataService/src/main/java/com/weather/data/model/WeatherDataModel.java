package com.weather.data.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Model for minimum and maximum temperature.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class WeatherDataModel {
    /**
     * Minimum temperature
     */
    private String minimumTemperature;

    /**
     * Maximum temperature
     */
    private String maximumTemperature;
}
