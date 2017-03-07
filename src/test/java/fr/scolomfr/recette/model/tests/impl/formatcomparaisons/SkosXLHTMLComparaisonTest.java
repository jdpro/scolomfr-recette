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
package fr.scolomfr.recette.model.tests.impl.formatcomparaisons;

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
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import junit.framework.Assert;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.containsString;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class SkosXLHTMLComparaisonTest {

	@Autowired
	private SkosXLHtmlComparaison skosXLHtmlComparaison;

	@Test
	public void testHtmlWithInvalidLabel() {
		skosXLHtmlComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a19_label_invalid");
		skosXLHtmlComparaison.setExecutionParameters(executionParameters);
		skosXLHtmlComparaison.run();
		Result result = skosXLHtmlComparaison.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		int nbErrors = 0;
		for (Message message : result.getMessages()) {
			if (message.getType().equals(Message.Type.ERROR)) {
				nbErrors++;
				String invalidLabel = "<strong>un libellé invalide</strong>";
				assertThat(invalidLabel + " should be found in the message", message.getContent(),
						containsString(invalidLabel));

			}
		}
		assertEquals(1, nbErrors);

	}
	
	@Test
	public void testHtmlWithInvalidTA() {
		skosXLHtmlComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a19_ta_invalid");
		skosXLHtmlComparaison.setExecutionParameters(executionParameters);
		skosXLHtmlComparaison.run();
		Result result = skosXLHtmlComparaison.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		int nbErrors = 0;
		for (Message message : result.getMessages()) {
			if (message.getType().equals(Message.Type.ERROR)) {
				nbErrors++;
				String uri = "http://data.education.fr/voc/scolomfr/concept/exam";
				String label = "examen";
				String invalideAssociatedTerm = "préparation à l'examen";
				assertThat(uri + " should be found in the message", message.getContent(), containsString(uri));
				assertThat(label + " should be found in the message", message.getContent(), containsString(label));
				assertThat(invalideAssociatedTerm + " should be found in the message", message.getContent(),
						containsString(invalideAssociatedTerm));

			}
		}
		assertEquals(1, nbErrors);

	}

	@Test
	public void testHtmlWithInvalidNA() {
		skosXLHtmlComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a19_na_invalid");
		skosXLHtmlComparaison.setExecutionParameters(executionParameters);
		skosXLHtmlComparaison.run();
		Result result = skosXLHtmlComparaison.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		int nbErrors = 0;
		for (Message message : result.getMessages()) {
			if (message.getType().equals(Message.Type.ERROR)) {
				nbErrors++;
				String uri = "http://data.education.fr/voc/scolomfr/concept/exam";
				String label = "examen";
				String invalideScopeNote = "Note modifiée";
				assertThat(uri + " should be found in the message", message.getContent(), containsString(uri));
				assertThat(label + " should be found in the message", message.getContent(), containsString(label));
				assertThat(invalideScopeNote + " should be found in the message", message.getContent(),
						containsString(invalideScopeNote));

			}
		}
		assertEquals(1, nbErrors);

	}

	@Test
	public void testValidHtml() {
		skosXLHtmlComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a19_valid");
		skosXLHtmlComparaison.setExecutionParameters(executionParameters);
		skosXLHtmlComparaison.run();
		Result result = skosXLHtmlComparaison.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 0, result.getErrorCount());
	}
}
