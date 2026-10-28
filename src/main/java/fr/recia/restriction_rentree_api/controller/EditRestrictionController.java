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