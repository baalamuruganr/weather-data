package com.weather.data.tests.functionaltests;

import com.weather.data.tests.WeatherDataTestConfiguration;
import com.weather.data.tests.repository.WeatherDataRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;

import javax.inject.Inject;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;

/**
 * Tests current-weather api
 */
@Getter
@Slf4j
@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {WeatherDataTestConfiguration.class})
public class BaseFunctionTest extends AbstractTestNGSpringContextTests {

    /**
     * Repository to retrieve weather data.
     */
    @Inject
    private WeatherDataRepository weatherDataRepository;

    /**
     * Set the default values.
     */
    @BeforeClass(alwaysRun = true)
    public void setup() {
        baseURI = "http://localhost";
        port = 8080;
        basePath = "/api/weather/";
    }
}
