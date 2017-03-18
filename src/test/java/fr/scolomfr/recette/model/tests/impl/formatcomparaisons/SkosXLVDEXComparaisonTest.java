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

import static fr.scolomfr.recette.model.tests.impl.ResultTestHelper.assertContainsMessage;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("web")
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class SkosXLVDEXComparaisonTest {

	@Autowired
	private SkosXLVdexComparaison skosXLVDEXComparaison;

	@Test
	public void testValidVdex() {
		skosXLVDEXComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skosxl");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.2");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a17_valid");
		skosXLVDEXComparaison.setExecutionParameters(executionParameters);
		skosXLVDEXComparaison.run();
		Result result = skosXLVDEXComparaison.getResult();
		Assert.assertEquals("There should be exactly one error.", 0, result.getErrorCount());
	}

	@Test
	public void testVdexWithInvalidURI() {
		skosXLVDEXComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.3");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a17_invalid_uri");
		skosXLVDEXComparaison.setExecutionParameters(executionParameters);
		skosXLVDEXComparaison.run();
		Result result = skosXLVDEXComparaison.getResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String line = "15";
		String invalidUri = "http://data.education.fr/voc/scolomfr/concept/creator";
		String file = "scolomfr-tests-v3/a17/a17_invalid_uri.vdex";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { file, line, invalidUri });

	}

	@Test
	public void testVdexWithInvalidLabel() {
		skosXLVDEXComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.4");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a17_invalid_label");
		skosXLVDEXComparaison.setExecutionParameters(executionParameters);
		skosXLVDEXComparaison.run();
		Result result = skosXLVDEXComparaison.getResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String file = "scolomfr-tests-v4/a17/a17_invalid_label.vdex";
		String wrongLabel = "créateur";
		String rightLabel = "fantaisie";
		String uri = "http://data.education.fr/voc/scolomfr/concept/creator";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {},
				new String[] { file, wrongLabel, rightLabel, uri });

	}

	@Test
	public void testVdexWithMissingURI() {
		skosXLVDEXComparaison.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.5");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a17_missing_uri");
		skosXLVDEXComparaison.setExecutionParameters(executionParameters);
		skosXLVDEXComparaison.run();
		Result result = skosXLVDEXComparaison.getResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String uri = "http://data.education.fr/voc/scolomfr/concept/creator";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { uri });

	}
}
