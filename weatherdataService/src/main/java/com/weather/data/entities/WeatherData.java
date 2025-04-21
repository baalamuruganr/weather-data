package com.weather.data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Entity to hold weather data.
 */
@Getter
@Setter
@Entity
@Table(name = "weather_data")
public class WeatherData {

    /**
     * Sequence id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * City name.
     */
    @Column(name = "city", nullable = false)
    private String city;

    /**
     * Minimum temperature.
     */
    @Column(name = "minimum_temperature")
    private double minimumTemperature;

    /**
     * Maximum temperature.
     */
    @Column(name = "maximum_temperature")
    private double maximumTemperature;

    /**
     * Entry inserted time.
     */
    @Column(name = "time_created", columnDefinition = "TIMESTAMPTZ")
    private Instant timeCreated;
}
