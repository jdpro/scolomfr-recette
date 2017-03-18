package fr.scolomfr.recette.cli.commands;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.cli.commands.output.ConsoleFormatter;
import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTracker;
import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTrackingAspect;
import fr.scolomfr.recette.model.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.impl.AbstractTestCase.ExecutionMode;
import fr.scolomfr.recette.model.tests.organization.TestCase;
import fr.scolomfr.recette.model.tests.organization.TestCasesRepository;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017 MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
@Component
@Profile({ "!web" })
public class Commands implements CommandMarker, TestCaseExecutionTracker {
	@Log
	Logger logger;

	@Autowired
	TestCaseExecutionTrackingAspect testCaseExecutionTrackingAspect;

	@Autowired
	private Catalog catalog;

	@Autowired
	TestCasesRepository testsRepository;

	@Autowired
	TestCaseExecutionRegistry testCaseExecutionRegistry;

	@Autowired
	ConsoleFormatter consoleFormatter;

	@CliCommand(value = "sources", help = "Display sources by version and/or format")
	public String displaySources(
			@CliOption(key = {
					"version" }, mandatory = false, help = "The version major.minor.patch") final String versionStr,
			@CliOption(key = { "format" }, mandatory = false, help = "The format e.g. skos") final String format) {
		Version version = null;
		if (!StringUtils.isEmpty(versionStr)) {
			version = Version.valueOf(versionStr);
		}
		if (null != version && !StringUtils.isEmpty(format)) {
			return consoleFormatter.formatVocabularies(catalog.getFilePathsByVersionAndFormat(version, format));
		} else if (null != version) {
			return consoleFormatter.formatFormats(catalog.getFilePathsByVersion(version));
		} else if (!StringUtils.isEmpty(format)) {
			return consoleFormatter.formatVersions(catalog.getFilePathsByFormat(format));
		}

		return "";
	}

	@CliCommand(value = "tests", help = "Display tests")
	public String displayTests() {
		Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation = testsRepository
				.getTestOrganization().getStructure();
		return consoleFormatter.formatTestOrganisation(testsOrganisation);
	}

	@CliCommand(value = "test", help = "Execute specific test")
	public String execute(
			@CliOption(key = { "index" }, mandatory = false, help = "Test index e.g. a6") final String index,
			@CliOption(key = {
					"version" }, mandatory = false, help = "The version major.minor.patch") final String versionStr,
			@CliOption(key = { "format" }, mandatory = false, help = "The format e.g. skos") final String format,
			@CliOption(key = {
					"vocabulary" }, mandatory = false, help = "Vocabulary to test : 'global' or specific URI") final String vocabulary) {
		Version version = null;
		if (!StringUtils.isEmpty(versionStr)) {
			version = Version.valueOf(versionStr);
		}
		TestCase testCase = testsRepository.getTestCasesRegistry().getTestCaseNewInstance(index);
		if (testCase == null) {
			return MessageFormat.format("No test available under index {0}", index);
		}
		testCase.reset();
		testCaseExecutionTrackingAspect.setOwner(this);
		testCase.setExecutionMode(ExecutionMode.ASYNCHRONOUS);
		Map<String, String> executionParameters = new HashMap<>();
		if (null != version) {
			executionParameters.put(TestParameters.Values.VERSION, versionStr);
		}
		if (!StringUtils.isEmpty(format)) {
			executionParameters.put(TestParameters.Values.FORMAT, format);
		}
		if (!StringUtils.isEmpty(vocabulary)) {
			executionParameters.put(TestParameters.Values.VOCABULARY, vocabulary);
		}
		testCase.setExecutionParameters(executionParameters);
		testCase.run();

		return "";
	}

	@Override
	public void markForFutureDeletion(Integer executionIdentifier) {
		// nothing

	}

	@Override
	public void notify(Message message) {
		System.console().printf(consoleFormatter.formatMessage(message));
	}

	@Override
	public void notifyTestCaseTermination(Result result) {
		System.console().printf(consoleFormatter.formatExecutionResult(result));

	}

}
