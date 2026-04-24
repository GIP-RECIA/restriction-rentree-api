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
