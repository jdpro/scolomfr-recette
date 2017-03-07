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
package fr.scolomfr.recette.model.tests.impl.preservationofterms;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.representation.utils.JenaEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Two
 */
@TestCaseIndex(index = "a21")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VERSION2,
		TestParameters.Values.VOCABULARY, TestParameters.Values.SKOSTYPE })
public class PreservationOfTermsBetweenVersionsSkos extends AbstractJenaTestCase {

	private static final String LANG_FR_ATTR = "fr";

	@Override
	public void run() {
		String format = getSkosType();
		if (StringUtils.isEmpty(format)) {
			return;
		}
		Version newVersion = getVersion();
		Model newMdel = getModel(newVersion, getVocabulary(), format);
		Version oldVersion = getVersion(TestParameters.Values.VERSION2);
		Model oldMmodel = getModel(oldVersion, getVocabulary(), format);
		Property prefLabelProperty = oldMmodel.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_PREFLABEL_PROPERTY.toString());
		Selector prefLabelSelector2 = new SimpleSelector((Resource) null, prefLabelProperty, (RDFNode) null);
		StmtIterator stmts2 = oldMmodel.listStatements(prefLabelSelector2);
		// let's loop on the old version uri-prefLabel-litteral triples
		while (stmts2.hasNext()) {
			Statement statement = stmts2.next();
			if (!StringUtils.equals(statement.getLiteral().getLanguage(), LANG_FR_ATTR)) {
				continue;
			}
			// let's look for the same triple in the new version
			Selector prefLabelSelector1 = new SimpleSelector(statement.getSubject(), prefLabelProperty,
					statement.getObject());
			StmtIterator stmts1 = newMdel.listStatements(prefLabelSelector1);
			Statement statement1 = null;
			while (stmts1.hasNext()) {
				statement1 = stmts1.next();
				if (statement1.getLiteral().getLanguage().equals(LANG_FR_ATTR)) {
					break;
				} else {
					statement1 = null;
				}
			}
			if (null == statement1) {
				// it's missing
				String errorCode = null;
				String label = statement.getObject().asLiteral().getValue().toString();
				String resourceUri = statement.getSubject().getURI();
				try {
					errorCode = generateUniqueErrorCode(label + MESSAGE_ID_SEPARATOR + resourceUri);
				} catch (DuplicateErrorCodeException e) {
					logger.error("Errorcode {} generated twice ", errorCode, e);
					continue;
				}
				boolean ignored = errorIsIgnored(errorCode);
				// perhaps the term is here with another label ?
				Selector modifiedLabelSelector = new SimpleSelector(statement.getSubject(), prefLabelProperty,
						(RDFNode) null);
				StmtIterator stmts = newMdel.listStatements(modifiedLabelSelector);
				// eliminate other languages
				Statement statement2 = null;
				while (stmts.hasNext()) {
					statement2 = stmts.next();
					if (statement.getLiteral().getLanguage().equals(LANG_FR_ATTR)) {
						break;
					} else {
						statement2 = null;
					}
				}
				if (null != statement2) {
					// label has changed but URI is not lost
					result.incrementErrorCount(ignored);
					result.addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
							i18n.tr("tests.impl.a21.result.replaced.title"),
							i18n.tr("tests.impl.a21.result.replaced.content", new Object[] { label, resourceUri,
									newVersion, statement2.getObject().asLiteral().getValue().toString() })));
				} else {

					result.incrementErrorCount(ignored);
					result.addMessage(new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
							i18n.tr("tests.impl.a21.result.missing.title"),
							i18n.tr("tests.impl.a21.result.missing.content",
									new Object[] { label, resourceUri, newVersion })));
				}
			}
		}
		result.setState(State.FINAL);

	}

}
