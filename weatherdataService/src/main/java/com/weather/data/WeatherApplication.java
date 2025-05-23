package com.weather.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Application class.
 */
@EnableConfigurationProperties
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.weather.data"})
public class WeatherApplication {
    /**
     * The main method
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
