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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuildException;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuilder;
import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Two
 */
@TestCaseIndex(index = "a6")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class PrefLabelDupliquesSkos extends AbstractTestCase {

	@Log
	Logger logger;

	@Autowired
	Catalog catalog;

	@Override
	public void run() {
		String versionStr = executionParameters.get(TestParameters.Values.VERSION);
		Version version;
		try {
			version = Version.valueOf(versionStr);
		} catch (IllegalArgumentException e) {
			logger.error("Le paramètre version {}  est absent ou incorrect", versionStr, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					String.format("Le paramètre version : '%s' est absent ou incorrect", versionStr));
			return;
		}
		String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		if (StringUtils.isEmpty(vocabulary)) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					"Le paramètre vocabulary est absent");
			return;
		}
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, "skos", vocabulary);
		if (null == filePath) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					"Fichier indisponible",
					String.format("Aucun fichier n'est fourni pour la version %s, le format %s et le vocabulaire %s",
							version, "skos", vocabulary));
			return;
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
				"Fichier disponible",
				String.format("Chemin du fichier  pour la version %s, le format %s et le vocabulaire %s : %s", version,
						"skos", vocabulary, filePath));
		InputStream fileInputStream = catalog.getFileInputStreamByPath(filePath);
		if (null == fileInputStream) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					"Fichier impossible à ouvrir", String.format("Impossible d'ouvrir le fichier %s", filePath));
			return;
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_OPENED.toString() + filePath, "Fichier ouvert",
				String.format("L'ouverture du fichier %s a réussi", filePath));
		Document vocabularyDocument;
		try {
			vocabularyDocument = new SourceRepresentationBuilder<Document>(Document.class).inputStream(fileInputStream)
					.build();
		} catch (SourceRepresentationBuildException e) {
			logger.error("Impossible de lire le fichier {} comme du XML", filePath, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_FORMAT.toString() + filePath,
					"XML illisible",
					String.format("Impossible de lire le fichier %s comme du XML : %s", filePath, e.getMessage()));
			return;
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_FORMAT.toString() + filePath, "XML lisible",
				String.format("La lecture du fichier %s comme XML a réussi", filePath));

		CachedXPathAPI xpath = new CachedXPathAPI();
		String expressionStr = "/rdf:RDF/rdf:Description";
		NodeList descriptionNodes;
		try {
			descriptionNodes = xpath.selectNodeList(vocabularyDocument.getDocumentElement(), expressionStr);
			Map<String, String> identifiersByPrefLabel = new HashMap<>();
			Element descriptionNode;
			NodeList prefLabelNodes;
			Element prefLabelNode;
			String identifier = null;
			String prefLabel;
			int descriptionNodesLength = descriptionNodes.getLength();
			for (int i = 0; i < descriptionNodesLength; i++) {
				descriptionNode = (Element) descriptionNodes.item(i);
				expressionStr = "skos:prefLabel";

				prefLabelNodes = xpath.selectNodeList(descriptionNode, expressionStr);

				if (prefLabelNodes.getLength() == 0) {
					continue;
				}
				identifier = descriptionNode.getAttribute("rdf:about");

				prefLabelNode = (Element) prefLabelNodes.item(0);
				prefLabel = prefLabelNode.getTextContent();
				if (StringUtils.isEmpty(prefLabel)) {
					continue;
				}
				if (!identifiersByPrefLabel.containsKey(prefLabel)) {
					identifiersByPrefLabel.put(prefLabel, identifier);
				} else {
					String key = getIndex() + "_" + identifier + "_" + prefLabel;
					if (Math.random() > 0.8)
						result.addMessage(new Message(Message.Type.FAILURE, key, prefLabel,
								String.format("%s et %s ont le même label préférentiel : %s", identifier,
										identifiersByPrefLabel.get(prefLabel), prefLabel)));
				}
			}
			result.setState(State.FINAL);

		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

}
