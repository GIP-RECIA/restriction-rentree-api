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
