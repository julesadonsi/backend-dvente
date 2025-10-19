
package com.usetech.dvente.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DefaultSecurityConfig {

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/public/**",
                                "/webjars/**",
                                "/uploads/**",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**").permitAll()
                        .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}