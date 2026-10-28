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

import com.github.benmanes.caffeine.cache.Cache;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.service.DateRentreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/restriction/cas")
public class CASRequestController {

    private final DateRentreeService dateRentreeService;
    private final Cache<String, Boolean> accessDecisionCache;

    public CASRequestController(DateRentreeService dateRentreeService, Cache<String, Boolean> accessDecisionCache) {
        this.dateRentreeService = dateRentreeService;
        this.accessDecisionCache = accessDecisionCache;
    }

    @PostMapping()
    public ResponseEntity<Void> isAccessAllowed(@RequestParam String username, @RequestBody AccessStrategyRequest request) {
        log.debug("isAccessAllowed for {} with body {}", username, request);
        boolean isAuthorized = Boolean.TRUE.equals(accessDecisionCache.get(username, key -> dateRentreeService.isAccessAllowed(request)));
        if(isAuthorized){
            log.debug("access is allowed for {}", username);
            return ResponseEntity.ok().build();
        } else {
            log.debug("access is not allowed for {}", username);
            return ResponseEntity.status(403).build();
        }
    }

}