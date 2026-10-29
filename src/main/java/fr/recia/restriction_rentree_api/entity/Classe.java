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
package fr.recia.restriction_rentree_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"niveau_id", "nom"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dateRentree;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

}
