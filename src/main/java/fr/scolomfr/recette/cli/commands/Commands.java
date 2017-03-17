package fr.scolomfr.recette.cli.commands;

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
import fr.scolomfr.recette.model.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.model.tests.organization.TestCasesRepository;
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
public class Commands implements CommandMarker {
	@Log
	Logger logger;

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
		if (null != version) {
			return consoleFormatter.formatFormats(catalog.getFilePathsByVersion(version));
		} else if (!StringUtils.isEmpty(format)) {
			return consoleFormatter.formatVersions(catalog.getFilePathsByFormat(format));
		}

		return "";
	}

	@CliCommand(value = "tests", help = "Display tests")
	public String displayTests() {
		Version version = null;
		Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation = testsRepository.getTestOrganization().getStructure();
		return consoleFormatter.formatTestOrganisation(testsOrganisation);
	}

}
