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
package fr.scolomfr.recette.model.tests.organization;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.utils.log.Log;

public abstract class AbstractTestCase implements TestCase {

	@Log
	public Logger logger;

	@Autowired
	public Catalog catalog;

	protected Map<String, String> executionParameters;
	protected Result result = new Result();
	protected Integer executionIdentifier;
	protected TestCaseExecutionRegistry testCaseExecutionRegistry;

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
			logger.error("Le paramètre version {}  est absent ou incorrect", versionStr, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					String.format("Le paramètre version : '%s' est absent ou incorrect", versionStr));
			result.setState(State.FINAL);
			stopTestCase();
		}
		return version;
	}

	protected String getVocabulary() {
		final String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		if (StringUtils.isEmpty(vocabulary)) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					"Le paramètre vocabulary est absent");
			stopTestCase();
		}
		return vocabulary;
	}

	protected String getFilePath(final Version version, final String vocabulary, final String format) {
		final String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, format, vocabulary);
		if (null == filePath) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					"Fichier indisponible",
					String.format("Aucun fichier n'est fourni pour la version %s, le format %s et le vocabulaire %s",
							version, "skos", vocabulary));
			stopTestCase();

		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
				"Fichier disponible",
				String.format("Chemin du fichier  pour la version %s, le format %s et le vocabulaire %s : %s", version,
						"skos", vocabulary, filePath));
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
					"Fichier inaccessible",
					String.format("Impossible d'obtenir le fichier temporaire pour %s", filePath));
			stopTestCase();
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_OPENED.toString() + filePath, "Fichier ouvert",
				String.format("L'ouverture du fichier %s a réussi", filePath));
		return file;
	}

}
