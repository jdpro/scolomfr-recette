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
package fr.scolomfr.recette.model.tests.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.config.ContextParameters;
import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuildException;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuilder;
import fr.scolomfr.recette.model.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.organization.TestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.i18n.I18nProvider;
import fr.scolomfr.recette.utils.log.Log;

public abstract class AbstractTestCase implements TestCase {

	private static final String SKOSXL = "skosxl";

	private static final String SKOS = "skos";

	private static final String DEFAULT_SKOSTYPE = SKOSXL;

	protected static final String MESSAGE_ID_SEPARATOR = "_";

	protected static final String GLOBAL_VOCABULARY = "global";

	protected static final String ERROR_CODE_DUPLICATE = "Errorcode {} generated twice";

	private ExecutionMode executionMode = ExecutionMode.SYNCHRONOUS;

	@Log
	protected Logger logger;

	@Autowired
	protected Catalog catalog;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	ContextParameters contextParameters;

	List<String> errorCodes = new ArrayList<>();

	protected Map<String, String> executionParameters;
	protected Result result = new Result();
	protected Integer executionIdentifier;
	protected TestCaseExecutionRegistry testCaseExecutionRegistry;

	@Autowired
	protected I18nProvider i18n;

	@Override
	public void setExecutionParameters(Map<String, String> executionParameters) {
		this.executionParameters = executionParameters;
	}

	@Override
	public Result getExecutionResult() {
		return result;
	}

	@Override
	public void setExecutionIdentifier(Integer executionIdentifier) {
		this.executionIdentifier = executionIdentifier;
	}

	@Override
	public void setExecutionRegistry(TestCaseExecutionRegistry testCaseExecutionRegistry) {
		this.testCaseExecutionRegistry = testCaseExecutionRegistry;
	}

	protected String getIndex() {
		for (Annotation annotation : this.getClass().getDeclaredAnnotations()) {
			if (annotation.annotationType().equals(TestCaseIndex.class)) {
				return ((TestCaseIndex) annotation).index();
			}
		}
		return null;
	}

	@Override
	public Result temporaryResult() {
		synchronized (this.result) {
			Result temporaryResult = new Result();
			temporaryResult.setState(this.result.getState());
			temporaryResult.setErrorCount(this.result.getErrorCount());
			temporaryResult.setFalsePositiveCount(this.result.getFalsePositiveCount());
			temporaryResult.setComplianceIndicator(this.result.getComplianceIndicator());
			while (!this.result.getMessages().isEmpty()) {
				temporaryResult.addMessage(this.result.getMessages().pop());
			}
			if (temporaryResult.getState().equals(Result.State.FINAL)) {
				this.testCaseExecutionRegistry.markForFutureDeletion(executionIdentifier);
			}
			return temporaryResult;
		}

	}

	@Override
	public void reset() {
		this.executionParameters = null;
		this.result = new Result();
		this.executionIdentifier = null;
		this.errorCodes = new ArrayList<>();
		this.executionMode = ExecutionMode.SYNCHRONOUS;
	}

	protected Version getVersion() {
		return getVersion(TestParameters.Values.VERSION);
	}

	protected boolean useGlobalVocabulary() {
		String globalOrSpecialStr = executionParameters.get(TestParameters.Values.GLOBAL);
		if (StringUtils.isEmpty(globalOrSpecialStr)) {
			return true;
		}
		return globalOrSpecialStr.equals(GLOBAL_VOCABULARY);
	}

	protected Version getVersion(String versionParameter) {
		String versionStr = executionParameters.get(versionParameter);
		if (StringUtils.isEmpty(versionStr)) {
			versionStr = contextParameters.get(ContextParameters.Keys.SCOLOMFR_DEFAULT_VERSION_ENV_VAR_NAME);
		}
		Version version = null;
		try {
			version = Version.valueOf(versionStr);
		} catch (final IllegalArgumentException e) {
			String title = i18n.tr("test.impl.version.parameter.missing.title");
			String msg = i18n.tr("test.impl.version.parameter.missing.content", new Object[] { versionStr });
			logger.error(msg, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString() + "version", title,
					msg);
			result.incrementErrorCount(false);
			result.setState(State.FINAL);
			stopTestCase();
		}
		return version;
	}

	protected String getVocabulary() {
		String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		if (StringUtils.isEmpty(vocabulary)) {
			vocabulary = GLOBAL_VOCABULARY;
		}
		// TODO dead code
		if (StringUtils.isEmpty(vocabulary)) {
			String title = i18n.tr("test.impl.vocabulary.parameter.missing.title");
			String msg = i18n.tr("test.impl.vocabulary.parameter.missing.content");
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString() + "vocabulary", title,
					msg);
			result.incrementErrorCount(false);
			stopTestCase();
		}
		return vocabulary;
	}

	protected String getSkosType() {
		String skosType = executionParameters.get(TestParameters.Values.SKOSTYPE);
		String[] allowedSkosTypes = new String[] { SKOS, SKOSXL };
		if (StringUtils.isEmpty(skosType)) {
			skosType = DEFAULT_SKOSTYPE;
		}
		if (!ArrayUtils.contains(allowedSkosTypes, skosType)) {
			String title = i18n.tr("test.impl.skostype.parameter.invalid.title");
			String msg = i18n.tr("test.impl.skostype.parameter.invalid.content",
					new Object[] { StringUtils.isEmpty(skosType) ? "<empty>" : skosType });
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString() + "parameter", title,
					msg);
			result.incrementErrorCount(false);
			stopTestCase();
		}
		return skosType;
	}

	protected String getFilePath(final Version version, final String vocabulary, final String format) {
		final String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, format, vocabulary);
		if (StringUtils.isEmpty(filePath)) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					i18n.tr("test.impl.file.unavailable.title"),
					i18n.tr("test.impl.file.unavailable.content", new Object[] { version, format, vocabulary }));
			result.incrementErrorCount(false);
			stopTestCase();

		} else {
			result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					i18n.tr("test.impl.file.available.title"), i18n.tr("test.impl.file.available.content",
							new Object[] { version, format, vocabulary, filePath }));
		}

		return filePath;
	}

	protected Map<String, String> getFilePathsForAllVocabularies(final Version version, final String format) {
		final Map<String, String> filePaths = catalog.getFilePathsByVersionAndFormat(version, format);
		if (CollectionUtils.isEmpty(filePaths.keySet())) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + version + format,
					i18n.tr("test.impl.files.unavailable.title"),
					i18n.tr("test.impl.files.unavailable.content", new Object[] { version, format }));
			result.incrementErrorCount(false);
			stopTestCase();

		} else {
			result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePaths.toString(),
					i18n.tr("test.impl.files.available.title"), i18n.tr("test.impl.files.available.content",
							new Object[] { version, format, filePaths.size(), filePaths.values().toString() }));
		}

		return filePaths;
	}

	protected void stopTestCase() {
		result.setState(State.FINAL);
		Thread.currentThread().interrupt();
	}

	protected File getFileByPath(final String filePath) {
		final File file = catalog.getFileByPath(filePath, ".rdf");

		if (null == file) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					i18n.tr("test.impl.tempfile.unavailable.title"),
					i18n.tr("test.impl.tempfile.unavailable.content", new Object[] { filePath }));
			result.incrementErrorCount(false);
			stopTestCase();
		} else {
			result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					i18n.tr("test.impl.tempfile.available.title"),
					i18n.tr("test.impl.tempfile.available.content", new Object[] { filePath }));
		}
		return file;
	}

	protected InputStream getFileInputStreamByPath(final String filePath) {
		final InputStream fileInputStream = catalog.getFileInputStreamByPath(filePath);
		if (null == fileInputStream) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					i18n.tr("test.impl.file.unreadable.title"),
					i18n.tr("test.impl.file.unreadable.content", new Object[] { filePath }));
			result.incrementErrorCount(false);
			stopTestCase();
		} else {
			result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					i18n.tr("test.impl.file.readable.title"),
					i18n.tr("test.impl.file.readable.content", new Object[] { filePath }));
		}
		return fileInputStream;
	}

	protected void refreshComplianceIndicator(Result result, int numerator, int denominator) {
		if (denominator != 0) {
			result.setComplianceIndicator((float) numerator / (float) denominator);
		}

	}

	protected boolean errorIsIgnored(String key) {
		if (null == key) {
			return false;
		}
		String status = stringRedisTemplate.opsForValue().get(key);
		return null != status && status.equals("IGNORE");
	}

	protected String generateUniqueErrorCode(String identifier) throws DuplicateErrorCodeException {
		TestCaseIndex annotation = this.getClass().getAnnotation(TestCaseIndex.class);
		String annotationValue = "";
		if (null != annotation) {
			annotationValue = annotation.index();
		}
		String code = new StringBuilder().append(annotationValue).append(MESSAGE_ID_SEPARATOR).append(identifier)
				.toString();
		if (errorCodes.contains(code)) {
			throw new DuplicateErrorCodeException(
					"Duplicate error code " + code + "for test " + annotationValue + " and identifier " + identifier);
		}
		errorCodes.add(code);
		return code;
	}

	protected Document getDomDocument(final Version version, final String vocabulary, final String format,
			String dtdDirectory) {
		final String filePath = getFilePath(version, vocabulary, format);
		if (null == filePath) {
			return null;
		}
		return getDomDocument(filePath, dtdDirectory);
	}

	protected Document getDomDocument(String filePath, String dtdDirectory) {
		Document document;
		try {
			final InputStream fileInputStream = getFileInputStreamByPath(filePath);
			SourceRepresentationBuilder<Document> sourceRepresentationBuilder = new SourceRepresentationBuilder<Document>(
					Document.class);
			sourceRepresentationBuilder.setWithLineNumbers(true);
			if (!org.apache.commons.lang3.StringUtils.isEmpty(dtdDirectory)) {
				sourceRepresentationBuilder.setDtdDirectory(dtdDirectory);
			}
			document = sourceRepresentationBuilder.inputStream(fileInputStream).build();
		} catch (final SourceRepresentationBuildException e) {
			launchUnreadableError(filePath, "xml", e);
			return null;
		}
		String title = i18n.tr("test.impl.xml.readable.title");
		String content = i18n.tr("test.impl.xml.readable.content", new Object[] { filePath });
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_FORMAT.toString() + filePath, title, content);
		return document;
	}

	protected org.jsoup.nodes.Document getJsoupDocument(String filePath) {
		org.jsoup.nodes.Document document;
		try {
			final InputStream fileInputStream = getFileInputStreamByPath(filePath);

			document = Jsoup.parse(fileInputStream, null, filePath);
		} catch (final IOException e) {
			launchUnreadableError(filePath, "html", e);
			return null;
		}
		String title = i18n.tr("test.impl.html.readable.title");
		String content = i18n.tr("test.impl.html.readable.content", new Object[] { filePath });
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_FORMAT.toString() + filePath, title, content);
		return document;
	}

	private void launchUnreadableError(String filePath, String format, final Exception e) {
		String title = i18n.tr("test.impl." + format + ".unreadable.title");
		String content = i18n.tr("test.impl." + format + ".unreadable.content",
				new Object[] { filePath, e.getMessage() });
		logger.error(content, e);
		result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_FORMAT.toString() + filePath, title, content);
		this.result.incrementErrorCount(false);
	}

	@Override
	public void progressionMessage(String info, float progressionRate) {
		if (getExecutionMode() != ExecutionMode.ASYNCHRONOUS) {
			return;
		}
		Message message = new Message(Message.Type.PROGRESS, UUID.randomUUID().toString(), info,
				Float.toString(Math.min(progressionRate, 100)));
		result.addMessage(message);

	}

	@Override
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	@Override
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	/**
	 * Execution modes for a test case
	 */
	public enum ExecutionMode {
		SYNCHRONOUS, ASYNCHRONOUS;
	}

}
