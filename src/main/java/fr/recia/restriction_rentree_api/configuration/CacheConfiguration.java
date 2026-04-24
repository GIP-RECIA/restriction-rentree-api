package fr.recia.restriction_rentree_api.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<String, Boolean> accessDecisionCache() {
        // TODO : externaliser ces properties
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(15))
                .maximumSize(10000)
                .build();
    }
}