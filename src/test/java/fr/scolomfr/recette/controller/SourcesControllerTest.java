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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.config.MvcConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class SourcesControllerTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		Mockito.reset();
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void sourcePageByVersionShouldDisplayFilesByFormat() throws Exception {
		Pair<String, Pair<String, String>> entry = Pair.of("skos",
				Pair.of("a7_valid_uri", "scolomfr-tests-v0/a7/a7_valid_uri.skos"));
		mockMvc.perform(get("/sources/version/0.0.0/")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("sources")).andExpect(model().attribute("by", "version"))
				.andExpect(model().attribute("criterium", "0.0.0"))
				.andExpect(model().attribute("lines", hasItem(is(entry))));

	}

	@Test
	public void sourcePageByFormatShouldDisplayFilesByVersion() throws Exception {
		Pair<Version, Pair<String, String>> entry = Pair.of(Version.forIntegers(0, 0, 0),
				Pair.of("a7_valid_uri", "scolomfr-tests-v0/a7/a7_valid_uri.skos"));
		mockMvc.perform(get("/sources/format/skos/")).andExpect(status().is2xxSuccessful())
				.andExpect(view().name("sources")).andExpect(model().attribute("by", "format"))
				.andExpect(model().attribute("criterium", "skos"))
				.andExpect(model().attribute("lines", hasItem(is(entry))));

	}

}
