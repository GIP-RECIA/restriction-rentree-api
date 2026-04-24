package fr.recia.restriction_rentree_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccessStrategyRequest {
    private TypedList<String> isMemberOf;
    @JsonProperty("ESCOUAICourant")
    private TypedList<String> ESCOUAICourant;

    public String getEtablissement(){
        return ESCOUAICourant.getValues().get(0);
    }
}
