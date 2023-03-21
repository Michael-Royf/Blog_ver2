package com.michael.blog.config;


import com.michael.blog.security.JwtAuthenticationEntryPoint;
import com.michael.blog.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {


    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.authenticationProvider = authenticationProvider;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http.csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
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
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
