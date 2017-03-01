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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
@TestCaseIndex(index = "a22")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class VdexIdentifiers extends AbstractJenaTestCase {

	private static UrlValidator urlValidator = new UrlValidator();

	@Autowired
	XPathEngineProvider xPathEngineProvider;

	@Override
	public void run() {
		int numerator = 0;
		int denominator = 0;
		progressionMessage("", 0);
		List<String> vdexFilePaths = new LinkedList<String>();
		if (getVocabulary().equals("global")) {
			vdexFilePaths.addAll(getFilePathsForAllVocabularies(getVersion(), "vdex"));
		} else {
			vdexFilePaths.add(getFilePath(getVersion(), getVocabulary(), "vdex"));
		}
		Map<String, Document> vdexDocuments = new HashMap<>();
		for (String vdexFilePath : vdexFilePaths) {

			vdexDocuments.put(vdexFilePath,
					getDomDocument(vdexFilePath, catalog.getDtddirByVersionAndFormat(getVersion(), "vdex")));
		}
		XPath xpath = xPathEngineProvider.getXpath();
		String expressionStr = "/vdex/term/termIdentifier";

		NodeList identifiers = null;
		Map<String, String> identifiersAndLineNumbers = new HashMap<>();
		try {
			XPathExpression expression = xpath.compile(expressionStr);
			int counter = 0;
			int nbDocs = vdexDocuments.keySet().size();
			for (String filePath : vdexDocuments.keySet()) {
				counter++;
				String docInfo = MessageFormat.format("{0} ({1}/{2})", filePath, counter, nbDocs);
				progressionMessage(docInfo, 0);
				Document vdexDocument = vdexDocuments.get(filePath);
				identifiers = (NodeList) expression.evaluate(vdexDocument, XPathConstants.NODESET);
				int nbIdentifiers = identifiers.getLength();
				int step = Math.min(50, nbIdentifiers / 50 + 1);
				for (int i = 0; i < nbIdentifiers; i++) {
					if (i % step == 0) {
						progressionMessage(docInfo, (float) i / (float) nbIdentifiers * 100.f);
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
						logger.debug("Errorcode {} generated twice ", errorCode, e1);
						try {
							errorCode = generateUniqueErrorCode(
									filePath + MESSAGE_ID_SEPARATOR + identifier + MESSAGE_ID_SEPARATOR + lineNumber);
						} catch (DuplicateErrorCodeException e2) {
							logger.debug("Errorcode {} generated twice ", errorCode, e2);
						}
					}
					boolean ignored = errorIsIgnored(errorCode);
					if (StringUtils.isEmpty(identifier)) {
						result.incrementErrorCount(ignored);
						numerator++;
						Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
								i18n.tr("tests.impl.a22.result.empty.title"),
								i18n.tr("tests.impl.a22.result.empty.content", new Object[] { filePath, lineNumber }));
						result.addMessage(message);
						continue;
					}
					if (identifiersAndLineNumbers.containsKey(identifier)) {
						result.incrementErrorCount(ignored);
						numerator++;
						Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
								i18n.tr("tests.impl.a22.result.duplicate.title"),
								i18n.tr("tests.impl.a22.result.duplicate.content", new Object[] { filePath, lineNumber,
										identifier, identifiersAndLineNumbers.get(identifier) }));
						result.addMessage(message);
						continue;
					} else {
						identifiersAndLineNumbers.put(identifier, lineNumber);
					}

					String uri = null;
					if (urlValidator.isValid(identifier)) {
						continue;
					} else {
						uri = lookForEquivalentUriInVdex(identifier, vdexDocument);
					}
					if (StringUtils.isEmpty(uri)) {
						result.incrementErrorCount(ignored);
						Message message = new Message(
								ignored ? Message.Type.IGNORED : ignored ? Message.Type.IGNORED : Message.Type.ERROR,
								errorCode, i18n.tr("tests.impl.a22.result.nouri.title"),
								i18n.tr("tests.impl.a22.result.nouri.content",
										new Object[] { filePath, lineNumber, identifier }));
						result.addMessage(message);
					}

				}
				progressionMessage(docInfo, 100);
			}
		} catch (XPathExpressionException e) {
			String title = "Invalid xpath expression";
			logger.error(title, e);
			result.addMessage(new Message(Message.Type.FAILURE,
					CommonMessageKeys.XPATH_ERROR.toString() + expressionStr, title, e.getMessage()));
			stopTestCase();
			return;
		}
		progressionMessage("", 100);
		result.setState(State.FINAL);

	}

	private String lookForEquivalentUriInVdex(String nonUriIdentifier, Document vdexDocument)
			throws XPathExpressionException {
		XPath xpath = xPathEngineProvider.getXpath();
		final String expressionStr = MessageFormat.format(
				"/vdex/relationship[./relationshipType=''UF''][./sourceTerm=''{0}'']/targetTerm", nonUriIdentifier);
		NodeList equivalents = null;
		XPathExpression expression = xpath.compile(expressionStr);
		equivalents = (NodeList) expression.evaluate(vdexDocument, XPathConstants.NODESET);
		for (int i = 0; i < equivalents.getLength(); i++) {
			Node node = equivalents.item(i);

			String equivalent = node.getTextContent();
			if (urlValidator.isValid(equivalent)) {
				return equivalent;
			}

		}
		return null;

	}

}
