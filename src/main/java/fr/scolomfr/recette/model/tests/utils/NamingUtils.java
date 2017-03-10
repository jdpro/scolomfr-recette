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
package fr.scolomfr.recette.model.tests.utils;

import java.text.MessageFormat;

public class NamingUtils {
	private static final String VOCAB_URI_PATTERN = "http://data.education.fr/voc/scolomfr/scolomfr-voc-{0}";

	private NamingUtils() {
		// no instanciation
	}

	public static String getVocabURI(final String vocabNumber) {
		return MessageFormat.format(VOCAB_URI_PATTERN, vocabNumber);
	}

}
