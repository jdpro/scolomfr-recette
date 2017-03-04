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
package fr.scolomfr.recette.model.tests.organization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide the list of parameters required by the test. They will
 * be taken into account in the view to build a form
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface TestParameters {
	/**
	 * The list of parameters names
	 * 
	 * @return
	 */
	public String[] names();

	public class Values {
		public static final String FORMAT = "format";
		public static final String VERSION = "version";
		public static final String VOCABULARY = "vocabulary";
		public static final String FORMAT2 = "format2";
		public static final String VERSION2 = "version2";
		public static final String VOCABULARY2 = "vocabulary2";
		public static final String SKOSTYPE = "skostype";
		public static final String GLOBAL = "global";

		private Values() {
		}
	}
}
