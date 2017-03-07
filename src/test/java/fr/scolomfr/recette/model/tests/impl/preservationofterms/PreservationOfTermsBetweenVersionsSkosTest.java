/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package fr.scolomfr.recette.model.tests.impl.preservationofterms;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.scolomfr.recette.config.MvcConfiguration;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.impl.preservationofterms.PreservationOfTermsBetweenVersionsSkos;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import junit.framework.Assert;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class PreservationOfTermsBetweenVersionsSkosTest {

	@Autowired
	private PreservationOfTermsBetweenVersionsSkos conservationConceptsEntreVersions;

	@Test
	public void testSkosWithLostConcepts() {
		conservationConceptsEntreVersions.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION2, "0.0.0");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.1");
		executionParameters.put("vocabulary", "s2_conservation_concepts_invalid");
		conservationConceptsEntreVersions.setExecutionParameters(executionParameters);
		conservationConceptsEntreVersions.run();
		Result result = conservationConceptsEntreVersions.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		for (Message message : result.getMessages()) {
			if (message.getType().equals(Message.Type.ERROR)) {
				String uri = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-1086";
				String prefLabelBeginning = "économie et gestion,";
				assertThat(uri + " should be found in the message", message.getContent(), containsString(uri));
				assertThat(prefLabelBeginning + " should be found in the message", message.getContent(),
						containsString(prefLabelBeginning));
			}
		}
	}

	@Test
	public void testSkosWithoutLostConcepts() {
		conservationConceptsEntreVersions.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION2, "0.0.0");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.1");
		executionParameters.put("vocabulary", "s2_conservation_concepts_valid");
		conservationConceptsEntreVersions.setExecutionParameters(executionParameters);
		conservationConceptsEntreVersions.run();
		Result result = conservationConceptsEntreVersions.getExecutionResult();
		Assert.assertEquals("There should be exactly 0 error.", 0, result.getErrorCount());

	}
}
