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
package fr.scolomfr.recette.model.tests.impl.conservationconcepts;

import java.io.File;
import java.io.IOException;

import org.apache.jena.graph.Graph;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;
import fr.scolomfr.recette.utils.FileUtils;

/**
 * Two
 */
@TestCaseIndex(index = "s2xx")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VERSION2,
		TestParameters.Values.VOCABULARY })
public class ConservationConceptsEntreVersionsSkosSparql extends AbstractJenaTestCase {

	private static final String QUERY_FILE = "classpath:sparql/" + "conservation_concepts_entre_versions.sparql";

	private static final String TDB_DIR = "/tmp/tdb";

	private String grapheNameStub = "http://www.reseau­canope.fr/scolomfr/";

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void run() {
		Version version1 = getVersion();
		String filePath1 = getFilePath(version1, getVocabulary(), "skos");
		Version version2 = getVersion(TestParameters.Values.VERSION2);
		String filePath2 = getFilePath(version2, getVocabulary(), "skos");

		String sparql;
		try {
			sparql = getSparlsQueryString();
		} catch (IOException e) {

			String title = i18n.tr("test.impl.sparql.missing.title");
			String msg = i18n.tr("test.impl.sparql.missing.content", new Object[] { QUERY_FILE });
			result.addMessage(Message.Type.FAILURE, CommonMessageKeys.SPARQL_REQUEST_AVAILABLE.toString(), title, msg);
			result.incrementErrorCount(false);
			logger.error(msg, e);
			return;
		}
		String graph1Name = grapheNameStub + version1;
		String graph2Name = grapheNameStub + version2;
		sparql = sparql.replace("[VERSION1]", graph1Name).replace("[VERSION2]", graph2Name);
		Dataset dataset = TDBFactory.createDataset(TDB_DIR);
		Model tdb1 = loadModel(getFileByPath(filePath1), dataset);
		Model tdb2 = loadModel(getFileByPath(filePath2), dataset);
		dataset.addNamedModel(graph1Name, tdb1);
		dataset.addNamedModel(graph2Name, tdb2);

		ResultSet resultSet = queryTDB(sparql, dataset);
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			System.out.println(querySolution);
		}

		tdb1.close();
		tdb2.close();
		dataset.close();
	}

	private String getSparlsQueryString() throws IOException {
		Resource sparqlFile = resourceLoader.getResource(QUERY_FILE);
		return FileUtils.readResourceAsString(sparqlFile);
	}

	public Model loadModel(File source, Dataset dataset) {
		Model tdb = dataset.getDefaultModel();
		FileManager.get().readModel(tdb, source.getAbsolutePath(), "RDF/XML");
		return tdb;
	}

	public ResultSet queryTDB(String queryStr, Dataset dataset) {

		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = QueryExecutionFactory.create(query, dataset);
		return qexec.execSelect();

	}
}
