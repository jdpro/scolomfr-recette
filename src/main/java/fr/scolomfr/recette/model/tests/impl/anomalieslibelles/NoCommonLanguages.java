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

import java.util.Collection;
import java.util.Iterator;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.language.NoCommonLanguages
 */
@TestCaseIndex(index = "q3")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class NoCommonLanguages extends AbstractQskosTestCase<Collection<String>> {

	@Override
	protected String getQskosIssueCode() {
		return "ncl";
	}

	@Override
	protected void populateResult(Collection<String> data) {
		if (data == null) {
			return;
		}
		Iterator<String> it = data.iterator();
		boolean commonLanguageFound = false;
		while (it.hasNext()) {
			String lang = it.next();
			commonLanguageFound = true;

			result.addMessage(new Message(Message.Type.INFO, getErrorCode(lang),
					i18n.tr("tests.impl.qskos.ncl.result.info.title"),
					i18n.tr("tests.impl.qskos.ncl.result.info.content", new Object[] { lang })));
		}
		if (!commonLanguageFound) {
			result.incrementErrorCount();
			result.addMessage(new Message(Message.Type.ERROR, getErrorCode(""),
					i18n.tr("tests.impl.qskos.ncl.result.error.title"),
					i18n.tr("tests.impl.qskos.ncl.result.error.content", null)));

		}

	}

}
