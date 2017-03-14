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
package fr.scolomfr.recette.model.tests.impl.labelanomaly;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.impl.AbstractQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels
 */
@TestCaseIndex(index = "q5")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public class OverlappingLabels extends AbstractQskosTestCase<Collection<LabelConflict>> {

	@Override
	protected String getQskosIssueCode() {
		return "ol";
	}

	protected boolean isRestrictedToPrefLabels() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void populateResult(Collection<LabelConflict> data) {
		if (data == null) {
			return;
		}
		Iterator<LabelConflict> it = data.iterator();
		while (it.hasNext()) {
			LabelConflict conflict = it.next();
			Set<LabeledConcept> concepts = null;
			try {
				concepts = (Set<LabeledConcept>) FieldUtils.readField(conflict, "conflicts", true);
			} catch (IllegalAccessException e) {
				logger.trace("LabelConflict is decided not to share its data", e);
			}
			int prefLabelsFound = 0;
			StringBuilder contentBuilder = new StringBuilder("<ul>");
			for (LabeledConcept concept : concepts) {
				String uri = concept.getConcept().stringValue();
				String label = concept.getLiteral().stringValue();
				String lang = concept.getLiteral().getLanguage();
				String type = concept.getLabelType().name();
				Object[] testArgs = { uri, label, lang, type };
				if (isRestrictedToPrefLabels() && !type.equals(LabelType.PREF_LABEL.name())) {
					continue;
				}
				prefLabelsFound++;
				MessageFormat format = new MessageFormat(i18n.tr("tests.impl.qskos.ol.result.content"));
				contentBuilder.append(format.format(testArgs));
			}
			contentBuilder.append("</ul>");
			if (0 == prefLabelsFound && isRestrictedToPrefLabels()) {
				continue;
			}

			String errorCode = generateUniqueErrorCode(DigestUtils.md5Hex(contentBuilder.toString()));
			boolean ignored = errorIsIgnored(errorCode);
			result.incrementErrorCount(ignored);
			result.addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.qskos.ol.result.title", new Object[] { prefLabelsFound }),
					contentBuilder.toString()));
		}

	}

}
