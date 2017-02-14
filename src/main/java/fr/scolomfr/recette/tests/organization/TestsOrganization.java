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

	/**
	 * Get folder label by requirement key and folder key
	 * 
	 * @param requestedRequirement
	 * @param requestedFolder
	 * @return
	 */
	public String getFolderLabel(String requestedRequirement, String requestedFolder) {
		Map<String, String> folder = getFolder(requestedRequirement, requestedFolder);
		if (null == folder) {
			return null;
		} else {
			return folder.get(LABEL_KEY);
		}
	}

	private Map<String, String> getFolder(String requestedRequirement, String requestedFolder) {
		Map<String, Map<String, Map<String, String>>> requirement = structure.get(requestedRequirement);
		if (null == requirement) {
			return null;
		}
		Map<String, Map<String, String>> folders = requirement.get(FOLDERS_KEY);
		if (null == folders) {
			return null;
		}
		Iterator<String> foldersIterator = folders.keySet().iterator();
		while (foldersIterator.hasNext()) {
			String folder = foldersIterator.next();
			if (folder.equals(requestedFolder)) {
				return folders.get(folder);
			}

		}

		return null;
	}

	/**
	 * Get test case label by test case index
	 * 
	 * @param requestedIndex
	 * @return
	 */
	public String getTestCaseLabel(String requestedIndex) {
		Map<String, String> testCase = getTestCase(requestedIndex);
		if (null == testCase) {
			return null;
		} else {
			return testCase.get(LABEL_KEY);
		}
	}

	private Map<String, String> getTestCase(String requestedIndex) {
		Iterator<String> requirementsIterator = structure.keySet().iterator();
		Map<String, String> requestedTest = Collections.emptyMap();
		while (requirementsIterator.hasNext() && requestedTest.isEmpty()) {
			String requirementKey = requirementsIterator.next();
			Map<String, Map<String, Map<String, String>>> requirement = structure.get(requirementKey);
			Map<String, Map<String, String>> folders = requirement.get(FOLDERS_KEY);
			if (null == folders) {
				continue;
			}
			Iterator<String> foldersIterator = folders.keySet().iterator();
			while (foldersIterator.hasNext() && requestedTest.isEmpty()) {
				String folderKey = foldersIterator.next();
				Map<String, ?> folder = folders.get(folderKey);
				@SuppressWarnings("unchecked")
				Map<String, Map<String, String>> tests = (Map<String, Map<String, String>>) folder.get("tests");
				Iterator<String> testCasesIterator = tests.keySet().iterator();
				while (testCasesIterator.hasNext() && requestedTest.isEmpty()) {
					String formatKey = testCasesIterator.next();
					Map<String, String> test = tests.get(formatKey);
					String testIndex = test.get(INDEX_KEY);
					requestedTest = (testIndex == null || !requestedIndex.equals(testIndex)) ? test : requestedTest;
				}

			}
		}
		return requestedTest;
	}
}
