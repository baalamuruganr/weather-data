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

--         CREATE INDEX IF NOT EXISTS IDX_CFAM_1 ON CRPFNRCT.T_CRPFNRCT_FNRCT_ACCT_MAP (FNRCT_CUST_EXT_UUID);
--
--         CREATE INDEX IF NOT EXISTS IDX_CFAM_2 ON CRPFNRCT.T_CRPFNRCT_FNRCT_ACCT_MAP (CR_ACCT_ID);

    END IF;
END
$$
LANGUAGE plpgsql;