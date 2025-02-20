package com.robert.rwbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // httpSecurity.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        httpSecurity.cors().and().csrf().disable().authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/user/**").permitAll()
                .anyRequest().authenticated());

        //  httpSecurity.csrf(csrf -> csrf.disable())
        //         .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.POST, "/api/user/").permitAll()
        //                 .anyRequest().authenticated());
        // httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }
}
  