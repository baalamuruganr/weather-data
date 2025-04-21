package com.weather.data.repository;

import com.weather.data.entities.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for {@link WeatherData}.
 */
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    /**
     * Retrieve the data based on city name, start and end date.
     *
     * @param city  city name
     * @param start start date
     * @param end   end date
     * @return the list of weather data
     */
    List<WeatherData> findByCityAndTimeCreatedBetween(String city, Instant start, Instant end);
}
