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
package fr.recia.restriction_rentree_api.repository;

import fr.recia.restriction_rentree_api.entity.Classe;
import fr.recia.restriction_rentree_api.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClasseRepository extends JpaRepository<Classe, Long> {

    List<Classe> findByNiveau(Niveau niveau);

    Optional<Classe> findByNiveauAndNom(Niveau niveau, String nom);

    @Query(" SELECT c FROM Classe c JOIN c.niveau n JOIN n.etablissement e WHERE c.nom = :classeNom AND e.id = :etabId")
    List<Classe> findByNomAndEtablissement(@Param("classeNom") String classeNom, @Param("etabId") Long etabId);
}
