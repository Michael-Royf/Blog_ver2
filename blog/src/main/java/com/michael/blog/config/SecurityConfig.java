package com.michael.blog.config;


import com.michael.blog.security.JwtAuthenticationEntryPoint;
import com.michael.blog.security.JwtAuthenticationFilter;
import com.michael.blog.service.impl.LogoutService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.michael.blog.constants.SecurityConstant.PUBLIC_URLS_SWAGGER;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@SecurityScheme(
        name = "Bear Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutService logoutService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          AuthenticationProvider authenticationProvider,
                          LogoutService logoutService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.authenticationProvider = authenticationProvider;
        this.logoutService = logoutService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PUBLIC_URLS_SWAGGER).permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/refesh_token").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signin").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/forgot-password").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/registration/confirm/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/category/**").hasAnyRole("ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/category/**").hasAnyRole("ADMIN", "SUPERADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/category/**").hasAnyRole("ADMIN", "SUPERADMIN")

                                .requestMatchers(HttpMethod.POST, "/api/v1/superadmin/**").hasRole("SUPERADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/logout")
                .addLogoutHandler(logoutService)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
        return http.build();
    }


}
