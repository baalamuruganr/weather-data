package com.weather.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.inject.Inject;

/**
 * Configuration for basic auth.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * The Environment bean to read properties from.
     */
    @Inject
    private Environment environment;

    /**
     * Basic auth username.
     */
    @Value("${weather.data.username}")
    private String username;

    /**
     * Basic auth password.
     */
    @Value("${weather.data.password}")
    private String password;

    /**
     * Basic auth role.
     */
    @Value("${weather.data.role}")
    private String role;

    /**
     * Build security filter chain.
     *
     * @param httpSecurity {@link HttpSecurity}
     * @return the security filter chain
     * @throws Exception in case of any errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/weather/*").authenticated()
                .and().httpBasic();
        return httpSecurity.build();
    }

    /**
     * Bean to define the default username and password.
     *
     * @return the {@link UserDetailsService}
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                        .username(username)
                        .password(password)
                        .roles(role)
                        .build()
        );
    }
}
