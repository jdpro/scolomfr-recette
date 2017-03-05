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
package fr.scolomfr.recette.model.tests.impl.serializationformat;

import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import fr.scolomfr.recette.model.tests.organization.TestCase;

@Component
public class CustomNuContentHandler implements ContentHandler {

	private HTMLW3CCompliance owner;
	private StringBuilder messageBuilder;
	private boolean inMessage;

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws SAXException {
		owner.submitMessage(MessageType.END, "");

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
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("message")) {
			inMessage = true;
		} else if (localName.equals("code")) {
			messageBuilder.append("<code>");
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (localName.equals("info") || localName.equals("error")) {
			MessageType messageType = MessageType.fromString(localName);
			owner.submitMessage(messageType, messageBuilder.toString());
			messageBuilder = new StringBuilder();
			inMessage = false;
		} else if (localName.equals("code")) {
			messageBuilder.append("</code>");
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		messageBuilder.append(new String(ch, start, length));

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

	public void setOwner(HTMLW3CCompliance owner) {
		this.owner = owner;

	}

	public void reset() {
		inMessage = false;
		messageBuilder = new StringBuilder();

	}

	public enum MessageType {
		ERROR("error"), INFO("info"), END("end");
		private String value;

		private MessageType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public static MessageType fromString(String string) {
			for (MessageType value : values()) {
				if (value.toString().equals(string)) {
					return value;
				}
			}
			return null;
		}

	}

}
