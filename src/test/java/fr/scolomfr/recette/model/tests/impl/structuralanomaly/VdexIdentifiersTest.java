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
package fr.scolomfr.recette.model.tests.impl.structuralanomaly;

import static fr.scolomfr.recette.model.tests.impl.ResultTestHelper.assertContainsMessage;

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

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class VdexIdentifiersTest {

	@Autowired
	private VdexIdentifiers vdexIdentifiers;

	@Test
	public void testVdexWithoutError() {
		vdexIdentifiers.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a22_valid");
		vdexIdentifiers.setExecutionParameters(executionParameters);
		vdexIdentifiers.run();
		Result result = vdexIdentifiers.getExecutionResult();
		Assert.assertEquals("There should be exactly zero error.", 0, result.getErrorCount());

	}

	@Test
	public void testVdexWithEmptyIdentifier() {
		vdexIdentifiers.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a22_empty");
		vdexIdentifiers.setExecutionParameters(executionParameters);
		vdexIdentifiers.run();
		Result result = vdexIdentifiers.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String file = "scolomfr-tests-v0/a22/a22_empty.vdex";
		String line = "16";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { file, line });
	}

	@Test
	public void testVdexWithIdentifierWithoutUri() {
		vdexIdentifiers.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a22_nouri");
		vdexIdentifiers.setExecutionParameters(executionParameters);
		vdexIdentifiers.run();
		Result result = vdexIdentifiers.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String file = "scolomfr-tests-v0/a22/a22_nouri.vdex";
		String line = "45";
		String word = "creator";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { file, line, word });
	}

	@Test
	public void testVdexWithDuplicateIdentifier() {
		vdexIdentifiers.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a22_duplicate");
		vdexIdentifiers.setExecutionParameters(executionParameters);
		vdexIdentifiers.run();
		Result result = vdexIdentifiers.getExecutionResult();
		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String file = "scolomfr-tests-v0/a22/a22_duplicate.vdex";
		String line1 = "46";
		String line2 = "15";
		String identifier = "http://data.education.fr/voc/scolomfr/concept/creator";
		String key = "a22_scolomfr-tests-v0/a22/a22_duplicate.vdex_http://data.education.fr/voc/scolomfr/concept/creator_46";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {},
				new String[] { file, line1, line2, identifier }, key);
	}

}
