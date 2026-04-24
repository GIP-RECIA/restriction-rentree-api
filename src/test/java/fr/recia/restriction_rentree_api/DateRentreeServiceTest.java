package fr.recia.restriction_rentree_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.dto.RestrictionEtab;
import fr.recia.restriction_rentree_api.dto.SarapisRequestClasseDTO;
import fr.recia.restriction_rentree_api.entity.Classe;
import fr.recia.restriction_rentree_api.entity.Niveau;
import fr.recia.restriction_rentree_api.entity.Etablissement;
import fr.recia.restriction_rentree_api.repository.ClasseRepository;
import fr.recia.restriction_rentree_api.repository.EtablissementRepository;
import fr.recia.restriction_rentree_api.repository.NiveauRepository;
import fr.recia.restriction_rentree_api.service.ClasseCalculatorService;
import fr.recia.restriction_rentree_api.service.DateRentreeService;
import fr.recia.restriction_rentree_api.service.SarapisQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateRentreeServiceTest {

    @Mock
    private EtablissementRepository etablissementRepository;
    @Mock
    private NiveauRepository niveauRepository;
    @Mock
    private ClasseRepository classeRepository;
    @Mock
    private SarapisQueryService sarapisClasseRetrieveService;
    @Mock
    private ClasseCalculatorService classeCalculatorService;
    @Mock
    private RestrictionProperties restrictionProperties;

    @InjectMocks
    private DateRentreeService dateRentreeService;

    // Constantes
    private static final String UAI = "0450000A";
    private static final String _3EME_A = "3EME A";
    private static final String _3EME = "3EME";

    // =========================================================
    // test pour méthode isAccessAllowed
    // =========================================================

    @Test
    void shouldReturnFalseWhenClasseIsNull() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(null);
        when(request.getEtablissement()).thenReturn(UAI);
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenUaiIsNull() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(null);
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenEtablissementNotFound() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty());
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenEtablissementIsDisabled() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(false).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoClasseFoundInDatabase() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of());
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenMultipleClassesFound() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Classe c1 = mock(Classe.class);
        Classe c2 = mock(Classe.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(c1, c2));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenNowIsAfterDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(ZonedDateTime.now().minusDays(1)).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNowIsBeforeDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(ZonedDateTime.now().plusDays(1)).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldUseNiveauDateWhenClasseDateIsNull() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(ZonedDateTime.now().minusDays(1)).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUseEtablissementDateWhenClasseAndNiveauDatesAreNull() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).dateRentree(ZonedDateTime.now().minusDays(1)).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(null).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUseDefaultDateWhenNoDateDefined() {
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1));
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).dateRentree(null).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(null).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1));
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    // =========================================================
    // setNewRestriction
    // =========================================================

    @Test
    void shouldThrowWhenSettingRestrictionForUnknownEtab() {
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty());
        RestrictionEtab restriction = new RestrictionEtab();
        assertThrows(RuntimeException.class, () -> dateRentreeService.setNewRestriction(UAI, restriction));
    }

    // TODO : vérifier aussi l'update de la date des niveaux et classes
    @Test
    void shouldUpdateEtablissementWhenSettingRestriction() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setDateRentreeEtab(ZonedDateTime.now().plusDays(2));
        restriction.setEnabled(false);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        dateRentreeService.setNewRestriction(UAI, restriction);
        assertThat(etab.getDateRentree()).isEqualTo(restriction.getDateRentreeEtab());
        assertThat(etab.isEnabled()).isFalse();
        verify(etablissementRepository).save(etab);
    }

    // =========================================================
    // getRestrictions
    // =========================================================

    @Test
    void shouldCreateEtablissementIfNotExistsAndReturnRestrictions() {
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1));
        when(restrictionProperties.getStartDate()).thenReturn(ZonedDateTime.now().minusDays(10));
        SarapisRequestClasseDTO dto = new SarapisRequestClasseDTO();
        dto.setNiveau(_3EME);
        dto.setClasse(_3EME_A);
        Etablissement savedEtab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(savedEtab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).build();
        when(sarapisClasseRetrieveService.getClasses(UAI)).thenReturn(List.of(dto));
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty(), Optional.of(savedEtab));
        when(etablissementRepository.save(any(Etablissement.class))).thenReturn(savedEtab);
        when(niveauRepository.findByEtablissementAndNom(savedEtab, _3EME)).thenReturn(Optional.empty(), Optional.of(niveau));
        when(niveauRepository.save(any(Niveau.class))).thenReturn(niveau);
        when(classeRepository.findByNiveauAndNom(niveau, _3EME_A)).thenReturn(Optional.empty(), Optional.of(classe));
        when(classeRepository.save(any(Classe.class))).thenReturn(classe);
        when(niveauRepository.findByEtablissement(savedEtab)).thenReturn(List.of(niveau));
        when(classeRepository.findByNiveau(niveau)).thenReturn(List.of(classe));
        RestrictionEtab result = dateRentreeService.getRestrictions(UAI);
        assertThat(result).isNotNull();
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getNiveaux()).hasSize(1);
        assertThat(result.getNiveaux().get(0).getNiveau()).isEqualTo(_3EME);
        assertThat(result.getNiveaux().get(0).getClasses()).hasSize(1);
        assertThat(result.getNiveaux().get(0).getClasses().get(0).getClasse()).isEqualTo(_3EME_A);
    }
}
