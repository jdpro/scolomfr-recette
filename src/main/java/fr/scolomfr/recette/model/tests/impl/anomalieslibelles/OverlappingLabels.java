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
package fr.scolomfr.recette.model.tests.impl.anomalieslibelles;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels
 */
@TestCaseIndex(index = "q5")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class OverlappingLabels extends AbstractQskosTestCase<Collection<LabelConflict>> {

	private static String STRING_SPLITTER = "([^ (]+) \\((\"[^\"]+\")@([^,]+),\\s([^)]+)";

	@Override
	protected String getQskosIssueCode() {
		return "ol";
	}

	@Override
	protected void populateResult(Collection<LabelConflict> data) {
		Pattern datePatt = Pattern.compile(STRING_SPLITTER);
		if (data == null) {
			return;
		}
		Iterator<LabelConflict> it = data.iterator();

		while (it.hasNext()) {
			LabelConflict conflict = it.next();
			result.incrementErrorCount();
			String allConflicts = conflict.toString();
			String[] conflictsArray = allConflicts.substring(1, allConflicts.length() - 2).split("\\),\\s");
			StringBuilder contentBuilder = new StringBuilder("<ul>");
			Matcher m;
			for (int i = 0; i < conflictsArray.length; i++) {
				String conflictStr = conflictsArray[i];
				m = datePatt.matcher(conflictStr);
				if (m.matches()) {
					String uri = m.group(1);
					String label = m.group(2);
					String lang = m.group(3);
					String type = m.group(4);
					Object[] testArgs = { uri, label, lang, type };

					MessageFormat format = new MessageFormat(i18n.tr("tests.impl.qskos.ol.result.content"));
					contentBuilder.append(format.format(testArgs));
				}
			}
			contentBuilder.append("</ul>");
			result.addMessage(new Message(Message.Type.ERROR, getErrorCode(DigestUtils.md5Hex(allConflicts)),
					i18n.tr("tests.impl.qskos.ol.result.title"), contentBuilder.toString()));
		}

	}

}
