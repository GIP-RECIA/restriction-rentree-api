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
