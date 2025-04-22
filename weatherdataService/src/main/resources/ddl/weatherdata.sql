DO
$$
BEGIN
    IF NOT EXISTS (select * from information_schema.tables
    where table_name = 'weather_data') THEN
        CREATE TABLE weather_data (
            id SERIAL PRIMARY KEY,
            city VARCHAR(100),
            minimum_temperature DOUBLE PRECISION,
            maximum_temperature DOUBLE PRECISION,
            time_created TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
        );

        CREATE INDEX IF NOT EXISTS IDX_CITY_1 ON weather_data (city);

    END IF;
END
$$
LANGUAGE plpgsql;