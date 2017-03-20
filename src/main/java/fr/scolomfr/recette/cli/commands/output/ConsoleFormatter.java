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
package fr.scolomfr.recette.cli.commands.output;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;

@Component
public interface ConsoleFormatter {

	String formatFormats(List<Pair<String, Pair<String, String>>> filePathsByVersion);

	String formatVersions(List<Pair<Version, Pair<String, String>>> filePathsByFormat);

	String formatTestOrganisation(Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation);

	String formatVocabularies(Map<String, String> filePathsByVersionAndFormat);

	String formatMessage(Message message);

	String formatExecutionResult(Result result);

	String formatError(String format);

}
