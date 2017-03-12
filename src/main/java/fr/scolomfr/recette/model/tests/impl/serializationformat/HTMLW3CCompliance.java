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
package fr.scolomfr.recette.model.tests.impl.serializationformat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.impl.serializationformat.CustomNuContentHandler.MessageType;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import nu.validator.messages.MessageEmitter;
import nu.validator.messages.MessageEmitterAdapter;
import nu.validator.messages.XmlMessageEmitter;
import nu.validator.servlet.imagereview.ImageCollector;
import nu.validator.source.SourceCode;
import nu.validator.validation.SimpleDocumentValidator;
import nu.validator.xml.SystemErrErrorHandler;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.skosintegrity.HierarchicalRedundancy
 */
@TestCaseIndex(index = "a13")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class HTMLW3CCompliance extends AbstractTestCase {

	/**
	 * @see https://github.com/validator/validator/tree/master/schema/.drivers
	 */
	private static final String TYPE_TO_VALIDATE_AGAINST = "http://s.validator.nu/html5.rnc";
	@Autowired
	CustomNuContentHandler customNuContentHandler;
	private String filePath;

	@Override
	public void run() {
		Version version = getVersion();
		String vocabulary = getVocabulary();
		if (null == version || StringUtils.isEmpty(vocabulary)) {
			return;
		}
		Map<String, String> htmlFilePaths = null;
		if (vocabulary.equals(GLOBAL_VOCABULARY)) {
			htmlFilePaths = getFilePathsForAllVocabularies(version, "html");
		} else {
			htmlFilePaths = new HashMap<>();
			String singleHTMLFilePath = getFilePath(version, vocabulary, "html");
			if (null == singleHTMLFilePath) {
				return;
			}
			htmlFilePaths.put(vocabulary, singleHTMLFilePath);
		}
		if (MapUtils.isEmpty(htmlFilePaths)) {
			return;
		}
		Iterator<String> it = htmlFilePaths.keySet().iterator();
		while (it.hasNext()) {
			filePath = htmlFilePaths.get(it.next());
			validateWithNu();
		}

	}

	private void validateWithNu() {
		InputStream in = getFileInputStreamByPath(filePath);
		SourceCode sourceCode = new SourceCode();
		ImageCollector imageCollector = new ImageCollector(sourceCode);
		boolean showSource = false;
		customNuContentHandler.reset();
		customNuContentHandler.setOwner(this);
		MessageEmitter emitter = new XmlMessageEmitter(customNuContentHandler);
		MessageEmitterAdapter errorHandler = new MessageEmitterAdapter(sourceCode, showSource, imageCollector, 0, false,
				emitter);
		errorHandler.setErrorsOnly(false);
		errorHandler.setHtml(true);
		SimpleDocumentValidator validator = new SimpleDocumentValidator();
		try {
			validator.setUpMainSchema(TYPE_TO_VALIDATE_AGAINST, new SystemErrErrorHandler());
			validator.setUpValidatorAndParsers(errorHandler, true, false);
			validator.checkHtmlInputSource(new InputSource(in));
			errorHandler.end("Document checking completed. No errors found.", "Document checking completed.");
		} catch (Exception e) {
			String messageStr = i18n.tr("tests.impl.a13.result.failure.content", new Object[] { e.getMessage() });
			Message message = new Message(Message.Type.FAILURE,
					CommonMessageKeys.NO_EXECUTION.name() + MESSAGE_ID_SEPARATOR + filePath,
					i18n.tr("tests.impl.a13.result.failure.title"), messageStr);
			logger.error(messageStr, e);
			result.addMessage(message);
			stopTestCase();
			return;
		}

	}

	public void submitMessage(MessageType messageType, String messageStr) {
		Message message;
		String errorCode = null;
		try {
			errorCode = generateUniqueErrorCode(filePath + MESSAGE_ID_SEPARATOR + DigestUtils.md5Hex(messageStr));
		} catch (DuplicateErrorCodeException e2) {
			logger.trace(ERROR_CODE_DUPLICATE, errorCode, e2);
			if (messageType.equals(MessageType.ERROR)) {
				return;
			}
		}
		switch (messageType) {
		case ERROR:
			boolean ignored = errorIsIgnored(errorCode);
			result.incrementErrorCount(ignored);
			message = new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode,
					i18n.tr("tests.impl.a13.result.error.title"),
					i18n.tr("tests.impl.a13.result.error.content", new Object[] { filePath, messageStr }));
			result.addMessage(message);
			break;
		case INFO:
			message = new Message(Message.Type.INFO, errorCode, i18n.tr("tests.impl.a13.result.warning.title"),
					i18n.tr("tests.impl.a13.result.warning.content", new Object[] { filePath, messageStr }));
			result.addMessage(message);
			break;
		case END:

			result.setState(State.FINAL);
			break;

		default:
			break;
		}

	}

}
