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
package fr.scolomfr.recette.tests.impl.coherenceinterne.doublonsconcepts;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuildException;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuilder;
import fr.scolomfr.recette.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.tests.execution.result.Result;
import fr.scolomfr.recette.tests.organization.TestCase;
import fr.scolomfr.recette.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * A URI should not be duplicated duplicated in Skos file
 */
@TestCaseIndex(index = "1.2.1")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class DoublonsConceptsSkos implements TestCase {

	@Log
	Logger logger;

	@Autowired
	Catalog catalog;

	@Override
	public Result getExecutionResult(Map<String, String> executionParameters) {

		Result result = new Result();
		String versionStr = executionParameters.get(TestParameters.Values.VERSION);
		Version version = Version.valueOf(versionStr);
		String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, "skos", vocabulary);
		if (null == filePath) {
			result.addError(CommonMessageKeys.FILE_PROVIDED.toString(),
					String.format("Aucun fichier n'est fourni pour la version %s, le format %s et le vocabulaire %s",
							version, "skos", vocabulary));
			return result;
		}
		result.addInfo(CommonMessageKeys.FILE_PROVIDED.toString(),
				String.format("Chemin du fichier  pour la version %s, le format %s et le vocabulaire %s : %s", version,
						"skos", vocabulary, filePath));
		InputStream fileInputStream = catalog.getFileByPath(filePath);
		if (null == fileInputStream) {
			result.addError(CommonMessageKeys.FILE_OPENING.toString(),
					String.format("Impossible d'ouvrir le fichier %s", filePath));
			return result;
		}
		result.addInfo(CommonMessageKeys.FILE_OPENING.toString(),
				String.format("L'ouverture du fichier %s a réussi", filePath));
		Document vocabularyDocument;
		try {
			vocabularyDocument = new SourceRepresentationBuilder<Document>(Document.class).inputStream(fileInputStream)
					.build();
		} catch (SourceRepresentationBuildException e) {
			logger.error("Impossible de lire le fichier {} comme du XML", filePath, e);
			result.addError(CommonMessageKeys.FILE_FORMAT.toString(),
					String.format("Impossible de lire le fichier %s comme du XML : %s", filePath, e.getMessage()));
			return result;
		}
		result.addInfo(CommonMessageKeys.FILE_FORMAT.toString(),
				String.format("La lecture du fichier %s comme XML a réussi", filePath));
		return result;
	}

}
