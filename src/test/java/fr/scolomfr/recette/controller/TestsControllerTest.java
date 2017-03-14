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
package fr.scolomfr.recette.controller;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fr.scolomfr.recette.config.MvcConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestsControllerTest {
	private static final String TESTCASE_EXECUTION_PATH = "http://localhost/tests/async/1";

	private static final String TESTCASE_LABEL = "SKOS Recherche de prefLabel dupliqués dans la même branche";

	private static final String TESTCASE_ASYNC_PATH = "/tests/absence_anomalies_libelles_documentation/preflabels_dupliques_meme_branche/a6/";

	private static final String TESTCASE_SYNC_PATH = "/tests/exec/a6/";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		Mockito.reset();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testCaseShouldDisplayWithMetadataAndParameters() throws Exception {
		mockMvc.perform(get(TESTCASE_ASYNC_PATH)).andExpect(status().is2xxSuccessful()).andExpect(view().name("tests"))
				.andExpect(model().attribute("implemented", true))
				.andExpect(model().attribute("implementation", "DuplicatePrefLabelsSkos"))
				.andExpect(model().attribute("testCaseIndex", "a6"))
				.andExpect(model().attribute("testCaseLabel", TESTCASE_LABEL))
				.andExpect(model().attribute("parameters", arrayContainingInAnyOrder(
						Arrays.asList(equalTo("version"), equalTo("vocabulary"), equalTo("skostype")))));

	}

	@Test
	public void testCaseAsyncShouldReturnExcutionUri() throws Exception {
		mockMvc.perform(post(TESTCASE_ASYNC_PATH).header("Accept", "application/json"))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.status", is("INITIATED")))
				.andExpect(jsonPath("$.uri", startsWith(TESTCASE_EXECUTION_PATH)));
		mockMvc.perform(get(TESTCASE_EXECUTION_PATH).header("Accept", "application/json"))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.state", isA(String.class)))
				.andExpect(jsonPath("$.errorCount", isA(Integer.class)))
				.andExpect(jsonPath("$.falsePositiveCount", isA(Integer.class)))
				.andExpect(jsonPath("$.complianceIndicator", isA(Double.class)));

	}

	@Test
	public void testCaseSyncShouldReturnResult() throws Exception {
		mockMvc.perform(get(TESTCASE_SYNC_PATH).header("Accept", "application/json").param("version", "0.0.0"))
				.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$.state", is("FINAL")))
				.andExpect(jsonPath("$.errorCount", isA(Integer.class)))
				.andExpect(jsonPath("$.falsePositiveCount", isA(Integer.class)))
				.andExpect(jsonPath("$.complianceIndicator", isA(Double.class)));
	}

}
