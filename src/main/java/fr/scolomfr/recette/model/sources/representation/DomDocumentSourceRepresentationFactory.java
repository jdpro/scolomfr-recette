/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch 
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
package fr.scolomfr.recette.model.sources.representation;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DomDocumentSourceRepresentationFactory extends SourceRepresentationFactory {

	@Override
	public Document getSourceRepresentation(InputStream inputStream) throws SourceRepresentationBuildException {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		Document ret;

		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new SourceRepresentationBuildException("Unable to create document builder", e);
		}

		try {
			ret = builder.parse(new InputSource(inputStream));
		} catch (SAXException e) {
			throw new SourceRepresentationBuildException("Unable to read XML Document from InputStream", e);
		} catch (IOException e) {
			throw new SourceRepresentationBuildException("Unable to open InputStream", e);
		}
		return ret;
	}

	@Override
	public void setDtdDirectory(String dtdDirectory) {
		// Not used

	}

}
