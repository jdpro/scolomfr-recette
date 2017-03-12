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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import fr.scolomfr.recette.config.MvcConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestsIgnoreControllerTest {
	private static final String KEY = "a6_génie civil, option topographiehttp://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-1669http://data.education.fr/voc/scolomfr/concept/scolomfr-voc-014-num-2781";

	private static final String TEST_IGNORE_PATH = "/tests/ignore-false-positive";
	private static final String TEST_RESTORE_PATH = "/tests/restore-true-positive";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		Mockito.reset();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testResultShouldBeIgnoredAndRestored() throws Exception {
		String key = KEY + UUID.randomUUID();
		mockMvc.perform(post(TEST_IGNORE_PATH).param("key", key).accept("application/json"))
				.andExpect(jsonPath("$.key", is(key))).andExpect(jsonPath("$.state", is("ACCEPTED")));
		mockMvc.perform(post(TEST_RESTORE_PATH).param("key", key).accept("application/json"))
				.andDo(MockMvcResultHandlers.print()).andExpect(jsonPath("$.key", is(key)))
				.andExpect(jsonPath("$.state", is("ACCEPTED")));

	}

	@Test
	public void duplicateIgnoreRequestShouldbeNotified() throws Exception {
		String key = KEY + UUID.randomUUID();
		mockMvc.perform(post(TEST_IGNORE_PATH).param("key", key).accept("application/json"))
				.andExpect(jsonPath("$.key", is(key))).andExpect(jsonPath("$.state", is("ACCEPTED")));
		mockMvc.perform(post(TEST_IGNORE_PATH).param("key", key).accept("application/json"))
				.andDo(MockMvcResultHandlers.print()).andExpect(jsonPath("$.key", is(key)))
				.andExpect(jsonPath("$.state", is("DUPLICATE")));

	}

}
