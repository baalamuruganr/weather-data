package com.weather.data.api.controller;

import com.weather.data.api.service.CurrentWeatherService;
import com.weather.data.api.service.HistoricalWeatherService;
import com.weather.data.model.WeatherDataModel;
import com.weather.data.validator.HistoricalWeatherValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class HistoricalWeatherControllerTest {

    @Test
    public void testRetrieveHistoricalWeather() {
        final TestContext testContext = new TestContext();

        final ResponseEntity<Object> responseEntity = testContext.subjectToTest.retrieveHistoricalWeather("test", null, null, null);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    /**
     * The test context.
     */
    private static class TestContext {

        /**
         * Helper to fetch the partner product details from properties file.
         */
        @Mock
        private HistoricalWeatherValidator historicalWeatherValidator;

        @Mock
        private HistoricalWeatherService historicalWeatherService;

        /**
         * The subject under test.
         */
        @InjectMocks
        private HistoricalWeatherController subjectToTest;

        /**
         * The test context constructor.
         */
        public TestContext() {
            MockitoAnnotations.openMocks(this);
        }
    }
}
