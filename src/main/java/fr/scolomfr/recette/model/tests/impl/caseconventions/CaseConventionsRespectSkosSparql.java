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
package fr.scolomfr.recette.model.tests.impl.caseconventions;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.impl.AbstractSparqlTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Two
 */
@TestCaseIndex(index = "s1")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class CaseConventionsRespectSkosSparql extends AbstractSparqlTestCase {

	static final String QUERY_FILE = "conventions_casse.sparql";

	@Override
	public void run() {
		Version version = getVersion();
		String filePath = getFilePath(version, getVocabulary(), "skos");

		String sparql = getSparlsQueryString(QUERY_FILE);
		Dataset dataset = TDBFactory.createDataset(TDB_DIR);
		Model tdb = loadModel(getFileByPath(filePath), dataset);

		QueryExecution queryexecution = queryTDB(sparql, dataset);
		ResultSet execResult = queryexecution.execSelect();

		while (execResult.hasNext()) {
			QuerySolution querySolution = execResult.next();
			System.out.println(querySolution);
		}
		queryexecution.close();
		tdb.close();
		dataset.close();
		result.setState(Result.State.FINAL);
	}

}
