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
package fr.scolomfr.recette.tests.impl.coherenceinterne.libellesidentiques;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.xpath.CachedXPathAPI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuildException;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuilder;
import fr.scolomfr.recette.model.sources.representation.utils.XPathEngineProvider;
import fr.scolomfr.recette.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.tests.execution.result.Message;
import fr.scolomfr.recette.tests.execution.result.Result;
import fr.scolomfr.recette.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.tests.organization.TestCase;
import fr.scolomfr.recette.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Two
 */
@TestCaseIndex(index = "1.3.1")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class LibellesIdentiquesSkos extends AbstractTestCase {

	@Log
	Logger logger;

	@Autowired
	Catalog catalog;

	@Autowired
	XPathEngineProvider xPathEngineProvider;

	@Override
	public void run() {
		String versionStr = executionParameters.get(TestParameters.Values.VERSION);
		Version version = Version.valueOf(versionStr);
		String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, "skos", vocabulary);
		if (null == filePath) {
			result.addError(CommonMessageKeys.FILE_PROVIDED.toString(),
					String.format("Aucun fichier n'est fourni pour la version %s, le format %s et le vocabulaire %s",
							version, "skos", vocabulary));
			return;
		}
		result.addInfo(CommonMessageKeys.FILE_PROVIDED.toString(),
				String.format("Chemin du fichier  pour la version %s, le format %s et le vocabulaire %s : %s", version,
						"skos", vocabulary, filePath));
		InputStream fileInputStream = catalog.getFileByPath(filePath);
		if (null == fileInputStream) {
			result.addError(CommonMessageKeys.FILE_OPENING.toString(),
					String.format("Impossible d'ouvrir le fichier %s", filePath));
			return;
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
			return;
		}
		result.addInfo(CommonMessageKeys.FILE_FORMAT.toString(),
				String.format("La lecture du fichier %s comme XML a réussi", filePath));

		// XPath xpath = xPathEngineProvider.getXpath();
		CachedXPathAPI xpath = new CachedXPathAPI();
		String expressionStr = "/rdf:RDF/rdf:Description";// rdf:Description[skos:prefLabel=
															// following::rdf:Description/skos:prefLabel]";
		NodeList descriptionNodes;
		try {
			// descriptionNodes = (NodeList) xpath.evaluate(expressionStr,
			// vocabularyDocument.getDocumentElement(),
			// XPathConstants.NODESET);
			descriptionNodes = xpath.selectNodeList(vocabularyDocument.getDocumentElement(), expressionStr);
			Map<String, String> identifiersByPrefLabel = new HashMap<>();
			Element descriptionNode;
			NodeList prefLabelNodes;
			Element prefLabelNode;
			String identifier = null;
			String prefLabel;
			for (int i = 0; i < descriptionNodes.getLength(); i++) {
				descriptionNode = (Element) descriptionNodes.item(i);
				expressionStr = "skos:prefLabel";
				// prefLabelNodes = (NodeList) xpath.evaluate(expressionStr,
				// descriptionNode, XPathConstants.NODESET);

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
					result.addError(new Message(prefLabel, String.format("%s et %s ont le même label préférentiel : %s",
							identifier, identifiersByPrefLabel.get(prefLabel), prefLabel)));
				}
			}
			// } catch (XPathExpressionException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
