package fr.recia.restriction_rentree_api.repository;

import fr.recia.restriction_rentree_api.entity.Etablissement;
import fr.recia.restriction_rentree_api.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NiveauRepository extends JpaRepository<Niveau, Long> {
    List<Niveau> findByEtablissement(Etablissement etablissement);
    Optional<Niveau> findByEtablissementAndNom(Etablissement etablissement, String nom);
}
