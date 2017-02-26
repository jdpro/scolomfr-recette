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
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.UrlValidator;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.apiscol.metadata.scolomfr3utils.utils.xml.DomDocumentWithLineNumbersBuilder;
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
		Model model = getModel(getVersion(), "global", getSkosType());

		List<String> vdexFilePaths = getFilePathsForAllVocabularies(getVersion(), "vdex");
		Map<String, Document> vdexDocuments = new HashMap<>();
		String dtdDirectory = catalog.getDtddirByVersionAndFormat(getVersion(), "vdex");
		for (String vdexFilePath : vdexFilePaths) {
			vdexDocuments.put(vdexFilePath, getDomDocument(vdexFilePath, dtdDirectory));
		}
		XPath xpath = xPathEngineProvider.getXpath();
		String expressionStr = "/vdex/term/termIdentifier";

		// First pass : let's loop on vdex identifiers and look for them in skos
		NodeList identifiers = null;
		try {
			XPathExpression expression = xpath.compile(expressionStr);
			for (String filePath : vdexDocuments.keySet()) {
				Document vdexDocument = vdexDocuments.get(filePath);
				identifiers = (NodeList) expression.evaluate(vdexDocument, XPathConstants.NODESET);
				for (int i = 0; i < identifiers.getLength(); i++) {
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
						boolean resourceIsInSkos = model.containsResource(model.createResource(identifier));
						if (!resourceIsInSkos) {
							result.incrementErrorCount(ignored);
							numerator++;
							Message message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR,
									errorCode, i18n.tr("tests.impl.a17.result.missinginskos.title"),
									i18n.tr("tests.impl.a17.result.missinginskos.content",
											new Object[] { filePath, lineNumber, identifier }));
							result.addMessage(message);
						}
					}

				}

			}
		} catch (XPathExpressionException e) {
			String title = "Invalid xpath expression";
			logger.error(title, e);
			result.addMessage(new Message(Message.Type.FAILURE,
					CommonMessageKeys.XPATH_ERROR.toString() + expressionStr, title, e.getMessage()));
			stopTestCase();
			return;
		}

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
		System.out.println(">>>>>>>>>>>>>>>>>>>No equivalent for " + nonUriIdentifier);
		return null;

	}

}
