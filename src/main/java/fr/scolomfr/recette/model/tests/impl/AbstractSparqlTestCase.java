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
package fr.scolomfr.recette.model.tests.impl;

import java.io.File;
import java.io.IOException;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.utils.FileUtils;

public abstract class AbstractSparqlTestCase extends AbstractJenaTestCase {

	protected static final String TDB_DIR = "/tmp/tdb";
	@Autowired
	ResourceLoader resourceLoader;

	protected String getSparlsQueryString(String sparqlQueryFileRelativePath) {
		Resource sparqlFile = resourceLoader.getResource("classpath:sparql/" + sparqlQueryFileRelativePath);
		try {
			return FileUtils.readResourceAsString(sparqlFile);
		} catch (IOException e) {
			final String title = i18n.tr("test.impl.sparql.missing.title");
			final String msg = i18n.tr("test.impl.sparql.missing.content",
					new Object[] { sparqlQueryFileRelativePath });
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.SPARQL_REQUEST_AVAILABLE.toString(), title, msg);
			result.incrementErrorCount(false);
			logger.error(msg, e);
			stopTestCase();
		}
		return null;
	}

	public Model loadModel(File source, Dataset dataset) {
		Model tdb = dataset.getDefaultModel();
		FileManager.get().readModel(tdb, source.getAbsolutePath(), "RDF/XML");
		return tdb;
	}

	public QueryExecution queryTDB(String queryStr, Dataset dataset) {
		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		return qexec;

	}

}
