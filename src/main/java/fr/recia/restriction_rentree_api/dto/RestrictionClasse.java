package fr.recia.restriction_rentree_api.dto;

import lombok.Data;
import lombok.ToString;

import java.time.ZonedDateTime;

@Data
@ToString
public class RestrictionClasse {
    private String classe;
    private ZonedDateTime dateRentreeClasse;
}
