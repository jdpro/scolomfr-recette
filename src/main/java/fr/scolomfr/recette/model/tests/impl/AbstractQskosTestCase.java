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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.representation.utils.QskosException;
import fr.scolomfr.recette.model.sources.representation.utils.QskosEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;

public abstract class AbstractQskosTestCase<T> extends AbstractTestCase {

	private static final String QSKOS_FAILURE_PREFIX = "qskos_failure_";

	private static final String QSKOS_ERROR_PREFIX = "qskos_error_";

	@Autowired
	QskosEngine qskosEngine;

	@Override
	public void run() {
		Version version = getVersion();
		final String vocabulary = getVocabulary();
		qskosEngine.notifyTestCase(this);
		String format = getSkosType();
		final String filePath = getFilePath(version, vocabulary, format);

		if (StringUtils.isEmpty(filePath)) {
			// TODO i18n message
			result.addMessage(new Message(Message.Type.FAILURE,
					QSKOS_FAILURE_PREFIX + version + MESSAGE_ID_SEPARATOR + vocabulary + MESSAGE_ID_SEPARATOR + format,
					"File unavailable", "File unavailable"));
		}

		final File file = getFileByPath(filePath);

		T data = null;
		try {
			qskosEngine.setFile(file).setIssueCode(getQskosIssueCode());
			result.addMessage(new Message(Message.Type.INFO, "qskos_ml_launched_" + filePath,
					i18n.tr("tests.impl.qskos.launched.title"), i18n.tr("tests.impl.qskos.launched.content")));
			data = qskosEngine.buildResultData();

		} catch (QskosException e) {
			logger.error("Problem with skos : {}", e.getMessage(), e);
			result.addMessage(new Message(Message.Type.FAILURE,
					QSKOS_FAILURE_PREFIX + getQskosIssueCode() + MESSAGE_ID_SEPARATOR + filePath,
					i18n.tr("tests.impl.qskos.failure.title"), e.getMessage()));

		}

		populateResult(data);

		result.setState(State.FINAL);
	}

	protected String generateUniqueErrorCode(String identifier) {
		return new StringBuilder().append(QSKOS_ERROR_PREFIX).append(getQskosIssueCode()).append(MESSAGE_ID_SEPARATOR)
				.append(identifier).toString();
	}

	protected abstract void populateResult(T data);

	protected abstract String getQskosIssueCode();

}
