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
package fr.scolomfr.recette.model.tests.impl.structuralanomaly;

import java.util.Iterator;
import java.util.Map;

import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.util.Tuple;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.relations.UnidirectionallyRelatedConcepts
 */
@TestCaseIndex(index = "q15")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public class UnidirectionallyRelatedConcepts extends AbstractQskosTestCase<Map<Tuple<Resource>, String>> {

	@Override
	protected String getQskosIssueCode() {
		return "urc";
	}

	@Override
	protected void populateResult(Map<Tuple<Resource>, String> data) {
		if (data == null) {
			return;
		}
		Iterator<Tuple<Resource>> it = data.keySet().iterator();

		while (it.hasNext()) {
			Tuple<Resource> resources = it.next();
			String relation = data.get(resources);
			Resource first = resources.getFirst();
			Resource second = resources.getSecond();
			String errorCode = generateUniqueErrorCode(first.stringValue() + relation + second.stringValue());
			boolean ignored = errorIsIgnored(errorCode);
			incrementErrorCount(ignored);
			addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.qskos.urc.result.title"), i18n.tr("tests.impl.qskos.urc.result.content",
							new Object[] { first.stringValue(), relation, second.stringValue() })));
		}

	}

}
