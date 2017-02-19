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
package fr.scolomfr.recette.model.sources.representation.utils;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;

@Component
public class XPathEngineProvider {
	public XPath getXpath() {
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xPath = xpf.newXPath();
		NamespaceContext nsContext = new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				if (prefix == null)
					throw new NullPointerException("Null prefix");
				else if ("rdf".equals(prefix))
					return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
				else if ("skos".equals(prefix))
					return "http://www.w3.org/2004/02/skos/core#";
				return XMLConstants.NULL_NS_URI;
			}

			@Override
			public String getPrefix(String uri) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Iterator<?> getPrefixes(String uri) {
				throw new UnsupportedOperationException();
			}
		};
		xPath.setNamespaceContext(nsContext);
		return xPath;
	}
}
