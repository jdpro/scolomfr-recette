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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
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

	protected static final String MESSAGE_ID_SEPARATOR = "_";

	@Log
	public Logger logger;

	@Autowired
	public Catalog catalog;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

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
		Result temporaryResult = new Result();
		temporaryResult.setState(this.result.getState());
		temporaryResult.setErrorCount(this.result.getErrorCount());
		temporaryResult.setComplianceIndicator(this.result.getComplianceIndicator());
		while (!this.result.getMessages().isEmpty()) {
			temporaryResult.addMessage(this.result.getMessages().pop());
		}
		return temporaryResult;
	}

	@Override
	public void reset() {
		this.executionParameters = null;
		this.result = new Result();
		this.executionIdentifier = null;
	}

	protected Version getVersion() {
		final String versionStr = executionParameters.get(TestParameters.Values.VERSION);
		Version version = null;
		try {
			version = Version.valueOf(versionStr);
		} catch (final IllegalArgumentException e) {
			String title = i18n.tr("test.impl.version.parameter.missing.title");
			String msg = i18n.tr("test.impl.version.parameter.missing.content", new Object[] { versionStr });
			logger.error(msg, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString() + "version", title,
					msg);
			result.incrementErrorCount();
			result.setState(State.FINAL);
			stopTestCase();
		}
		return version;
	}

	protected String getVocabulary() {
		final String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		if (StringUtils.isEmpty(vocabulary)) {
			String title = i18n.tr("test.impl.vocabulary.parameter.missing.title");
			String msg = i18n.tr("test.impl.vocabulary.parameter.missing.content");
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString() + "vocabulary", title,
					msg);
			result.incrementErrorCount();
			stopTestCase();
		}
		return vocabulary;
	}

	protected String getFilePath(final Version version, final String vocabulary, final String format) {
		final String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, format, vocabulary);
		if (null == filePath) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					i18n.tr("test.impl.file.unavailable.title"),
					i18n.tr("test.impl.file.unavailable.content", new Object[] { version, format, vocabulary }));
			result.incrementErrorCount();
			stopTestCase();

		} else {
			result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					i18n.tr("test.impl.file.available.title"), i18n.tr("test.impl.file.available.content",
							new Object[] { version, format, vocabulary, filePath }));
		}

		return filePath;
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
			result.incrementErrorCount();
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
			result.incrementErrorCount();
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
		String status = stringRedisTemplate.opsForValue().get(key);
		return null != status && status.equals("IGNORE");
	}

}
