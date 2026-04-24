package fr.recia.restriction_rentree_api.configuration;

import fr.recia.restriction_rentree_api.security.ApiKeyAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private final ApiKeyAuthorizationFilter apiKeyAuthorizationFilter;

    public SecurityConfiguration(ApiKeyAuthorizationFilter apiKeyAuthorizationFilter){
        this.apiKeyAuthorizationFilter = apiKeyAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/health-check").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(apiKeyAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
