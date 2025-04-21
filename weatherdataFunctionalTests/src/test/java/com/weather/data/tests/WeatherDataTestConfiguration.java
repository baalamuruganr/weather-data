package com.weather.data.tests;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for Functional tests.
 */
@Configuration
@PropertySource("classpath:db-application.properties")
@EnableJpaRepositories(basePackages = "com.weather.data.tests.repository")
public class WeatherDataTestConfiguration {

    /**
     * Test data source properties.
     *
     * @return the {@link DataSourceProperties}
     */
    @Bean
    @Primary
    @ConfigurationProperties("test.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }


    /**
     * Create the data source.
     *
     * @param properties the data source properties
     * @return the {@link HikariDataSource}
     */
    @Bean
    @ConfigurationProperties(prefix = "test.datasource.configuration")
    protected HikariDataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * Create Transaction manager
     * @return TransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory(dataSource(dataSourceProperties())));
        return transactionManager;
    }

    /**
     * Entity Manager definition
     *
     * @param dataSource data source
     * @return - {@link EntityManagerFactory}
     */
    @Bean
    public EntityManagerFactory entityManagerFactory(final DataSource dataSource) {
        final Properties props = new Properties();

        props.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty(Environment.SHOW_SQL, "true");
        props.setProperty(Environment.MAX_FETCH_DEPTH, "3");
        props.setProperty(Environment.GENERATE_STATISTICS, "true");

        // Determine the number of queries that are cached in each connection
        props.setProperty("preparedStatementCacheQueries", "200");
        // Determine the maximum size (in mebibytes) of the prepared queries cache
        props.setProperty("preparedStatementCacheSizeMiB", "10");

        // Per Hibernate docs: For an application server JTA datasource, use after_statement to aggressively release
        // connections after every JDBC call.
        props.setProperty(Environment.CONNECTION_HANDLING, "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION");

        // prevent hibernate from downloading all metadata
        props.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        props.setProperty(Environment.HBM2DDL_AUTO, "none");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        //setting this value to true nullifies hibernate.hbm2ddl property
        vendorAdapter.setGenerateDdl(false);

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.weather.data.tests");
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setJpaProperties(props);
        factory.afterPropertiesSet();

        return factory.getObject();
    }
}
