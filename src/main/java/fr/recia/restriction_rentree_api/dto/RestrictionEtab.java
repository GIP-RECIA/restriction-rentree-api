package fr.recia.restriction_rentree_api.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@ToString
public class RestrictionEtab {
    private boolean enabled;
    private LocalDateTime dateDebutBloquage;
    private LocalDateTime dateRentreeDefaut;
    private LocalDateTime dateRentreeEtab;
    private List<RestrictionNiveau> niveaux;
}
