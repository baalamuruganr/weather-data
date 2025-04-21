package com.weather.data.api.controller;

import com.weather.data.api.service.HistoricalWeatherService;
import com.weather.data.exception.BusinessException;
import com.weather.data.validator.HistoricalWeatherValidator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Named;
import java.time.LocalDate;

/**
 * Controller for historical weather api.
 */
@Named
@RequestMapping("/api/weather")
public class HistoricalWeatherController {

    /**
     * Validator to validate the input parameters.
     */
    private final HistoricalWeatherValidator historicalWeatherValidator;

    /**
     * Historical weather service.
     */
    private final HistoricalWeatherService historicalWeatherService;

    /**
     * Constructor.
     *
     * @param historicalWeatherValidator {@link HistoricalWeatherValidator}
     * @param historicalWeatherService   {@link HistoricalWeatherService}
     */
    public HistoricalWeatherController(final HistoricalWeatherValidator historicalWeatherValidator,
                                       final HistoricalWeatherService historicalWeatherService) {
        this.historicalWeatherValidator = historicalWeatherValidator;
        this.historicalWeatherService = historicalWeatherService;
    }

    /**
     * Get historical weather data based on the input params.
     *
     * @param cityName  city name
     * @param startDate start data
     * @param endDate   end data
     * @param interval  interval type
     * @return the response
     */
    @GetMapping("/historical-weather/{cityName}")
    public ResponseEntity<Object> retrieveHistoricalWeather(final @PathVariable("cityName") String cityName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") String interval) {

        // Set default dates
        final LocalDate today = LocalDate.now();
        if (endDate == null) {
            endDate = today;
        }
        if (startDate == null) {
            startDate = endDate.minusDays(1);
        }

        // Validate the input fields
        try {
            historicalWeatherValidator.validate(startDate, endDate, interval);
        } catch (final BusinessException businessException) {
            return new ResponseEntity<>(businessException, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // return the response
        return ResponseEntity.ok(historicalWeatherService.retrieveHistoricalWeather(cityName, startDate, endDate, interval));
    }
}
