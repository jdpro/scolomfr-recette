/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
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
package fr.scolomfr.recette.model.tests.impl.standardizedvocabularies;

import java.text.MessageFormat;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class MimeTypesXMLContenthandler implements ContentHandler {

	private String currentRegistry;
	private boolean inRecord;
	private boolean inName;
	private StringBuilder currentNameBuilder;
	private String currentName;
	private boolean inGlobalRegistry;
	private MimeTypesCompleteness owner;

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("registry")) {
			if (!inGlobalRegistry) {
				inGlobalRegistry = true;
			} else {
				for (int index = 0; index < attributes.getLength(); index++) {
					if (attributes.getLocalName(index).equals("id")) {
						currentRegistry = attributes.getValue(index);
					}
				}
			}

		} else if (localName.equals("record")) {
			inRecord = true;
		} else if (localName.equals("name") && inRecord) {
			currentNameBuilder = new StringBuilder();
			currentName = "";
			inName = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("record")) {
			inRecord = false;
		}
		if (localName.equals("name") && inRecord) {
			currentName = currentNameBuilder.toString();
			inName = false;
			String mimeType = MessageFormat.format("{0}/{1}", currentRegistry, currentName);
			owner.submitMimeType(mimeType);
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String str = new String(ch, start, length);
		if (inName) {
			currentNameBuilder.append(str);
		}

	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void setOwner(MimeTypesCompleteness owner) {
		this.owner = owner;		
	}

}
