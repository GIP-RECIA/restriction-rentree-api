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
package fr.recia.restriction_rentree_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.dto.RestrictionClasse;
import fr.recia.restriction_rentree_api.dto.RestrictionEtab;
import fr.recia.restriction_rentree_api.dto.RestrictionNiveau;
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
    void shouldReturnTrueWhenClasseIsNullAndBeforeStartDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(null);
        when(request.getEtablissement()).thenReturn(UAI);
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(10));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenClasseIsNullAndBetweenStartAndEndDates() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(null);
        when(request.getEtablissement()).thenReturn(UAI);
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenClasseIsNullAndAfterDefaultEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(null);
        when(request.getEtablissement()).thenReturn(UAI);
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().minusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
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
    void shouldReturnTrueWhenEtablissementNotFoundAndAfterDefaultEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().minusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenEtablissementNotFoundAndBeforeStartDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(10));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEtablissementNotFoundAndBetweenStartAndEndDates() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.empty());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(1));
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
    void shouldReturnTrueWhenNoClasseFoundInDatabaseAndBeforeStartDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(10));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoClasseFoundInDatabaseAndBetweenStartAndEndDates() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenNoClasseFoundInDatabaseAndAfterDefaultEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of());
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().minusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenMultipleClassesFoundAndBeforeStartDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Classe c1 = mock(Classe.class);
        Classe c2 = mock(Classe.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(c1, c2));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(10));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenMultipleClassesFoundAndBetweenStartAndEndDates() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Classe c1 = mock(Classe.class);
        Classe c2 = mock(Classe.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(c1, c2));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().plusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenMultipleClassesFoundAndAfterDefaultEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Classe c1 = mock(Classe.class);
        Classe c2 = mock(Classe.class);
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(c1, c2));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(restrictionProperties.getDefaultDate()).thenReturn(LocalDateTime.now().minusDays(1));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }
    @Test
    void shouldReturnTrueWhenNowIsAfterEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(LocalDateTime.now().minusDays(1)).build();
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenNowIsBeforeStartDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(LocalDateTime.now().plusDays(10)).build();
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().plusDays(1));
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNowIsBetweenStartDateAndEndDate() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(LocalDateTime.now().plusDays(1)).build();
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(1));
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
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(ZonedDateTime.now().minusDays(1).toLocalDateTime()).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUseEtablissementDateWhenClasseAndNiveauDatesAreNull() {
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).dateRentree(ZonedDateTime.now().minusDays(1).toLocalDateTime()).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(null).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
        when(classeRepository.findByNomAndEtablissement(_3EME_A, 1L)).thenReturn(List.of(classe));
        boolean result = dateRentreeService.isAccessAllowed(request);
        assertThat(result).isTrue();
    }

    @Test
    void shouldUseDefaultDateWhenNoDateDefined() {
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1).toLocalDateTime());
        AccessStrategyRequest request = mock(AccessStrategyRequest.class);
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).dateRentree(null).build();
        Niveau niveau = Niveau.builder().nom(_3EME).etablissement(etab).dateRentree(null).build();
        Classe classe = Classe.builder().nom(_3EME_A).niveau(niveau).dateRentree(null).build();
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1).toLocalDateTime());
        when(classeCalculatorService.getClasse(request)).thenReturn(_3EME_A);
        when(request.getEtablissement()).thenReturn(UAI);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(restrictionProperties.getStartDate()).thenReturn(LocalDateTime.now().minusDays(10));
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

    @Test
    void shouldUpdateEtablissementWhenSettingRestriction() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setDateRentreeEtab(ZonedDateTime.now().plusDays(2).toLocalDateTime());
        restriction.setEnabled(false);
        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        dateRentreeService.setNewRestriction(UAI, restriction);
        assertThat(etab.getDateRentree()).isEqualTo(restriction.getDateRentreeEtab());
        assertThat(etab.isEnabled()).isFalse();
        verify(etablissementRepository).save(etab);
    }

    @Test
    void shouldUpdateNiveauAndClasseDatesWhenSettingRestriction() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().id(1L).nom(_3EME).etablissement(etab).build();
        Classe classe = Classe.builder().id(1L).nom(_3EME_A).niveau(niveau).build();

        LocalDateTime newNiveauDate = ZonedDateTime.now().plusDays(3).toLocalDateTime();
        LocalDateTime newClasseDate = ZonedDateTime.now().plusDays(5).toLocalDateTime();

        RestrictionClasse restrictionClasse = new RestrictionClasse();
        restrictionClasse.setClasse(_3EME_A);
        restrictionClasse.setDateRentreeClasse(newClasseDate);

        RestrictionNiveau restrictionNiveau = new RestrictionNiveau();
        restrictionNiveau.setNiveau(_3EME);
        restrictionNiveau.setDateRentreeNiveau(newNiveauDate);
        restrictionNiveau.setClasses(List.of(restrictionClasse));

        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setDateRentreeEtab(etab.getDateRentree());
        restriction.setEnabled(true);
        restriction.setNiveaux(List.of(restrictionNiveau));

        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(niveauRepository.findByEtablissementAndNom(etab, _3EME)).thenReturn(Optional.of(niveau));
        when(classeRepository.findByNiveauAndNom(niveau, _3EME_A)).thenReturn(Optional.of(classe));

        dateRentreeService.setNewRestriction(UAI, restriction);

        assertThat(niveau.getDateRentree()).isEqualTo(newNiveauDate);
        assertThat(classe.getDateRentree()).isEqualTo(newClasseDate);
        verify(niveauRepository).save(niveau);
        verify(classeRepository).save(classe);
    }

    @Test
    void shouldNotTouchClasseRepositoryWhenNiveauHasNoClasses() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().id(1L).nom(_3EME).etablissement(etab).build();

        RestrictionNiveau restrictionNiveau = new RestrictionNiveau();
        restrictionNiveau.setNiveau(_3EME);
        restrictionNiveau.setDateRentreeNiveau(ZonedDateTime.now().plusDays(3).toLocalDateTime());
        restrictionNiveau.setClasses(null);

        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setNiveaux(List.of(restrictionNiveau));

        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(niveauRepository.findByEtablissementAndNom(etab, _3EME)).thenReturn(Optional.of(niveau));

        dateRentreeService.setNewRestriction(UAI, restriction);

        verify(niveauRepository).save(niveau);
        verifyNoInteractions(classeRepository);
    }

    @Test
    void shouldThrowWhenSettingRestrictionForUnknownNiveau() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();

        RestrictionNiveau restrictionNiveau = new RestrictionNiveau();
        restrictionNiveau.setNiveau(_3EME);

        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setNiveaux(List.of(restrictionNiveau));

        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(niveauRepository.findByEtablissementAndNom(etab, _3EME)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dateRentreeService.setNewRestriction(UAI, restriction));
    }

    @Test
    void shouldThrowWhenSettingRestrictionForUnknownClasse() {
        Etablissement etab = Etablissement.builder().id(1L).uai(UAI).enabled(true).build();
        Niveau niveau = Niveau.builder().id(1L).nom(_3EME).etablissement(etab).build();

        RestrictionClasse restrictionClasse = new RestrictionClasse();
        restrictionClasse.setClasse(_3EME_A);

        RestrictionNiveau restrictionNiveau = new RestrictionNiveau();
        restrictionNiveau.setNiveau(_3EME);
        restrictionNiveau.setClasses(List.of(restrictionClasse));

        RestrictionEtab restriction = new RestrictionEtab();
        restriction.setNiveaux(List.of(restrictionNiveau));

        when(etablissementRepository.findByUai(UAI)).thenReturn(Optional.of(etab));
        when(niveauRepository.findByEtablissementAndNom(etab, _3EME)).thenReturn(Optional.of(niveau));
        when(classeRepository.findByNiveauAndNom(niveau, _3EME_A)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dateRentreeService.setNewRestriction(UAI, restriction));
    }

    // =========================================================
    // getRestrictions
    // =========================================================

    @Test
    void shouldCreateEtablissementIfNotExistsAndReturnRestrictions() {
        when(restrictionProperties.getDefaultDate()).thenReturn(ZonedDateTime.now().minusDays(1).toLocalDateTime());
        when(restrictionProperties.getStartDate()).thenReturn(ZonedDateTime.now().minusDays(10).toLocalDateTime());
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
