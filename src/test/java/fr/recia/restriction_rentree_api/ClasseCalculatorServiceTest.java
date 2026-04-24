package fr.recia.restriction_rentree_api;

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.dto.TypedList;
import fr.recia.restriction_rentree_api.service.ClasseCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class ClasseCalculatorServiceTest {

	private static ClasseCalculatorService classeCalculatorService;

	@BeforeAll
	static void setUp() {
		RestrictionProperties restrictionProperties = new RestrictionProperties();
		restrictionProperties.setRegexPlaceholder("{UAI}");
		restrictionProperties.setElevesClasseRegex("[^:]+:[^:]+:[^:]+_{UAI}:[^:]+:Eleves_(.+)");
		restrictionProperties.setParentsClasseRegex("[^:]+:[^:]+:[^:]+_{UAI}:[^:]+:Parents_(.+)");
		classeCalculatorService = new ClasseCalculatorService(restrictionProperties);
	}

	@Test
	void shouldReturnClasseWhenElevesGroupMatches() {
		AccessStrategyRequest request = buildRequest("0450000A", List.of("e1", "e2", "clg45:Etablissements:FICTIF CLG 45_0450000A:3EME:Eleves_3EME A"));
		String result = classeCalculatorService.getClasse(request);
		assertThat(result).isEqualTo("3EME A");
	}

	@Test
	void shouldReturnClasseWhenParentsGroupMatches() {
		AccessStrategyRequest request = buildRequest("0450000A", List.of("e1", "e2", "clg45:Etablissements:FICTIF CLG 45_0450000A:3EME:Parents_3EME B"));
		String result = classeCalculatorService.getClasse(request);
		assertThat(result).isEqualTo("3EME B");
	}

	@Test
	void shouldReturnGroupMatchingCurrentEtab() {
		AccessStrategyRequest request = buildRequest("0450000A", List.of("clg45:Etablissements:FICTIF CLG 45_0180000A:3EME:Parents_3EME C", "clg45:Etablissements:FICTIF CLG 45_0450000A:3EME:Parents_3EME D"));
		String result = classeCalculatorService.getClasse(request);
		assertThat(result).isEqualTo("3EME D");
	}

	@Test
	void shouldReturnNullWhenNoGroupMatches() {
		AccessStrategyRequest request = buildRequest("0450000A", List.of("e1", "e2", "clg45:Etablissements:FICTIF CLG 45_0450000A:3EME:Profs_3EME A"));
		String result = classeCalculatorService.getClasse(request);
		assertThat(result).isNull();
	}

	private TypedList<String> buildTypedList(List<String> values){
		TypedList<String> typedList = new TypedList<>();
		typedList.setType(values.getClass().getName());
		typedList.setValues(values);
		return typedList;
	}

	private AccessStrategyRequest buildRequest(String etablissement, List<String> groups) {
		AccessStrategyRequest request = new AccessStrategyRequest();
		request.setESCOUAICourant(buildTypedList(List.of(etablissement)));
		request.setIsMemberOf(buildTypedList(groups));
		return request;
	}
}
