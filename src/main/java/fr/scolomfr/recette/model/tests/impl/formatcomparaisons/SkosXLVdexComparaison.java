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
package fr.scolomfr.recette.model.tests.impl.formatcomparaisons;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.representation.utils.DomDocumentWithLineNumbersBuilder;
import fr.scolomfr.recette.model.sources.representation.utils.XPathEngineProvider;
import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Compare list of uris in both formats
 */
@TestCaseIndex(index = "a17")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.SKOSTYPE })
public class SkosXLVdexComparaison extends AbstractJenaTestCase {

	private static UrlValidator urlValidator = new UrlValidator();

	@Autowired
	XPathEngineProvider xPathEngineProvider;

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;

		Version version = getVersion();
		String skosType = getSkosType();
		if (null == version || StringUtils.isEmpty(skosType)) {
			return;
		}
		progressionMessage(0);
		Model model = getModel(version, "global", skosType);

		List<String> vdexFilePaths = getFilePathsForAllVocabularies(version, "vdex");
		Map<String, Document> vdexDocuments = new HashMap<>();
		String dtdDirectory = catalog.getDtddirByVersionAndFormat(version, "vdex");
		for (String vdexFilePath : vdexFilePaths) {
			vdexDocuments.put(vdexFilePath, getDomDocument(vdexFilePath, dtdDirectory));
		}
		XPath xpath = xPathEngineProvider.getXpath();
		String allIdentifiersExpressionStr = "/vdex/term/termIdentifier";
		String labelExpressionStr = "/vdex/term[./termIdentifier=''{0}'']/caption/langstring[@language=''{1}'']/text()";

		// First pass : let's loop on vdex identifiers and look for them in skos
		Set<String> allVdexUriIdentifiers = new HashSet<>();
		NodeList identifiers = null;
		try {
			XPathExpression expression = xpath.compile(allIdentifiersExpressionStr);
			int counter = 0;
			int nbDocs = vdexDocuments.keySet().size();
			float interval = 100.f / (nbDocs == 0 ? 1.f : nbDocs);
			for (String filePath : vdexDocuments.keySet()) {
				float step = counter * interval;

				progressionMessage(step);
				Document vdexDocument = vdexDocuments.get(filePath);
				identifiers = (NodeList) expression.evaluate(vdexDocument, XPathConstants.NODESET);
				int nbIdentifiers = identifiers.getLength();
				for (int i = 0; i < nbIdentifiers; i++) {
					if (i % 100 == 0) {
						progressionMessage(step + interval * i / nbIdentifiers);
					}
					refreshComplianceIndicator(result, (denominator - numerator), denominator);
					denominator++;
					Node node = identifiers.item(i);
					String identifier = node.getTextContent();
					String lineNumber = (String) node.getUserData(DomDocumentWithLineNumbersBuilder.LINE_NUMBER_KEY);
					String errorCode = null;
					try {
						errorCode = generateUniqueErrorCode(filePath + MESSAGE_ID_SEPARATOR
								+ (StringUtils.isEmpty(identifier) ? lineNumber : identifier));
					} catch (DuplicateErrorCodeException e1) {
						try {
							errorCode = generateUniqueErrorCode(
									filePath + MESSAGE_ID_SEPARATOR + identifier + MESSAGE_ID_SEPARATOR + lineNumber);
						} catch (DuplicateErrorCodeException e) {
							logger.debug("Errorcode {} generated twice ", errorCode, e);
						}
					}
					boolean ignored = errorIsIgnored(errorCode);
					if (urlValidator.isValid(identifier)) {
						if (!allVdexUriIdentifiers.contains(identifier)) {
							allVdexUriIdentifiers.add(identifier);
						}
						Resource resource = model.createResource(identifier);
						boolean resourceIsInSkos = model.containsResource(resource);
						if (!resourceIsInSkos) {
							result.incrementErrorCount(ignored);
							numerator++;
							Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
									errorCode, i18n.tr("tests.impl.a17.result.missinginskos.title"),
									i18n.tr("tests.impl.a17.result.missinginskos.content",
											new Object[] { filePath, lineNumber, identifier }));
							result.addMessage(message);
						} else {
							String labelInSkos = jenaEngine.getPrefLabelFor(resource.asNode(), model);
							String expression2Str = MessageFormat.format(labelExpressionStr, identifier, "fr");
							XPathExpression expression2 = xpath.compile(expression2Str);
							Node captionNode = (Node) expression2.evaluate(vdexDocument, XPathConstants.NODE);

							if (null != captionNode) {
								String captionInVdex = captionNode.getTextContent();
								if (!StringUtils.equals(captionInVdex, labelInSkos)) {
									result.incrementErrorCount(ignored);
									numerator++;
									Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
											errorCode, i18n.tr("tests.impl.a17.result.nonmatchinglabels.title"),
											i18n.tr("tests.impl.a17.result.nonmatchinglabels.content",
													new Object[] { identifier, captionInVdex, filePath, labelInSkos }));
									result.addMessage(message);
								}
							}
						}

					}

				}
				counter++;
			}
		} catch (XPathExpressionException e) {
			String title = "Invalid xpath expression";
			logger.error(title, e);
			result.addMessage(new Message(Message.Type.FAILURE,
					CommonMessageKeys.XPATH_ERROR.toString() + allIdentifiersExpressionStr, title, e.getMessage()));
			stopTestCase();
			return;
		}
		// Let's loop on the Skos to find identifiers that would be missing in
		// VDEX
		HashMap<String, String> allPrefLabels = jenaEngine.getAllPrefLabels(model);
		Set<String> missings = allPrefLabels.keySet();
		missings.removeAll(allVdexUriIdentifiers);
		for (String missing : missings) {
			String errorCode;
			try {
				errorCode = generateUniqueErrorCode(
						MessageFormat.format("missing{0}{1}", MESSAGE_ID_SEPARATOR, missing));
			} catch (DuplicateErrorCodeException e) {
				logger.debug("Duplicate error code for uri " + missing, e);
				continue;
			}
			boolean ignored = errorIsIgnored(errorCode);
			result.incrementErrorCount(ignored);
			numerator++;
			Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.a17.result.missinginvdex.title"),
					i18n.tr("tests.impl.a17.result.missinginvdex.content", new Object[] { missing }));
			result.addMessage(message);
		}
		progressionMessage(100);
		result.setState(State.FINAL);

	}

}
