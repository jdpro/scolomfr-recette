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
import fr.scolomfr.recette.model.tests.impl.TestCaseInterruptionException;
import fr.scolomfr.recette.model.tests.organization.TestCase;
import fr.scolomfr.recette.model.tests.organization.TestCasesRepository;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.i18n.I18nProvider;
import fr.scolomfr.recette.utils.log.Log;

/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017 Direction du Numérique pour l'éducation - Ministère de
 * l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
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

	@Autowired
	private I18nProvider i18n;

	@CliCommand(value = "sources", help = "Display ScoLOMFR sources by version and/or format, example : 'sources --version 3.1.0', 'sources --format vdex', 'sources --version 3.2.0 --format html'")
	public String displaySources(
			@CliOption(key = {
					"version" }, mandatory = false, help = "Display selected version : major.minor.patch, e.g. '3.2.0'", unspecifiedDefaultValue = "", specifiedDefaultValue = "") final String versionStr,
			@CliOption(key = {
					"format" }, mandatory = false, help = "Display selected format : e.g. 'skos'", unspecifiedDefaultValue = "", specifiedDefaultValue = "") final String format) {
		forceLocale();
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

	private void forceLocale() {
		i18n.setLocale(org.springframework.util.StringUtils.parseLocaleString("en_EN"));

	}

	@CliCommand(value = "tests", help = "Display testcases reference, without options.")
	public String displayTests() {
		forceLocale();
		Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation = testsRepository
				.getTestOrganization().getStructure();
		return consoleFormatter.formatTestOrganisation(testsOrganisation);
	}

	@CliCommand(value = "test", help = "Execute specific test referenced by code, e.g 'test a6 --version 3.2.0 --format skos --vocabulary global'")
	public String execute(
			@CliOption(key = {
					"" }, mandatory = true, help = "Test code, e.g. 'a6' . Type 'test' command for a complete test reference.", unspecifiedDefaultValue = "", specifiedDefaultValue = "") final String index,
			@CliOption(key = {
					"version" }, mandatory = false, help = "The version : major.minor.patch, e.g. '3.2.0' for ScoLOMFR 3.2.", unspecifiedDefaultValue = "", specifiedDefaultValue = "") final String versionStr,
			@CliOption(key = { "format" }, mandatory = false, help = "The format e.g. skos") final String format,
			@CliOption(key = {
					"vocabulary" }, mandatory = false, help = "Vocabulary to test : 'global' or specific URI. Type sources --version x.y.z to get a list of available vocabularies for a specific ScoLOMFR version.", unspecifiedDefaultValue = "", specifiedDefaultValue = "") final String vocabulary) {
		forceLocale();
		Version version = null;
		if (!StringUtils.isEmpty(versionStr)) {
			version = Version.valueOf(versionStr);
		}
		TestCase testCase = testsRepository.getTestCasesRegistry().getTestCaseNewInstance(index);
		if (null == testCase) {
			return consoleFormatter.formatError(MessageFormat.format("No test available under index ''{0}''", index));
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
		try {
			testCase.run();
		} catch (TestCaseInterruptionException e) {
			String message = MessageFormat
					.format("The attempt to start the ''{0}'' test failed, refer to Error Messages.", index);
			logger.trace(message, e);
			return consoleFormatter.formatError(message);
		}

		return "";
	}

	@Override
	public void markForDeletion(Integer executionIdentifier) {
		// nothing
	}

	@Override
	public void notify(Message message) {
		if (System.console() == null)
			System.out.println(consoleFormatter.formatMessage(message));
		else
			System.console().printf(consoleFormatter.formatMessage(message));
	}

	@Override
	public void notifyTestCaseTermination(Result result) {
		if (System.console() == null)
			System.out.println(consoleFormatter.formatExecutionResult(result));
		else
			System.console().printf(consoleFormatter.formatExecutionResult(result));
	}

}
