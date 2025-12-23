package com.fpl.edu.shoeStore.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fpl.edu.shoeStore.auth.handler.AdminAuthenticationSuccessHandler;
import com.fpl.edu.shoeStore.auth.handler.CustomAccessDeniedHandler;
import com.fpl.edu.shoeStore.auth.handler.CustomAuthEntryPoint;
import com.fpl.edu.shoeStore.auth.security.JwtAuthFilter;

import lombok.RequiredArgsConstructor;


/**
 * Security Configuration - Spring Security Setup
 * Cấm đụng vô, nói rồi đó
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final AdminAuthenticationSuccessHandler adminAuthSuccessHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new BCryptPasswordEncoder();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))  // Use CORS from WebConfig
            .csrf(csrf -> csrf
                // Disable CSRF for API endpoints (using JWT)
                .ignoringRequestMatchers("/api/**")
                // Enable CSRF for form-based login
            )
            // Allow sessions for admin form-based login, stateless for API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(authorize -> authorize
                // Public resources
                .requestMatchers(
                        "/css/**", "/js/**", "/images/**",
                        "/login/**", "/register/**", "/home/**", 
                        "/", "/403", "/error/**",
                        "/api/v1/auth/**",
                        "/admin/login/**", "/admin/css/**", "/admin/js/**", "/admin/images/**", "/admin/plugins/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                // Admin endpoints require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            // Form-based login for admin
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")  // POST /admin/login
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(adminAuthSuccessHandler)  // Custom success handler
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .deleteCookies("accessToken", "refreshToken", "JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            // JWT filter for API requests (checked after form login)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
