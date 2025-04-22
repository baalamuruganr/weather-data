package com.weather.data.api.controller;

import com.weather.data.api.service.HistoricalWeatherService;
import com.weather.data.exception.BusinessException;
import com.weather.data.validator.HistoricalWeatherValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;

/**
 * Controller for historical weather api.
 */
@Named
@RequestMapping("/api/weather")
@Slf4j
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

        try {
            // Set default dates
            final LocalDate today = LocalDate.now();
            if (endDate == null) {
                endDate = today;
            }
            if (startDate == null) {
                startDate = endDate.minusDays(1);
            }

            // Validate the input fields
            historicalWeatherValidator.validate(startDate, endDate, interval);

            // return the response
            log.info("Retrieving historical weather for city {}", cityName);
            return ResponseEntity.ok(historicalWeatherService.retrieveHistoricalWeather(cityName, startDate, endDate, interval));

        } catch (final BusinessException businessException) {
            return ResponseEntity.unprocessableEntity().body(businessException);
        } catch (final Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }
}
