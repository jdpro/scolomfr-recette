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
package fr.scolomfr.recette.model.tests.organization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.scolomfr.recette.config.MvcConfiguration;
import fr.scolomfr.recette.model.tests.impl.labelanomaly.DuplicatePrefLabelsSkos;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TestsCasesRegistryTest {

	@Autowired
	private TestCasesRepository testCasesRepository;

	@Test
	public void testTestCaseDefaultInstance() {
		TestCasesRegistry testCasesRegistry = testCasesRepository.getTestCasesRegistry();
		String testCaseIndex = "a6";
		assertEquals(DuplicatePrefLabelsSkos.class, testCasesRegistry.getTestCaseDefaultInstance(testCaseIndex).getClass());
	}

	@Test
	public void testTestCaseNewInstance() {
		TestCasesRegistry testsRegistry = testCasesRepository.getTestCasesRegistry();
		String testCaseIndex = "a6";
		assertEquals(DuplicatePrefLabelsSkos.class, testsRegistry.getTestCaseNewInstance(testCaseIndex).getClass());
	}

}
