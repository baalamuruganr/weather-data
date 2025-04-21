package com.weather.data.api.controller;

import com.weather.data.api.service.CurrentWeatherService;
import com.weather.data.model.WeatherDataModel;
import net.aksingh.owmjapis.api.APIException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CurrentWeatherControllerTest {

    @Test
    void testRetrieveCurrentWeather() throws APIException {
        final TestContext testContext = new TestContext();

        when(testContext.currentWeatherService.retrieveCurrentWeather("test"))
                .thenReturn(WeatherDataModel.builder().build());

        final ResponseEntity<WeatherDataModel> responseEntity = testContext.subjectToTest.retrieveCurrentWeather("test");

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        verify(testContext.currentWeatherService).retrieveCurrentWeather("test");
    }

    /**
     * DataProvider for get request id test.
     *
     * @return the data provider
     */
    @DataProvider
    public static Object[][] exceptionDataProvider() {
        return new Object[][]{
                {new APIException(HttpStatus.NOT_FOUND.value(), "Not Found"),
                        HttpStatus.NOT_FOUND.value(),
                        "API call gave error: 404 - Not Found"},
                {new RuntimeException("exception"),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "exception"},
        };
    }

    @Test(dataProvider = "exceptionDataProvider")
    void testRetrieveCurrentWeatherFor(final Exception exception,
                                       final int statusCode,
                                       final String errorMessage) throws APIException {
        final TestContext testContext = new TestContext();

        when(testContext.currentWeatherService.retrieveCurrentWeather("test")).thenThrow(exception);

        final ResponseStatusException responseStatusException = Assert.expectThrows(ResponseStatusException.class, () ->
                        testContext.subjectToTest.retrieveCurrentWeather("test"));

        assertEquals(responseStatusException.getStatus().value(), statusCode);
        assertEquals(responseStatusException.getReason(), errorMessage);
        verify(testContext.currentWeatherService).retrieveCurrentWeather("test");
    }

    /**
     * The test context.
     */
    private static class TestContext {

        /**
         * Helper to fetch the partner product details from properties file.
         */
        @Mock
        private CurrentWeatherService currentWeatherService;

        /**
         * The subject under test.
         */
        @InjectMocks
        private CurrentWeatherController subjectToTest;

        /**
         * The test context constructor.
         */
        public TestContext() {
            MockitoAnnotations.openMocks(this);
        }
    }
}