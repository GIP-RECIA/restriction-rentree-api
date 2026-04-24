package fr.recia.restriction_rentree_api.repository;

import fr.recia.restriction_rentree_api.entity.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtablissementRepository extends JpaRepository<Etablissement, Long> {
    Optional<Etablissement> findByUai(String uai);
}
