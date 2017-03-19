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
package fr.scolomfr.recette.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.shell.Bootstrap;

/**
 * Driver class to run the helloworld example.
 * 
 * @author Mark Pollack
 *
 */
public class Main {

	/**
	 * Main class that delegates to Spring Shell's Bootstrap class in order to
	 * simplify debugging inside an IDE
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
		argsList.add("--disableInternalCommands");
		String[] argsArray = new String[argsList.size()];
		argsArray = argsList.toArray(argsArray);
		Bootstrap.main(argsArray);

	}

}
