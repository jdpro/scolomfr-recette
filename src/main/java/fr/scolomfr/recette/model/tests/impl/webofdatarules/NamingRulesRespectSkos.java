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
package fr.scolomfr.recette.model.tests.impl.webofdatarules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import fr.scolomfr.recette.model.sources.representation.utils.JenaEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.model.tests.utils.NamingUtils;

/**
 * Check respect URI syntax
 */
@TestCaseIndex(index = "a7")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY,
		TestParameters.Values.SKOSTYPE })
public class NamingRulesRespectSkos extends AbstractJenaTestCase {

	private static final String NUMERIC_NAMING_FRAGMENT = "scolomfr-voc";
	private static final String EDUCATION_DOMAIN = "http://data.education.fr";
	private static String conceptRegex = EDUCATION_DOMAIN
			+ "/voc/scolomfr/concept/scolomfr-voc-(\\d{3})-num-(\\d{2,5})";

	private static Pattern regexpPattern = Pattern.compile(conceptRegex);

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;
		progressionMessage(i18n.tr("tests.impl.data.loading.title"), 0);
		String format = getSkosType();
		Model model = getModel(getVersion(), getVocabulary(), format);
		if (null == model) {
			return;
		}
		Property type = model.getProperty(JenaEngine.Constant.RDF_SYNTAX_NS.toString(),
				JenaEngine.Constant.RDF_TYPE_PROPERTY.toString());
		Property inScheme = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.RDF_INSCHEME_PROPERTY.toString());
		RDFNode concept = model.createResource("http://www.w3.org/2004/02/skos/core#Concept");
		Selector selector = new SimpleSelector(null, type, concept);
		StmtIterator stmts = model.listStatements(selector);
		List<Statement> list = stmts.toList();
		int numberOfStatements = list.size();
		Iterator<Statement> it = list.iterator();
		List<String> uris = new ArrayList<>();

		while (it.hasNext()) {
			denominator++;
			if (denominator % 100 == 0) {
				progressionMessage("", (float) denominator / (float) numberOfStatements * 100.f);
			}
			Statement statement = it.next();
			Resource resource = statement.getSubject();
			String uri = resource.getURI();
			if (uris.contains(uri) || !uri.startsWith(EDUCATION_DOMAIN) || !uri.contains(NUMERIC_NAMING_FRAGMENT)) {
				continue;
			}
			uris.add(uri);
			String errorCode = null;
			try {
				errorCode = generateUniqueErrorCode(uri);
			} catch (DuplicateErrorCodeException e) {
				logger.trace(ERROR_CODE_DUPLICATE, errorCode, e);
				continue;
			}

			boolean ignored = errorIsIgnored(errorCode);
			Matcher m = regexpPattern.matcher(uri);
			if (m.matches()) {
				String vocabNumber = m.group(1);
				String term = m.group(2);
				if (term.length() < 3 || term.length() > 4) {
					Message message = new Message(Message.Type.INFO, errorCode,
							i18n.tr("tests.impl.a7.result.weird.title"),
							i18n.tr("tests.impl.a7.result.weird.content", new Object[] { uri }));
					addMessage(message);
				}
				String vocabUri = NamingUtils.getVocabURI(vocabNumber);
				Resource vocabResource = model.createResource(vocabUri);
				Selector inSchemeSelector = new SimpleSelector(resource, inScheme, vocabResource);
				StmtIterator stmts2 = model.listStatements(inSchemeSelector);
				if (!stmts2.hasNext()) {
					Message message = new Message(Message.Type.INFO, errorCode,
							i18n.tr("tests.impl.a7.result.incoherent.title"),
							i18n.tr("tests.impl.a7.result.incoherent.content", new Object[] { uri, vocabNumber }));
					addMessage(message);
				}
			} else {
				numerator++;
				incrementErrorCount(ignored);
				Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
						i18n.tr("tests.impl.a7.result.invalid.title"),
						i18n.tr("tests.impl.a7.result.invalid.content", new Object[] { uri }));
				addMessage(message);
			}
			refreshComplianceIndicator(denominator - numerator, denominator);

		}
		progressionMessage("", 100);
		setState(State.FINAL);
	}

}
