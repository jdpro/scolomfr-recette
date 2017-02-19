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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public abstract class AbstractJenaTestCase extends AbstractTestCase {
	private static final String JENA_FAILURE_PREFIX = "qskos_failure_";

	private static final String JENA_ERROR_PREFIX = "qskos_error_";
	protected static final String SKOS_NARROWER_PROPERTY = "narrower";
	protected static final String SKOS_BROADER_PROPERTY = "broader";
	public static final String SKOS_CORE = "http://www.w3.org/2004/02/skos/core#";
	private Model model;

	protected Model getModel() {
		if (null == model) {
			model = ModelFactory.createDefaultModel();
		}
		return model;
	}

	protected String getErrorCode(String identifier) {
		return new StringBuilder().append(JENA_ERROR_PREFIX).append(MESSAGE_ID_SEPARATOR).append(identifier).toString();
	}

}
