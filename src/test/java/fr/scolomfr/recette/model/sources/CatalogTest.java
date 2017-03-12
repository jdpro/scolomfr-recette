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
package fr.scolomfr.recette.model.sources;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.config.MvcConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class CatalogTest {

	@Autowired
	private Catalog catalog;

	@Test
	public void testFilePathFound() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "skos",
				"a6_dup_pref_lab_invalid");
		assertEquals("scolomfr-tests-v0/a6/a6_dup_pref_lab_invalid.skos", filePath);
	}

	@Test
	public void testFilePathNotFound() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "html",
				"i_dont_exist");
		assertNull(filePath);
	}

	@Test
	public void testFileExists() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "html",
				"a19_valid");
		File file = catalog.getFileByPath(filePath, ".rdf");
		assertTrue(file.exists());
	}

	@Test
	public void testFileDoesNotExist() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "html",
				"i_dont_exist");
		File file = catalog.getFileByPath(filePath, ".rdf");
		assertNull(file);
	}

	@Test
	public void testInputStreamNull() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "html",
				"i_dont_exist");
		InputStream is = catalog.getFileInputStreamByPath(filePath);
		assertNull(is);
	}

	@Test
	public void testInputStreamNotNull() {
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(Version.forIntegers(0, 0, 0), "html",
				"a19_valid");
		InputStream is = catalog.getFileInputStreamByPath(filePath);
		assertNotNull(is);
	}

}
