package com.weather.data.api.service;

import com.google.common.util.concurrent.RateLimiter;
import com.weather.data.model.WeatherDataModel;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static net.aksingh.owmjapis.core.OWM.Unit.IMPERIAL;

/**
 * Service for current weather api.
 */
@Service
public class CurrentWeatherService {

    /**
     * Open weather api key.
     */
    @Value("${open.weather.api.key}")
    private String apiKey;

    /**
     * Initialize the rate limiter.
     * 60 calls per minute = 1 call per second
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(1.0);

    /**
     * Retrieve current weather condition of the given city.
     *
     * @param cityName city name
     * @return the current weather
     * @throws APIException when no data found
     */
    public WeatherDataModel retrieveCurrentWeather(final String cityName) throws APIException {

        final OWM owm = new OWM(apiKey);

        // https://openweathermap.org/api/one-call-3#data
        // IMPERIAL - Fahrenheit
        // METRIC   - Celsius
        // STANDARD - Kelvin
        owm.setUnit(IMPERIAL);

        // Get current weather
        rateLimiter.acquire();
        final CurrentWeather currentWeather = owm.currentWeatherByCityName(cityName);

        // return the model
        return WeatherDataModel.builder()
                .minimumTemperature(currentWeather.getMainData().getTempMin().toString())
                .maximumTemperature(currentWeather.getMainData().getTempMax().toString())
                .build();
    }
}
