package fr.recia.restriction_rentree_api.dto;

import lombok.Data;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@ToString
public class RestrictionNiveau {
    private String niveau;
    private ZonedDateTime dateRentreeNiveau;
    private List<RestrictionClasse> classes;
}
