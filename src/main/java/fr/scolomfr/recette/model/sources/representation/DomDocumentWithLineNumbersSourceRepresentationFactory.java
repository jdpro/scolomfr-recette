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

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.scolomfr.recette.model.sources.representation.utils.DomDocumentWithLineNumbersBuilder;

public class DomDocumentWithLineNumbersSourceRepresentationFactory extends SourceRepresentationFactory {
	
	private String dtdDirectory;

	@Override
	public Document getSourceRepresentation(InputStream inputStream) throws SourceRepresentationBuildException {
		Document ret = null;

		try {
			ret = DomDocumentWithLineNumbersBuilder.getInstance().parse(inputStream,getDtdDirectory());
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new SourceRepresentationBuildException(
					"Impossible to get iputStream as dom document with line numbers", e);
		}

		return ret;
	}

	public String getDtdDirectory() {
		return dtdDirectory;
	}

	@Override
	public void setDtdDirectory(String dtdDirectory) {
		this.dtdDirectory = dtdDirectory;
	}

}
