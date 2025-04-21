package com.weather.data.api.controller;

import com.weather.data.api.service.CurrentWeatherService;
import com.weather.data.model.WeatherDataModel;
import net.aksingh.owmjapis.api.APIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Named;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Controller for current weather api.
 */
@Named
@RequestMapping("/api/weather")
public class CurrentWeatherController {

    /**
     * Current weather service.
     */
    private final CurrentWeatherService currentWeatherService;

    /**
     * Constructor.
     *
     * @param currentWeatherService weather service
     */
    public CurrentWeatherController(final CurrentWeatherService currentWeatherService) {
        this.currentWeatherService = currentWeatherService;
    }

    /**
     * Get current weather for the given city.
     *
     * @param cityName city name
     * @return the response
     */
    @GetMapping("/current-weather/{cityName}")
    public ResponseEntity<WeatherDataModel> retrieveCurrentWeather(final @PathVariable("cityName") String cityName) {
        try {
            return ResponseEntity.ok(currentWeatherService.retrieveCurrentWeather(cityName));
        } catch (APIException apiException) {
            throw new ResponseStatusException(HttpStatus.valueOf(apiException.getCode()), apiException.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
