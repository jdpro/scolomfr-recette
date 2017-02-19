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
package fr.scolomfr.recette.model.sources.representation.utils;

import java.io.File;
import java.io.IOException;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StreamProgressMonitor;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import fr.scolomfr.recette.utils.log.Log;

@Component
public class QskosResultBuilder {
	@Log
	Logger logger;

	File file;

	private String issueCode;

	public <T> T build() throws QskosException {
		if (file == null) {
			throw new QskosException("Please provide a file to analyse.");
		}
		if (StringUtils.isEmpty(issueCode)) {
			throw new QskosException("Please provide the issue to look for.");
		}
		String filePath = file.getAbsolutePath();
		final QSkos qSkos = new QSkos();

		final RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
		Repository repository = null;
		try {
			repository = repositoryBuilder.setUpFromFile(file, null, RDFFormat.RDFXML);
		} catch (final IOException e) {
			throw new QskosException(
					String.format("Impossible d'ouvrir le fichier temporaire pour %s : %s", filePath, e.getMessage()),
					e);
		} catch (final OpenRDFException e) {
			throw new QskosException(
					String.format("Le fichier %s n'est pas lisible comme Rdf: %s", filePath, e.getMessage()), e);
		}

		try {
			qSkos.setRepositoryConnection(repository.getConnection());
		} catch (final RepositoryException e) {
			throw new QskosException(String.format("Problème avec qskos : %s", e.getMessage()), e);
		}
		qSkos.setAuthResourceIdentifier("data.education.fr/voc/scolomfr");
		final IProgressMonitor streamProgressMonitor = new StreamProgressMonitor();
		qSkos.setProgressMonitor(streamProgressMonitor);
		@SuppressWarnings("unchecked")
		final Issue<Result<T>> issue = qSkos.getIssues(issueCode).iterator().next();
		try {
			return issue.getResult().getData();
		} catch (OpenRDFException e) {
			throw new QskosException(String.format("Problème qSkos lecture RDF : %s", e.getMessage()), e);
		}
	}

	public QskosResultBuilder setFile(File file) {
		this.file = file;
		return this;
	}

	public QskosResultBuilder setIssueCode(String issueCode) {
		this.issueCode = issueCode;
		return this;
	}
}
