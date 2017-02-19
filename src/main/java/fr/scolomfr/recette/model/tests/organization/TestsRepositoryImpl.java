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

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.resources.EmbeddedResourcesLoader;
import fr.scolomfr.recette.utils.log.Log;

/**
 * @see TestsRepository
 */
@Component
public class TestsRepositoryImpl implements TestsRepository {
	private static final String TESTS_ORGANIZATION_FILE = "/tests.yml";

	@Log
	Logger logger;

	@Autowired
	EmbeddedResourcesLoader resourcesLoader;

	@Autowired
	TestsOrganizationParser testOrganizationParser;

	@Autowired
	private TestCasesRegistry testCasesRegistry;

	private TestsOrganization testOrganization;

	/**
	 * Loads tests organization file at server startup
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() throws IOException {

		logger.info("Loading tests organization file from {}", TESTS_ORGANIZATION_FILE);

		try (InputStream manifestInputStream = resourcesLoader.loadResource(TESTS_ORGANIZATION_FILE)) {
			if (null == manifestInputStream) {
				logger.error("No manifest file {}, cancel startup.", TESTS_ORGANIZATION_FILE);
				throw new TestsConfigurationException(
						"Impossible to load manifest file " + TESTS_ORGANIZATION_FILE + ", package assembly error.");
			}
			logger.info("Tests organization file found, parsing data.");
			setTestOrganization(testOrganizationParser.load(manifestInputStream).getTestOrganization());
			logger.info("Tests organization loaded.");
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw e;
		}

	}

	@Override
	public TestsOrganization getTestOrganization() {
		return testOrganization;
	}

	public void setTestOrganization(TestsOrganization testOrganization) {
		this.testOrganization = testOrganization;
	}

	@Override
	public TestCasesRegistry getTestCasesRegistry() {
		return testCasesRegistry;
	}

}
