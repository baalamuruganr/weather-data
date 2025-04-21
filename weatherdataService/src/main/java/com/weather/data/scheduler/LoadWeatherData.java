package com.weather.data.scheduler;

import com.weather.data.entities.WeatherData;
import com.weather.data.repository.WeatherDataRepository;
import lombok.extern.slf4j.Slf4j;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Locale;

import static net.aksingh.owmjapis.core.OWM.Unit.IMPERIAL;

/**
 * Class to load weather data.
 */
@Service
@Slf4j
public class LoadWeatherData {

    /**
     * Open weather api key.
     */
    @Value("${open.weather.api.key}")
    private String apiKey;

    @Inject
    private final WeatherDataRepository weatherDataRepository;

    public LoadWeatherData(final WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void loadWeather() {
        final OWM owm = new OWM(apiKey);
        owm.setUnit(IMPERIAL);
        saveCurrentWeather(owm, "Baltimore");
        saveCurrentWeather(owm,"Washington");
        saveCurrentWeather(owm,"Austin");
    }

    private void saveCurrentWeather(final OWM owm, final String city) {
        try {
            final CurrentWeather currentWeather = owm.currentWeatherByCityName(city);
            final WeatherData data = new WeatherData();
            data.setCity(currentWeather.getCityName().toLowerCase(Locale.ENGLISH));
            data.setMinimumTemperature(currentWeather.getMainData().getTempMin());
            data.setMaximumTemperature(currentWeather.getMainData().getTempMax());
            data.setTimeCreated(Instant.now());

            weatherDataRepository.save(data);
            log.info("Current weather data loaded successfully for city: " + currentWeather.getCityName());
        } catch (APIException e) {
            throw new RuntimeException(e);
        }
    }
}
