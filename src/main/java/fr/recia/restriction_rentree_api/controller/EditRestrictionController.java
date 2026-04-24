package fr.recia.restriction_rentree_api.controller;

import fr.recia.restriction_rentree_api.dto.RestrictionEtab;
import fr.recia.restriction_rentree_api.service.DateRentreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restriction")
public class EditRestrictionController {

    private final DateRentreeService dateRentreeService;

    public EditRestrictionController(DateRentreeService dateRentreeService) {
        this.dateRentreeService = dateRentreeService;
    }

    @PostMapping("/etab/{uai}")
    public ResponseEntity<Void> editRestriction(@PathVariable String uai, @RequestBody RestrictionEtab request) {
        dateRentreeService.setNewRestriction(uai, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/etab/{uai}")
    public ResponseEntity<RestrictionEtab> listRestrictions(@PathVariable String uai){
        return ResponseEntity.ok(dateRentreeService.getRestrictions(uai));
    }



}