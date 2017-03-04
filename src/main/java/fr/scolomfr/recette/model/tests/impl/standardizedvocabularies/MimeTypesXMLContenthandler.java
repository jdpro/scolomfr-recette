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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import fr.scolomfr.recette.utils.log.Log;

@Component
public class MimeTypesXMLContenthandler implements ContentHandler {

	private static final String FILE_TAG_NAME = "file";
	private static final String NAME_TAG_NAME = "name";
	private static final String RECORD_TAG_NAME = "record";
	private static final String REGISTRY_TAG_NAME = "registry";

	@Log
	Logger logger;

	private boolean inRecord;
	private boolean inName;
	private boolean inFile;
	private StringBuilder currentNameBuilder;
	private StringBuilder currentFileBuilder;
	private String currentName;
	private String currentFile;
	private boolean inGlobalRegistry;
	private MimeTypesCompleteness owner;
	// Not to start with error at first registry tag
	private boolean submitted = true;

	public void reset() {
		inGlobalRegistry = inRecord = inName = inFile = false;
		submitted = true;
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// nothing

	}

	@Override
	public void startDocument() throws SAXException {
		// nothing
	}

	@Override
	public void endDocument() throws SAXException {
		// nothing

	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		// nothing

	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// nothing

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals(REGISTRY_TAG_NAME)) {
			if (!inGlobalRegistry) {
				inGlobalRegistry = true;
			} else {
				if (!submitted) {
					logger.error(MessageFormat.format("No mime type submitted for file {0} / name {1}", currentFile,
							currentName));
				}
				submitted = false;
			}

		} else if (localName.equals(RECORD_TAG_NAME)) {
			inRecord = true;
		} else if (localName.equals(NAME_TAG_NAME) && inRecord) {
			currentNameBuilder = new StringBuilder();
			currentName = "";
			inName = true;
		} else if (localName.equals(FILE_TAG_NAME) && inRecord) {
			currentFileBuilder = new StringBuilder();
			currentFile = "";
			inFile = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals(RECORD_TAG_NAME)) {
			inRecord = false;
		} else if (localName.equals(NAME_TAG_NAME) && inRecord) {
			currentName = currentNameBuilder.toString();
			inName = false;
			submitMimeTypeIfPossible();
		} else if (localName.equals(FILE_TAG_NAME) && inRecord) {
			currentFile = currentFileBuilder.toString();
			inFile = false;
			submitMimeTypeIfPossible();
		}

	}

	private void submitMimeTypeIfPossible() {
		if (!StringUtils.isEmpty(currentFile) && !StringUtils.isEmpty(currentName)) {
			owner.submitMimeType(currentFile, currentName);
			currentFile = "";
			currentName = "";
			submitted = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String str = new String(ch, start, length);
		if (inName) {
			currentNameBuilder.append(str);
		} else if (inFile) {
			currentFileBuilder.append(str);
		}

	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		// nothing

	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		// nothing

	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// nothing

	}

	public void setOwner(MimeTypesCompleteness owner) {
		this.owner = owner;
	}

}
