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
package fr.recia.restriction_rentree_api.service;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ClasseCalculatorService {

    private final RestrictionProperties restrictionProperties;

    public ClasseCalculatorService(RestrictionProperties restrictionProperties){
        this.restrictionProperties = restrictionProperties;
    }

    public String getClasse(AccessStrategyRequest accessStrategyRequest){
        final String uai = accessStrategyRequest.getEtablissement();
        final String elevesClasseRegex = restrictionProperties.getElevesClasseRegex().replace(restrictionProperties.getRegexPlaceholder(), Pattern.quote(uai));
        final String parentsClasseRegex = restrictionProperties.getParentsClasseRegex().replace(restrictionProperties.getRegexPlaceholder(), Pattern.quote(uai));
        log.debug("Regex for eleves for request {} is {}", accessStrategyRequest, elevesClasseRegex);
        log.debug("Regex for eleves for request {} is {}", accessStrategyRequest, parentsClasseRegex);
        final Pattern elevesPattern = Pattern.compile(elevesClasseRegex);
        final Pattern parentsPattern = Pattern.compile(parentsClasseRegex);
        for(String group : accessStrategyRequest.getIsMemberOf().getValues()){
            final Matcher elevesMatcher = elevesPattern.matcher(group);
            if (elevesMatcher.matches()) {
                log.debug("match eleves for {}", group);
                return elevesMatcher.group(1);
            }
            final Matcher parentsMatcher = parentsPattern.matcher(group);
            if (parentsMatcher.matches()) {
                log.debug("match parents for {}", group);
                return parentsMatcher.group(1);
            }
        }
        return null;
    }
}
