# tweet-timeline
This repository provides APIs to retrieve current and historical weather data using the OpenWeather API.

### Steps to build the project
1. Clone the repository
2. cd weather-data
5. mvn clean install -DskipTests

### Starting database
1. cd weatherdataService/src/main/resources 
2. docker-compose up -d

### Starting application
1. Start database
2. cd weatherdataService 
3. mvn spring-boot:run

### Run functional tests
1. Start application
2. cd weatherdataFunctionalTests
3. mvn clean test

### APIs

#### Current weather
```
GET api/weather/current-weather/<city>
```

#### Historical weather

```
GET api/weather/historical-weather/<city>
```
Supported query parameters

| Query param | Format       | Optional | Default | Allowed values           |
|-------------|--------------|----------|---------|--------------------------|
| startDate   | YYYY-MM-DD   | Yes | today - 1 | 
| endDate     | YYYY-MM-DD   | Yes | today     |
| interval    | String       | Yes | DAILY | DAILY / WEEKLY / MONTHLY |
