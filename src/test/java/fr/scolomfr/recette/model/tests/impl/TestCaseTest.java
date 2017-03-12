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
package fr.scolomfr.recette.model.tests.impl;

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
import fr.scolomfr.recette.model.tests.impl.caseconventions.CaseConventionsRespectSkos;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import junit.framework.Assert;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestCaseTest {

	@Autowired
	private CaseConventionsRespectSkos caseConventionsRespectSkos;

	//@Test
	public void testTestCaseIndexAnnotation() {
		Assert.assertEquals("Test index should be a23", "a23", caseConventionsRespectSkos.getIndex());

	}

	@Test
	public void testTestCaseWithWrongParameters() {
		caseConventionsRespectSkos.reset();
		Map<String, String> executionParameters = new HashMap<>();
		String skostype = "skos";
		executionParameters.put(TestParameters.Values.SKOSTYPE, skostype);
		String version = "9.7.6";
		executionParameters.put(TestParameters.Values.VERSION, version);
		String vocabulary = "a99_notexists";
		executionParameters.put(TestParameters.Values.VOCABULARY, vocabulary);
		caseConventionsRespectSkos.setExecutionParameters(executionParameters);
		caseConventionsRespectSkos.run();
		Result result = caseConventionsRespectSkos.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		assertContainsMessage(result, Message.Type.FAILURE, new String[] {},
				new String[] { skostype, version, vocabulary });

	}

	@Test
	public void testTestCaseWithWrongSkosType() {
		caseConventionsRespectSkos.reset();
		Map<String, String> executionParameters = new HashMap<>();
		String skostype = "skosxx";
		executionParameters.put(TestParameters.Values.SKOSTYPE, skostype);
		String version = "0.0.0";
		executionParameters.put(TestParameters.Values.VERSION, version);
		String vocabulary = "a23_invalid";
		executionParameters.put(TestParameters.Values.VOCABULARY, vocabulary);
		caseConventionsRespectSkos.setExecutionParameters(executionParameters);
		caseConventionsRespectSkos.run();
		Result result = caseConventionsRespectSkos.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 2, result.getErrorCount());
		assertContainsMessage(result, Message.Type.FAILURE, new String[] {}, new String[] { skostype });
		assertContainsMessage(result, Message.Type.FAILURE, new String[] {},
				new String[] { skostype, version, vocabulary });
	}
}
