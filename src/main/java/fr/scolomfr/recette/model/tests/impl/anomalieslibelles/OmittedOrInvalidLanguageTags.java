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
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags
 */
@TestCaseIndex(index = "q1")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class OmittedOrInvalidLanguageTags extends AbstractQskosTestCase<Map<Resource, Collection<Literal>>> {

	@Override
	protected String getQskosIssueCode() {
		return "oilt";
	}

	@Override
	protected void populateResult(Map<Resource, Collection<Literal>> data) {
		if (data == null) {
			return;
		}
		Iterator<Resource> it = data.keySet().iterator();

		while (it.hasNext()) {
			Resource resource = it.next();
			Collection<Literal> literals = data.get(resource);
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Literal literal : literals) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(literal.stringValue());
				first = false;
			}
			result.incrementErrorCount();
			String errorCode = generateUniqueErrorCode(resource.stringValue());
			boolean ignored = errorIsIgnored(errorCode);
			result.addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.qskos.oilt.result.title"), i18n.tr("tests.impl.qskos.oilt.result.content",
							new Object[] { resource.stringValue(), sb.toString() })));
		}

	}

}
