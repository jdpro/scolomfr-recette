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
package fr.scolomfr.recette.model.tests.impl.spellchecking;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.springframework.beans.factory.annotation.Autowired;

import fr.scolomfr.recette.model.sources.representation.utils.JenaEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Check the spelling of skos langstrings
 */
@TestCaseIndex(index = "a15")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class SkosSpellChecking extends AbstractJenaTestCase {

	@Autowired
	SpellChecker spellChecker;

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;
		Model model = getModel(getVersion(), getVocabulary(), "skos");
		Property prefLabel = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_PRELABEL_PROPERTY.toString());
		Property altLabel = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_ALTLABEL_PROPERTY.toString());
		Property scopeNote = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_SCOPENOTE_PROPERTY.toString());
		Selector prefLabelSelector = new SimpleSelector(null, prefLabel, (RDFNode) null);
		Selector altLabelSelector = new SimpleSelector(null, altLabel, (RDFNode) null);
		Selector scopeNoteSelector = new SimpleSelector(null, scopeNote, (RDFNode) null);
		StmtIterator stmts1 = model.listStatements(prefLabelSelector);
		StmtIterator stmts2 = model.listStatements(altLabelSelector);
		StmtIterator stmts3 = model.listStatements(scopeNoteSelector);
		ExtendedIterator<Statement> stmts = stmts1.andThen(stmts2).andThen(stmts3);
		Resource vocab001 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-001");
		Resource vocab006 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-006");
		Resource vocab024 = model.getResource("http://data.education.fr/voc/scolomfr/scolomfr-voc-024");
		while (stmts.hasNext()) {
			denominator++;
			Statement statement = stmts.next();

			if (jenaEngine.memberOfVocab(vocab001, statement.getSubject(), model)
					|| jenaEngine.memberOfVocab(vocab024, statement.getSubject(), model)
					|| jenaEngine.memberOfVocab(vocab006, statement.getSubject(), model)) {
				continue;
			}
			Literal labelLit = (Literal) statement.getObject();
			Property predicate = statement.getPredicate();

			String label = labelLit.getString();
			String language = labelLit.getLanguage();
			String errorCode = null;
			try {
				errorCode = generateUniqueErrorCode(statement, predicate, label);
			} catch (DuplicateErrorCodeException e1) {
				logger.debug("Errorcode {} generated twice ", errorCode, e1);
				continue;
			}
			try {
				SpellCheckResult spellCheckResult = spellChecker.spell(label, language);

				boolean ignored = errorIsIgnored(errorCode);
				switch (spellCheckResult.getState()) {
				case INVALID:
					if (!ignored) {
						result.incrementErrorCount();
						numerator++;
					}
					Message message = new Message(ignored ? Message.Type.IGNORED : ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
							i18n.tr("tests.impl.a15.result.invalid.title"),
							i18n.tr("tests.impl.a15.result.invalid.content",
									new Object[] { statement.getSubject().getURI(), label,
											spellCheckResult.getInvalidFragmentsAsString(), predicate.getLocalName(),
											language }));
					result.addMessage(message);
					break;
				case PARTIALY_INVALID:
					if (!ignored) {
						result.incrementErrorCount();
						numerator++;
					}
					result.addMessage(new Message(ignored ? Message.Type.IGNORED : ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
							i18n.tr("tests.impl.a15.result.part.invalid.title"),
							i18n.tr("tests.impl.a15.result.part.invalid.content",
									new Object[] { statement.getSubject().getURI(), label,
											spellCheckResult.getInvalidFragmentsAsString(), predicate.getLocalName(),
											language, spellCheckResult.getNonEvaluatedFragmentsAsString() })));
					break;
				case PARTIALY_VALID:
					result.addMessage(new Message(Message.Type.INFO, errorCode,
							i18n.tr("tests.impl.a15.result.part.valid.title"),
							i18n.tr("tests.impl.a15.result.part.valid.content",
									new Object[] { statement.getSubject().getURI(), label,
											spellCheckResult.getInvalidFragmentsAsString(), predicate.getLocalName(),
											language, spellCheckResult.getNonEvaluatedFragmentsAsString() })));
					break;

				default:
					break;
				}
			} catch (NoDictionaryForLanguageException e) {

				String content = i18n.tr("tests.impl.a15.result.nodic.content", new Object[] { language });
				logger.error(content, e);
				result.addMessage(new Message(Message.Type.INFO, errorCode,
						i18n.tr("tests.impl.a15.result.nodic.title"), content));
				break;
			}
			refreshComplianceIndicator(result, (denominator - numerator), denominator);

		}
		result.setState(State.FINAL);
	}

	private String generateUniqueErrorCode(Statement statement, Property predicate, String label)
			throws DuplicateErrorCodeException {
		return generateUniqueErrorCode(
				Integer.toString(label.hashCode()) + '_' + statement.getSubject() + '_' + predicate.getLocalName());
	}

}
