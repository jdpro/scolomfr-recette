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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthAbsoluteEven;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl;
import fr.scolomfr.recette.model.tests.execution.result.Message.Type;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.organization.TestCase;
import fr.scolomfr.recette.utils.OSInfo;
import fr.scolomfr.recette.utils.OSInfo.OS;

@Component
public class AcsiiConsoleFormatter implements ConsoleFormatter {

	int HEADER_FREQUENCY = 10;

	private RenderedTable getRenderedTable(V2_AsciiTable at) {
		V2_AsciiTableRenderer rend = new V2_AsciiTableRenderer();
		rend.setTheme(V2_E_TableThemes.UTF_STRONG_DOUBLE.get());
		WidthLongestLine width = new WidthLongestLine();

		rend.setWidth(width);
		RenderedTable rt = rend.render(at);
		return rt;
	}

	@Override
	public String formatFormats(List<Pair<String, Pair<String, String>>> filePathsByVersion) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		String previousFormat = "";
		int counter = 0;
		for (Pair<String, Pair<String, String>> source : filePathsByVersion) {
			String format = source.getFirst().toString();
			boolean headerRequired = !StringUtils.equals(format, previousFormat) || 0 == counter % HEADER_FREQUENCY;
			if (headerRequired) {
				// TODO i18n
				at.addRow("Format", "Vocabulary", "File");
				at.addStrongRule();
				counter = 0;
			}
			counter++;
			at.addRow(format, source.getSecond().getFirst(), source.getSecond().getSecond());
			at.addRule();
			previousFormat = format;
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatVersions(List<Pair<Version, Pair<String, String>>> filePathsByFormat) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		String previousVersion = "";
		int counter = 0;
		for (Pair<Version, Pair<String, String>> source : filePathsByFormat) {
			String version = source.getFirst().toString();
			boolean headerRequired = !StringUtils.equals(version, previousVersion) || 0 == counter % HEADER_FREQUENCY;

			if (headerRequired) {
				// TODO i18n
				at.addRow("Version", "Vocabulary", "File");
				at.addStrongRule();
				counter = 0;
			}
			counter++;
			previousVersion = version;
			at.addRow(version, source.getSecond().getFirst(), source.getSecond().getSecond());
			at.addRule();
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatTestOrganisation(Map<String, Map<String, Map<String, Map<String, String>>>> testsOrganisation) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		Set<Entry<String, Map<String, Map<String, Map<String, String>>>>> requirementsEntries = testsOrganisation
				.entrySet();
		for (Entry<String, Map<String, Map<String, Map<String, String>>>> requirementEntry : requirementsEntries) {
			at.addStrongRule();
			at.addRow(null, null,
					requirementEntry.getValue().get("index") + " : " + requirementEntry.getValue().get("label"));
			at.addStrongRule();
			Map<String, Map<String, String>> tests = requirementEntry.getValue().get("tests");
			if (null == tests) {
				continue;
			}
			for (Entry<String, Map<String, String>> entry : tests.entrySet()) {
				at.addRow("", entry.getValue().get("index"), entry.getValue().get("label"));
				at.addRule();
			}
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatVocabularies(Map<String, String> filePathsByVersionAndFormat) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		int counter = 0;
		for (Entry<String, String> source : filePathsByVersionAndFormat.entrySet()) {
			boolean headerRequired = 0 == counter % HEADER_FREQUENCY;
			counter++;
			if (headerRequired) {
				// TODO i18n
				at.addRow("Vocabulary", "File");
				at.addStrongRule();
			}
			at.addRow(source.getKey(), source.getValue());
			at.addRule();
		}
		return getRenderedTable(at).toString();
	}

	@Override
	public String formatMessage(Message message) {
		StringBuilder sb = new StringBuilder(System.lineSeparator());
		sb.append(System.lineSeparator());
		Type type = message.getType();
		String colorMarker = "";
		String reset = "";
		String green = "";
		if (!OSInfo.getOs().equals(OS.WINDOWS)) {
			reset = AnsiConstants.ANSI_RESET;
			green = AnsiConstants.ANSI_GREEN;
			switch (type) {
			case ERROR:
			case FAILURE:
				colorMarker = AnsiConstants.ANSI_RED;
				break;
			case IGNORED:
				colorMarker = AnsiConstants.ANSI_GRAY;
				break;
			case INFO:
				colorMarker = AnsiConstants.ANSI_BLUE;
				break;
			case PROGRESS:
			default:
				return "";
			}
		}
		sb.append(colorMarker);
		sb.append(type).append(" : ");
		sb.append(message.getTitle()).append(System.lineSeparator()).append(reset);
		String content = message.getContent();
		content = content.replaceAll("</[^>]+>", reset);
		content = content.replaceAll("<[^>]+>", green);
		sb.append(content).append(System.lineSeparator());
		return sb.toString();
	}

	class AnsiConstants {

		public static final String ANSI_RESET = "\u001B[0m";
		public static final String ANSI_BLACK = "\u001B[30m";
		public static final String ANSI_RED = "\u001B[31m";
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_YELLOW = "\u001B[33m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_PURPLE = "\u001B[35m";
		public static final String ANSI_CYAN = "\u001B[36m";
		public static final String ANSI_GRAY = "\u001B[37m";
		public static final String ANSI_WHITE = "\u001B[37;1m";

		private AnsiConstants() {
		}
	}

	@Override
	public String formatExecutionResult(Result result) {
		V2_AsciiTable at = new V2_AsciiTable();
		at.addStrongRule();
		at.addRow(result.getErrorCount(), result.getComplianceIndicator() >= 0 ? result.getComplianceIndicator() : "-");
		return getRenderedTable(at).toString();
	}

}
