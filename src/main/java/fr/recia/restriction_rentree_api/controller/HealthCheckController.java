package fr.recia.restriction_rentree_api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    @GetMapping()
    public ResponseEntity<Void> healthCheck() {
        log.trace("Health check - Returning 200 OK");
        return ResponseEntity.ok().build();
    }

}