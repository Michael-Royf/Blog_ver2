package com.michael.blog.config;


import com.michael.blog.security.JwtAuthenticationEntryPoint;
import com.michael.blog.security.JwtAuthenticationFilter1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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


    private JwtAuthenticationFilter1 jwtAuthenticationFilter;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter1 jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(authorize ->
                        authorize
                                //    .requestMatchers(HttpMethod.POST, PUBLIC_URLS).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signin").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/forgot-password").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/registration/confirm/**").permitAll()

                                .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/v1/category/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/v1/category/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/category/**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //     .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
