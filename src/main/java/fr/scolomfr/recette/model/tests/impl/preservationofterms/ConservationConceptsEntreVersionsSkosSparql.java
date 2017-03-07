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
package fr.scolomfr.recette.model.tests.impl.preservationofterms;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.impl.AbstractSparqlTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Two
 */
@TestCaseIndex(index = "s2xx")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VERSION2,
		TestParameters.Values.VOCABULARY, TestParameters.Values.SKOSTYPE })
public class ConservationConceptsEntreVersionsSkosSparql extends AbstractSparqlTestCase {

	static final String QUERY_FILE = "conservation_concepts_entre_versions.sparql";

	private String grapheNameStub = "http://www.reseau­canope.fr/scolomfr/";

	@Override
	public void run() {

		Version version1 = getVersion();
		String format = getSkosType();
		String filePath1 = getFilePath(version1, getVocabulary(), format);
		Version version2 = getVersion(TestParameters.Values.VERSION2);
		String filePath2 = getFilePath(version2, getVocabulary(), format);

		String sparql;

		sparql = getSparlsQueryString(QUERY_FILE);

		String graph1Name = grapheNameStub + version1;
		String graph2Name = grapheNameStub + version2;
		sparql = sparql.replace("[VERSION1]", graph1Name).replace("[VERSION2]", graph2Name);
		Dataset dataset = TDBFactory.createDataset(TDB_DIR);
		Model tdb1 = loadModel(getFileByPath(filePath1), dataset);
		Model tdb2 = loadModel(getFileByPath(filePath2), dataset);
		dataset.addNamedModel(graph1Name, tdb1);
		dataset.addNamedModel(graph2Name, tdb2);

		QueryExecution queryexecution = queryTDB(sparql, dataset);
		ResultSet execResult = queryexecution.execSelect();

		while (execResult.hasNext()) {
			QuerySolution querySolution = execResult.next();
			System.out.println(querySolution);
		}
		queryexecution.close();
		tdb1.close();
		tdb2.close();
		dataset.close();
	}
}
