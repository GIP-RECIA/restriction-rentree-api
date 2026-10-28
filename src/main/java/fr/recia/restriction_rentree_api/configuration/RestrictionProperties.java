/*
 * Copyright (C) 2026 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2026 GIP-RECIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.restriction_rentree_api.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

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
    private Set<String> uaiReunion;

    public LocalDateTime getDefaultDate() {
        return Instant.ofEpochSecond(defaultDate).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    public LocalDateTime getStartDate() {
        return Instant.ofEpochSecond(startDate).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

}
