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
package fr.scolomfr.recette.tests.impl.coherenceinterne.anomalieslibelles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;

import com.github.zafarkhaja.semver.Version;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTagsResult;
import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StreamProgressMonitor;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuildException;
import fr.scolomfr.recette.model.sources.representation.SourceRepresentationBuilder;
import fr.scolomfr.recette.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.tests.execution.result.Message;
import fr.scolomfr.recette.tests.execution.result.Result.State;
import fr.scolomfr.recette.tests.organization.AbstractTestCase;
import fr.scolomfr.recette.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Two
 */
@TestCaseIndex(index = "q1")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class OmittedOrInvalidLanguageTags extends AbstractTestCase {

	@Log
	Logger logger;

	@Autowired
	Catalog catalog;

	@Override
	public void run() {
		String versionStr = executionParameters.get(TestParameters.Values.VERSION);
		Version version;
		try {
			version = Version.valueOf(versionStr);
		} catch (IllegalArgumentException e) {
			logger.error("Le paramètre version {}  est absent ou incorrect", versionStr, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					String.format("Le paramètre version : '%s' est absent ou incorrect", versionStr));
			return;
		}
		String vocabulary = executionParameters.get(TestParameters.Values.VOCABULARY);
		if (StringUtils.isEmpty(vocabulary)) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.TEST_PARAMETERS.toString(), "Version incorrect",
					"Le paramètre vocabulary est absent");
			return;
		}
		String filePath = catalog.getFilePathByVersionFormatAndVocabulary(version, "skos", vocabulary);
		if (null == filePath) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
					"Fichier indisponible",
					String.format("Aucun fichier n'est fourni pour la version %s, le format %s et le vocabulaire %s",
							version, "skos", vocabulary));
			return;
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_AVAILABLE.toString() + filePath,
				"Fichier disponible",
				String.format("Chemin du fichier  pour la version %s, le format %s et le vocabulaire %s : %s", version,
						"skos", vocabulary, filePath));
		File file = catalog.getFileByPath(filePath, ".rdf");
		if (null == file) {
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_OPENED.toString() + filePath,
					"Fichier inaccessible",
					String.format("Impossible d'obtenir le fichier temporaire pour %s", filePath));
			return;
		}
		result.addMessage(Message.Type.INFO, CommonMessageKeys.FILE_OPENED.toString() + filePath, "Fichier ouvert",
				String.format("L'ouverture du fichier %s a réussi", filePath));
		QSkos qSkos = new QSkos();

		RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
		Repository repository;
		try {
			repository = repositoryBuilder.setUpFromFile(file, null, RDFFormat.RDFXML);
		} catch (IOException e) {
			logger.error("Impossible d'ouvrir le fichier temporaire pour {}", filePath, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_OPENED.toString() + filePath, "Fichier e",
					String.format("Impossible d'ouvrir le fichier temporaire pour %s : %s", filePath, e.getMessage()));
			return;
		} catch (OpenRDFException e) {
			logger.error("Le fichier {} n'est pas lisible comme Rdf", filePath, e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.FILE_FORMAT.toString() + filePath,
					"Rdf invalible",
					String.format("Le fichier %s n'est pas lisible comme Rdf: %s", filePath, e.getMessage()));
			return;
		}

		try {
			qSkos.setRepositoryConnection(repository.getConnection());
		} catch (RepositoryException e) {
			logger.error("Problème avec qskos", e);
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.QSKOS_ERROR.toString() + filePath,
					"Rdf invalible", String.format("Problème avec qskos : %s", e.getMessage()));
			return;
		}
		qSkos.setAuthResourceIdentifier("data.education.fr/voc/scolomfr");
		IProgressMonitor streamProgressMonitor = new StreamProgressMonitor();
		qSkos.setProgressMonitor(streamProgressMonitor);
		at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags issue = (at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags) qSkos
				.getIssues("oilt").iterator().next();
		OmittedOrInvalidLanguageTagsResult issueResult = null;
		try {
			issueResult = issue.getResult();
		} catch (OpenRDFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Resource, Collection<Literal>> data = issueResult.getData();
		Iterator<Resource> it = data.keySet().iterator();

		while (it.hasNext()) {
			Resource resource = (Resource) it.next();
			System.out.println("***************************");
			Collection<Literal> literals = data.get(resource);
			StringBuilder sb = new StringBuilder();
			for (Literal literal : literals) {
				sb.append(literal.stringValue());
			}
			result.addMessage(
					new Message(Message.Type.ERROR, resource.stringValue(), "Erreur détectée", sb.toString()));
		}
		result.setState(State.FINAL);
	}

}
