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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import fr.scolomfr.recette.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.tests.execution.result.Result;
import fr.scolomfr.recette.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.tests.organization.TestCase;
import fr.scolomfr.recette.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * A URI should not be duplicated duplicated in Vdex file
 */
@TestCaseIndex(index = "1.2.2")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class DoublonsConceptsVdex extends AbstractTestCase {

	@Log
	Logger logger;

	@Autowired
	Catalog catalog;

	@Autowired
	XPathEngineProvider xPathEngineProvider;

	@Override
	public void run() {

	}

	/**
	 * Too slow
	 * 
	 * @param vocabularyDocument
	 */
	private void xPathSolution(Document vocabularyDocument) {
		XPath xpath = xPathEngineProvider.getXpath();
		String expressionStr = "//rdf:Description[@rdf:about = following::rdf:Description/@rdf:about]";

		NodeList duplicateIdentifiers = null;
		try {
			XPathExpression expression = xpath.compile(expressionStr);
			duplicateIdentifiers = (NodeList) expression.evaluate(vocabularyDocument.getDocumentElement(),
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-------------------------------------" + duplicateIdentifiers.getLength());
		for (int i = 0; i < duplicateIdentifiers.getLength(); i++) {
			Node node = duplicateIdentifiers.item(i);
			System.out.println("-------------------------------------");
			System.out.println(node.getAttributes().item(0).getNodeValue());
		}
	}

}
