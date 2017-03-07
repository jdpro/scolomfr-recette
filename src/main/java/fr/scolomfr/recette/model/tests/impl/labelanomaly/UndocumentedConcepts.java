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
package fr.scolomfr.recette.model.tests.impl.labelanomaly;

import fr.scolomfr.recette.model.tests.impl.AbstractCollectionResourceQskosTestCase;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;

/**
 * @see at.ac.univie.mminf.qskos4j.issues.concepts.UndocumentedConcepts
 */
@TestCaseIndex(index = "q4")
public class UndocumentedConcepts extends AbstractCollectionResourceQskosTestCase {

	@Override
	protected String getQskosIssueCode() {
		return "uc";
	}

}
