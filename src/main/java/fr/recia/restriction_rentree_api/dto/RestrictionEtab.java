package fr.recia.restriction_rentree_api.dto;

import lombok.Data;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@ToString
public class RestrictionEtab {
    private boolean enabled;
    private ZonedDateTime dateDebutBloquage;
    private ZonedDateTime dateRentreeDefaut;
    private ZonedDateTime dateRentreeEtab;
    private List<RestrictionNiveau> niveaux;
}
