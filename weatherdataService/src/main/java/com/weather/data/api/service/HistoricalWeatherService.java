package com.weather.data.api.service;

import com.weather.data.enums.IntervalType;
import com.weather.data.model.WeatherDataModel;
import com.weather.data.entities.WeatherData;
import com.weather.data.repository.WeatherDataRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Service for historical weather api.
 */
@Service
public class HistoricalWeatherService {

    /**
     * Repository to fetch the weather data.
     */
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Constructor.
     *
     * @param weatherDataRepository  {@link WeatherDataRepository}
     */
    public HistoricalWeatherService(final WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Retrieve current weather condition of the given city.
     *
     * @param cityName  city name
     * @param startDate start date
     * @param endDate   end adte
     * @param interval interval
     * @return the historical weather
     */
    public Map<String, WeatherDataModel> retrieveHistoricalWeather(final String cityName, final LocalDate startDate,
                                                                   final LocalDate endDate, final String interval) {

        // Retrieve weather data from database
        final Instant from = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        final Instant to = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        final List<WeatherData> retrievedWeatherData = weatherDataRepository.findByCityAndTimeCreatedBetween(
                cityName.toLowerCase(Locale.ENGLISH), from, to);

        // Group data by interval type
        final Map<String, List<WeatherData>> groupedWeatherData = new HashMap<>();
        final IntervalType intervalType = IntervalType.valueOf(interval.toUpperCase(Locale.ENGLISH));
        retrievedWeatherData.forEach(weatherData -> {
            final String key = getGroupKey(intervalType, weatherData);
            groupedWeatherData.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(weatherData);
        });

        // Aggregate min and max
        final Map<String, WeatherDataModel> result = new HashMap<>();
        groupedWeatherData.forEach((key, value) -> {
            final double minimumTemperature = value.stream().mapToDouble(WeatherData::getMinimumTemperature).min().orElse(0);
            final double maximumTemperature = value.stream().mapToDouble(WeatherData::getMaximumTemperature).max().orElse(0);
            final WeatherDataModel weatherDataModel = WeatherDataModel.builder()
                    .minimumTemperature(Double.toString(minimumTemperature))
                    .maximumTemperature(Double.toString(maximumTemperature))
                    .build();
            result.put(key, weatherDataModel);
        });

        return result;
    }

    /**
     * Get the key based on the interval type.
     *
     * @param intervalType interval type (DAILY/WEEKLY/MONTHLY)
     * @param weatherData  weather data from database
     * @return the key
     */
    private String getGroupKey(final IntervalType intervalType, final WeatherData weatherData) {
        final ZonedDateTime zonedDateTime = weatherData.getTimeCreated().atZone(ZoneId.systemDefault());
        return switch (intervalType) {
            case WEEKLY -> zonedDateTime.getYear() + "-Week" + zonedDateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); // 2025-Week16
            case MONTHLY -> zonedDateTime.getYear() + "-" + zonedDateTime.getMonthValue(); // 2025-4
            default -> zonedDateTime.toLocalDate().toString(); // 2025-04-19
        };
    }
}
