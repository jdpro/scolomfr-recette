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
package fr.scolomfr.recette.model.tests.impl.labelanomaly;

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
public class DuplicatePrefLabelsSkosTest {

	@Autowired
	private DuplicatePrefLabelsSkos duplicatePrefLabelsSkos;

	@Test
	public void testSkosWithDuplicate() {
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a6_dup_pref_lab_invalid");
		duplicatePrefLabelsSkos.setExecutionParameters(executionParameters);
		duplicatePrefLabelsSkos.run();
		Result result = duplicatePrefLabelsSkos.getExecutionResult();

		Assert.assertEquals("There should be exactly one error.", 1, result.getErrorCount());
		String uri1 = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-1086";
		String uri2 = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-1077";
		String uri3 = "http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-0008";
		assertContainsMessage(result, Message.Type.ERROR, new String[] {}, new String[] { uri1, uri2, uri3 });

	}

	@Test
	public void testSkosWithoutDuplicate() {
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.VOCABULARY, "a6_dup_pref_lab_valid");
		duplicatePrefLabelsSkos.setExecutionParameters(executionParameters);
		duplicatePrefLabelsSkos.run();
		Result result = duplicatePrefLabelsSkos.getExecutionResult();

		Assert.assertEquals("There should be exactly zero error.", 0, result.getErrorCount());

	}
}
