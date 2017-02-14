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
package fr.scolomfr.recette.tests.organization;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper object for test structure taken from tests configuration file
 */
public class TestsOrganization {
	private static final String FOLDERS_KEY = "folders";
	private static final String INDEX_KEY = "index";
	private static final String LABEL_KEY = "label";
	private Map<String, Map<String, Map<String, Map<String, String>>>> structure;

	public Map<String, Map<String, Map<String, Map<String, String>>>> getStructure() {
		return structure;
	}

	public void setStructure(Map<String, Map<String, Map<String, Map<String, String>>>> structure) {
		this.structure = structure;
	}

	public String getFolderLabel(String key) {
		Map<String, String> folder = getFolder(key);
		if (null == folder) {
			return null;
		} else {
			return folder.get(LABEL_KEY);
		}
	}

	private Map<String, String> getFolder(String key) {
		Iterator<String> it1 = structure.keySet().iterator();
		while (it1.hasNext()) {
			String requirement = it1.next();
			Map<String, Map<String, Map<String, String>>> map = structure.get(requirement);
			Map<String, Map<String, String>> folders = map.get(FOLDERS_KEY);
			if (null == folders) {
				continue;
			}
			Iterator<String> it2 = folders.keySet().iterator();
			while (it2.hasNext()) {
				String folder = it2.next();
				if (folder.equals(key)) {
					return folders.get(folder);
				}

			}
		}
		return null;
	}

	public String getTestCaseLabel(String key) {
		Map<String, String> testCase = getTestCase(key);
		if (null == testCase) {
			return null;
		} else {
			return testCase.get(LABEL_KEY);
		}
	}

	private Map<String, String> getTestCase(String key) {
		Iterator<String> it1 = structure.keySet().iterator();
		Map<String, String> requestedTest = Collections.emptyMap();
		while (it1.hasNext() && requestedTest.isEmpty()) {
			String requirement = it1.next();
			Map<String, Map<String, Map<String, String>>> map = structure.get(requirement);
			Map<String, Map<String, String>> folders = map.get(FOLDERS_KEY);
			if (null == folders) {
				continue;
			}
			Iterator<String> it2 = folders.keySet().iterator();
			while (it2.hasNext() && requestedTest.isEmpty()) {
				String folder = it2.next();
				Map<String, ?> map2 = folders.get(folder);
				@SuppressWarnings("unchecked")
				Map<String, Map<String, String>> tests = (Map<String, Map<String, String>>) map2.get("tests");
				Iterator<String> it3 = tests.keySet().iterator();
				while (it3.hasNext() && requestedTest.isEmpty()) {
					String formatKey = it3.next();
					Map<String, String> test = tests.get(formatKey);
					String testIndex = test.get(INDEX_KEY);
					requestedTest = (testIndex == null || !key.equals(testIndex)) ? test : requestedTest;
				}

			}
		}
		return requestedTest;
	}
}
