package com.bookstore.bookservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(authz -> authz
                        // Public endpoints - anyone can access
                        .antMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/search/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/category/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/author/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/available/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/categories").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/books/authors").permitAll()

                        // Admin only endpoints
                        .antMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PATCH, "/api/books/**/stock").hasRole("ADMIN")

                        // Health and management endpoints
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/h2-console/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .headers().frameOptions().disable()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
