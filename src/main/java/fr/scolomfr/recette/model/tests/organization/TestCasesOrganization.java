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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Wrapper object for test structure taken from tests configuration file
 */
public class TestCasesOrganization {
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

	/**
	 * Get test case label by test case index
	 * 
	 * @param requestedIndex
	 * @return
	 */
	public String getTestCaseLabel(String requestedIndex) {
		Map<String, String> testCase = getTestCaseInformation(requestedIndex);
		if (null == testCase) {
			return null;
		} else {
			return testCase.get(LABEL_KEY);
		}
	}

	private Map<String, String> getTestCaseInformation(String requestedIndex) {
		Iterator<String> requirementsIterator = structure.keySet().iterator();
		Map<String, String> requestedTest = Collections.emptyMap();
		while (requirementsIterator.hasNext() && requestedTest.isEmpty()) {
			String requirementKey = requirementsIterator.next();
			Map<String, Map<String, Map<String, String>>> requirement = structure.get(requirementKey);
			requestedTest = searchInTestsBranch(requestedIndex, requirement);
			Map<String, Map<String, String>> folders = requirement.get(FOLDERS_KEY);

			if (null == folders) {
				continue;
			}
			Iterator<String> foldersIterator = folders.keySet().iterator();
			while (foldersIterator.hasNext() && requestedTest.isEmpty()) {
				String folderKey = foldersIterator.next();
				Map<String, ?> folder = folders.get(folderKey);
				requestedTest = searchInTestsBranch(requestedIndex, folder);
			}
		}
		return requestedTest;
	}

	private Map<String, String> searchInTestsBranch(String requestedIndex, Map<String, ?> container) {
		Map<String, String> requestedTest = Collections.emptyMap();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> tests = (Map<String, Map<String, String>>) container.get("tests");
		if (tests != null) {
			Iterator<String> testCasesIterator = tests.keySet().iterator();
			while (testCasesIterator.hasNext() && requestedTest.isEmpty()) {
				String formatKey = testCasesIterator.next();
				Map<String, String> test = tests.get(formatKey);
				String testIndex = test.get(INDEX_KEY);
				requestedTest = StringUtils.equals(testIndex, requestedIndex) ? test : requestedTest;
			}
		}
		return requestedTest;
	}
}
