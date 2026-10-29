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

import fr.recia.restriction_rentree_api.configuration.RestrictionProperties;
import fr.recia.restriction_rentree_api.dto.AccessStrategyRequest;
import fr.recia.restriction_rentree_api.dto.TypedList;
import fr.recia.restriction_rentree_api.service.ClasseCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

class ClasseCalculatorServiceTest {

	private static final String UAI = "0450000A";
	private static final String AUTRE_UAI = "0180000A";

	private static ClasseCalculatorService classeCalculatorService;

	@BeforeAll
	static void setUp() {
		RestrictionProperties restrictionProperties = new RestrictionProperties();
		restrictionProperties.setRegexPlaceholder("{UAI}");
		restrictionProperties.setElevesClasseRegex("[^:]+:[^:]+:[^:]+_{UAI}:[^:]+:Eleves_(.+)");
		restrictionProperties.setParentsClasseRegex("[^:]+:[^:]+:[^:]+_{UAI}:[^:]+:Parents_(.+)");
		classeCalculatorService = new ClasseCalculatorService(restrictionProperties);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("classeMatchingCases")
	void shouldReturnExpectedClasse(String testName, List<String> groups, String expectedClasse) {
		AccessStrategyRequest request = buildRequest(UAI, groups);
		String result = classeCalculatorService.getClasse(request);
		assertThat(result).isEqualTo(expectedClasse);
	}

	private static Stream<Arguments> classeMatchingCases() {
		return Stream.of(
				Arguments.of(
						"eleves group matches",
						List.of("e1", "e2", groupe(UAI, "Eleves", "3EME A")),
						"3EME A"),
				Arguments.of(
						"parents group matches",
						List.of("e1", "e2", groupe(UAI, "Parents", "3EME B")),
						"3EME B"),
				Arguments.of(
						"group matching current etab is picked among several etabs",
						List.of(groupe(AUTRE_UAI, "Parents", "3EME C"), groupe(UAI, "Parents", "3EME D")),
						"3EME D"),
				Arguments.of(
						"no group matches (role is neither eleves nor parents)",
						List.of("e1", "e2", groupe(UAI, "Profs", "3EME A")),
						null)
		);
	}

	/** Construit une entrée isMemberOf au format LDAP attendu par les regex de configuration. */
	private static String groupe(String uai, String role, String classe) {
		return "clg45:Etablissements:FICTIF CLG 45_" + uai + ":3EME:" + role + "_" + classe;
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
