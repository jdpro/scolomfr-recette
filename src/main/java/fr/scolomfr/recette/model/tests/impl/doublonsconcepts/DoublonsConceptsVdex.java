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
package fr.scolomfr.recette.model.tests.impl.doublonsconcepts;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.utils.XPathEngineProvider;
import fr.scolomfr.recette.model.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
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
