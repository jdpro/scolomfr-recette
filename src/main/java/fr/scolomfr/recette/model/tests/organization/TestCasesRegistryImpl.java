/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch
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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.utils.log.Log;

/**
 * Implementation of the {@link TestCasesRegistry}
 */
@Component
public class TestCasesRegistryImpl implements TestCasesRegistry {

	Map<String, TestCase> testCases = new HashMap<>();

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	DefaultListableBeanFactory beanFactory;

	@Override
	public void register(String index, Object bean) {
		if (!(bean instanceof TestCase)) {
			throw new TestsConfigurationException(
					String.format("Test case %s with index %s should implement the Testcase interface",
							bean.getClass().getSimpleName(), index));
		}
		TestCase testCase = (TestCase) bean;
		testCases.put(index, testCase);

	}

	@Override
	public TestCase getTestCaseDefaultInstance(String id) {
		return testCases.get(id);
	}

	@Override
	public TestCase getTestCaseNewInstance(String id) {
		TestCase instance = getTestCaseDefaultInstance(id);
		return beanFactory.createBean(instance.getClass());
	}

}
