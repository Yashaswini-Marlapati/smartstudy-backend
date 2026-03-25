package com.studyroom.smartstudy.config;

import org.springframework.context.annotation.*;  
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration   // Marks this class as a configuration class (Spring reads it at startup)
public class SecurityConfig {

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        // Enable CORS in Spring Security
        .cors(cors -> {})

        // Disable CSRF for APIs
        .csrf(csrf -> csrf.disable())

        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                

                .requestMatchers("/rooms/admin/**").hasRole("ADMIN")
                .requestMatchers("/rooms/admin/**").hasRole("ADMIN")
                .requestMatchers("/analytics/**").hasRole("STUDENT")

                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/rooms/student/**").hasRole("STUDENT")
                .requestMatchers("/booking/**").hasAnyRole("STUDENT","ADMIN")

                .anyRequest().permitAll()
        )

        .httpBasic(httpBasic -> {});

    return http.build();
}

@Bean
public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {

    org.springframework.web.cors.CorsConfiguration configuration =
            new org.springframework.web.cors.CorsConfiguration();

    configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
    configuration.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
    configuration.setAllowedHeaders(java.util.List.of("*"));
    configuration.setAllowCredentials(true);

    org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
            new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}




    @Bean   // 👉 Spring will create ONE BCryptPasswordEncoder object and store it
    public BCryptPasswordEncoder passwordEncoder() {

        // This object encrypts passwords before storing in database
        return new BCryptPasswordEncoder();
    }
}
