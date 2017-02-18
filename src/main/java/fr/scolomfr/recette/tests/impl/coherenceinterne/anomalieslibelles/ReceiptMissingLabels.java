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
package fr.scolomfr.recette.tests.impl.coherenceinterne.anomalieslibelles;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.openrdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.representation.utils.QskosException;
import fr.scolomfr.recette.model.sources.representation.utils.QskosResultBuilder;
import fr.scolomfr.recette.tests.execution.result.Message;
import fr.scolomfr.recette.tests.execution.result.Result.State;
import fr.scolomfr.recette.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.tests.organization.TestParameters;

/**
 * Look for Missing Labels
 */
@TestCaseIndex(index = "q6")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class ReceiptMissingLabels extends AbstractTestCase {

	@Autowired
	QskosResultBuilder qskosResultBuilder;

	@Override
	public void run() {
		Version version = getVersion();
		final String vocabulary = getVocabulary();

		final String filePath = getFilePath(version, vocabulary, "skos");

		final File file = getFileByPath(filePath);

		Collection<Resource> data = Collections.emptyList();
		try {
			qskosResultBuilder.setFile(file).setIssueCode("ml");
			result.addMessage(new Message(Message.Type.INFO, "qskos_label_manquant_lance_" + filePath,
					"Lancement de qSkos", "L'utilitaire qSkos a été lancé, veuillez patienter."));
			data = qskosResultBuilder.build();

		} catch (QskosException e) {
			logger.error("Problem with skos : {}", e.getMessage(), e);
			result.addMessage(
					new Message(Message.Type.FAILURE, "label_manquants_" + filePath, "Erreur skos", e.getMessage()));

		}

		Iterator<Resource> it = data.iterator();

		while (it.hasNext()) {
			Resource resource = it.next();
			result.incrementErrorCount();
			result.addMessage(new Message(Message.Type.ERROR, resource.stringValue(), "Label manquant",
					"La ressource " + resource.stringValue() + " n'a pas de prefLabel"));
		}
		result.setState(State.FINAL);
	}

}
