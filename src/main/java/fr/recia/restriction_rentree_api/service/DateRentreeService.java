package fr.recia.restriction_rentree_api.service;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.dto.RestrictionClasse;
import fr.recia.restriction_rentree_api.dto.RestrictionEtab;
import fr.recia.restriction_rentree_api.dto.RestrictionNiveau;
import fr.recia.restriction_rentree_api.dto.SarapisRequestClasseDTO;
import fr.recia.restriction_rentree_api.entity.Classe;
import fr.recia.restriction_rentree_api.entity.Etablissement;
import fr.recia.restriction_rentree_api.entity.Niveau;
import fr.recia.restriction_rentree_api.repository.ClasseRepository;
import fr.recia.restriction_rentree_api.repository.EtablissementRepository;
import fr.recia.restriction_rentree_api.repository.NiveauRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DateRentreeService {

    private final EtablissementRepository etablissementRepository;
    private final NiveauRepository niveauRepository;
    private final ClasseRepository classeRepository;
    private final SarapisQueryService sarapisClasseRetrieveService;
    private final ClasseCalculatorService classeCalculatorService;
    private final RestrictionProperties restrictionProperties;

    public DateRentreeService(EtablissementRepository etablissementRepository, NiveauRepository niveauRepository, ClasseRepository classeRepository,
                              SarapisQueryService sarapisClasseRetrieveService, ClasseCalculatorService classeCalculatorService, RestrictionProperties restrictionProperties){
        this.etablissementRepository = etablissementRepository;
        this.niveauRepository = niveauRepository;
        this.classeRepository = classeRepository;
        this.sarapisClasseRetrieveService = sarapisClasseRetrieveService;
        this.classeCalculatorService = classeCalculatorService;
        this.restrictionProperties = restrictionProperties;
    }

    /**
     * Méthode qui retourne un booléen indiquant si l'utilisateur à le droit de se connecter ou non
     * Dépend de sa classe et de son établissement courant en fonction de la configuration faite par l'établissement
     */
    public boolean isAccessAllowed(AccessStrategyRequest request){
        String classeNom = classeCalculatorService.getClasse(request);
        String uai = request.getEtablissement();
        // TODO : si on ne trouve pas de classe du tout alors il faut juste regarder la date de l'établissement et bloquer si besoin
        if(classeNom != null && uai != null){
            log.debug("Class found for request {}", request);
            Etablissement etab = etablissementRepository.findByUai(uai).orElse(null);
            if(etab == null){
                log.warn("No etab was found in database for request {}", request);
                return false;
            }
            if(etab.isEnabled()){
                // On suppose que dans un même établissement on a pas 2 classes qui portent le même nom exact
                List<Classe> classes = classeRepository.findByNomAndEtablissement(classeNom, etab.getId());
                if (classes.isEmpty()) {
                    log.warn("No class was found in database for request {}", request);
                    return false;
                }
                if (classes.size() > 1) {
                    log.warn("Multiple classes found in database for request {}", request);
                    return false;
                }
                Classe classe = classes.get(0);
                ZonedDateTime now = ZonedDateTime.now();
                log.debug("Now is {}", now);
                log.debug("Restriction for {} applies for {}", classe.getNom(), getDateRentreeEffective(classe));
                return now.isAfter(getDateRentreeEffective(classe));
            }
        } else {
            log.warn("No class was found in groups for request {}", request);
            return false;
        }
        return true;
    }

    /**
     * Retourne toutes les restrictions sur un établissement en fonction de son uai
     * Créé des restrictions vides si elles n'existent pas encore
     */
    public RestrictionEtab getRestrictions(String uai) {

        // Récupération des classes de l'établissement depuis Sarapis
        List<SarapisRequestClasseDTO> classesExternes = sarapisClasseRetrieveService.getClasses(uai);

        // On insère touts les établissements / niveaux / classes qui ne sont pas encore connus
        // Etablissement
        Etablissement etab = etablissementRepository.findByUai(uai)
                .orElseGet(() -> {
                    log.info("Etab {} does not exists in database : creating it", uai);
                    Etablissement e = Etablissement.builder()
                            .uai(uai)
                            .enabled(true)
                            .dateRentree(null)
                            .build();
                    return etablissementRepository.save(e);
                });

        for (SarapisRequestClasseDTO dto : classesExternes) {
            // Niveau
            Niveau niveau = niveauRepository.findByEtablissementAndNom(etab, dto.getNiveau())
                    .orElseGet(() -> {
                        log.info("Level {} for {} not exists in database : creating it", dto.getNiveau(), uai);
                        Niveau n = Niveau.builder()
                                .nom(dto.getNiveau())
                                .etablissement(etab)
                                .dateRentree(null)
                                .build();
                        return niveauRepository.save(n);
                    });
            // Classe
            classeRepository.findByNiveauAndNom(niveau, dto.getClasse())
                    .orElseGet(() -> {
                        log.info("Class {} for {} not exists in database : creating it", dto.getClasse(), uai);
                        Classe c = Classe.builder()
                                .nom(dto.getClasse())
                                .niveau(niveau)
                                .dateRentree(null)
                                .build();
                        return classeRepository.save(c);
                    });
        }

        // Une fois qu'on sûr qu'ils sont bien dans la BD, on peut la lire pour constuire le DTO
        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setDateRentreeEtab(etab.getDateRentree());
        restriction.setDateRentreeDefaut(restrictionProperties.getDefaultDate());
        restriction.setDateDebutBloquage(restrictionProperties.getStartDate());
        restriction.setEnabled(etab.isEnabled());
        // TODO : une seule requête avec une jointure ?
        List<RestrictionNiveau> niveaux = niveauRepository.findByEtablissement(etab).stream()
                .map(n -> {
                    RestrictionNiveau rn = new RestrictionNiveau();
                    rn.setNiveau(n.getNom());
                    rn.setDateRentreeNiveau(n.getDateRentree());
                    List<RestrictionClasse> classes = classeRepository.findByNiveau(n).stream()
                            .map(c -> {
                                RestrictionClasse rc = new RestrictionClasse();
                                rc.setClasse(c.getNom());
                                rc.setDateRentreeClasse(c.getDateRentree());
                                return rc;
                            }).collect(Collectors.toList());
                    rn.setClasses(classes);
                    return rn;
                }).collect(Collectors.toList());
        restriction.setNiveaux(niveaux);
        log.debug("Restriction returned is {}", restriction);
        return restriction;
    }

    /**
     * Met à jour toutes les restrictions d'un établissement dans la base de données
     */
    public void setNewRestriction(String uai, RestrictionEtab restrictionEtab){
        log.debug("Will set new restriction for etab {} : {}", uai, restrictionEtab);
        Etablissement etab = etablissementRepository.findByUai(uai).orElseThrow(() -> new RuntimeException("Unknown etab : " + uai));

        // Mise à jour de la date établissement
        etab.setDateRentree(restrictionEtab.getDateRentreeEtab());
        etab.setEnabled(restrictionEtab.isEnabled());
        etablissementRepository.save(etab);

        // Parcours des niveaux
        if (restrictionEtab.getNiveaux() != null) {
            for (RestrictionNiveau rn : restrictionEtab.getNiveaux()) {
                Niveau niveau = niveauRepository.findByEtablissementAndNom(etab, rn.getNiveau()).orElseThrow(() -> new RuntimeException("Unknown level : " + rn.getNiveau()));
                // Mise à jour des dates de rentrée des niveaux
                niveau.setDateRentree(rn.getDateRentreeNiveau());
                niveauRepository.save(niveau);
                // Parcours des classes
                if (rn.getClasses() != null) {
                    for (RestrictionClasse rc : rn.getClasses()) {
                        Classe classe = classeRepository.findByNiveauAndNom(niveau, rc.getClasse()).orElseThrow(() -> new RuntimeException("Unknown class : " + rc.getClasse()));
                        // Mise à jour des dates de rentrée des classes
                        classe.setDateRentree(rc.getDateRentreeClasse());
                        classeRepository.save(classe);
                    }
                }
            }
        }
        log.debug("New restriction for etab {} was set : {}", uai, restrictionEtab);
    }

    private ZonedDateTime getDateRentreeEffective(Classe classe) {
        if (classe.getDateRentree() != null) {
            return classe.getDateRentree();
        }
        if (classe.getNiveau().getDateRentree() != null) {
            return classe.getNiveau().getDateRentree();
        }
        if (classe.getNiveau().getEtablissement().getDateRentree() != null){
            return classe.getNiveau().getEtablissement().getDateRentree();
        }
        return restrictionProperties.getDefaultDate();
    }

}
