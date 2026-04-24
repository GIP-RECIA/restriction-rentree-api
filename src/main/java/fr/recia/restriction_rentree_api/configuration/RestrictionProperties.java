package fr.recia.restriction_rentree_api.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "restriction")
@Data
@Validated
@Slf4j
public class RestrictionProperties {

    private Long defaultDate;
    private Long startDate;
    private String elevesClasseRegex;
    private String parentsClasseRegex;
    private String regexPlaceholder;
    private Map<String, SecurityProperty> security;

    public ZonedDateTime getDefaultDate() {
        return Instant.ofEpochSecond(defaultDate).atZone(ZoneId.systemDefault());
    }

    public ZonedDateTime getStartDate() {
        return Instant.ofEpochSecond(startDate).atZone(ZoneId.systemDefault());
    }

}
