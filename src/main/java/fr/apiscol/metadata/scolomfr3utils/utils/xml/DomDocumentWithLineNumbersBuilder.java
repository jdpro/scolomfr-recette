package fr.apiscol.metadata.scolomfr3utils.utils.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Deque;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@Component
public class DomDocumentWithLineNumbersBuilder {

	public static final String LINE_NUMBER_KEY = "LINE_NUMBER_KEY";
	private static DomDocumentWithLineNumbersBuilder instance;

	private DomDocumentWithLineNumbersBuilder() {
		// prevent instanciation from outside
	}

	/**
	 * Build a document from xml file and adds line numbers as user data from
	 * https://eyalsch.wordpress.com/2010/11/30/xml-dom-2/
	 * 
	 * @param dtdDirectory
	 * 
	 * @param xmlFile
	 *            The xml file to load
	 * @return {@link Document} A document with line number information as user
	 *         data
	 * @throws IOException
	 *             If file is unreachable
	 * @throws SAXException
	 *             If file is impossible to parse
	 * @throws ParserConfigurationException
	 *             Cannot happen
	 */
	public Document parse(InputStream is, String dtdDirectory)
			throws IOException, SAXException, ParserConfigurationException {
		final Document doc;
		SAXParser parser;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		System.setProperty("user.dir", dtdDirectory);
		XMLReader reader = parser.getXMLReader();
		reader.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				System.out.println(">>>>>>>>>>>>>>>>>>" + publicId + " " + systemId);
				return new InputSource(new StringReader(""));
			}
		});
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		doc = docBuilder.newDocument();

		final Deque<Element> elementStack = new LinkedList<>();
		final StringBuilder textBuffer = new StringBuilder();
		DefaultHandler handler = new DefaultHandler() {
			private Locator locator;

			@Override
			public void setDocumentLocator(Locator locator) {
				this.locator = locator; // Save the locator, so that it can be
										// used later for line tracking when
										// traversing nodes.
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				addTextIfNeeded();
				Element el = doc.createElement(qName);
				for (int i = 0; i < attributes.getLength(); i++)
					el.setAttribute(attributes.getQName(i), attributes.getValue(i));
				el.setUserData(LINE_NUMBER_KEY, String.valueOf(this.locator.getLineNumber()), null);
				elementStack.push(el);
			}

			@Override
			public void endElement(String uri, String localName, String qName) {
				addTextIfNeeded();
				Element closedEl = elementStack.pop();
				if (elementStack.isEmpty()) { // Is this the root element?
					doc.appendChild(closedEl);
				} else {
					Element parentEl = elementStack.peek();
					parentEl.appendChild(closedEl);
				}
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				textBuffer.append(ch, start, length);
			}

			// Outputs text accumulated under the current node
			private void addTextIfNeeded() {
				if (textBuffer.length() > 0) {
					Element el = elementStack.peek();
					Node textNode = doc.createTextNode(textBuffer.toString());
					el.appendChild(textNode);
					textBuffer.delete(0, textBuffer.length());
				}
			}
		};
		parser.parse(is, handler);

		return doc;
	}

	public static DomDocumentWithLineNumbersBuilder getInstance() {
		if (instance == null) {
			instance = new DomDocumentWithLineNumbersBuilder();
		}
		return instance;
	}

}
