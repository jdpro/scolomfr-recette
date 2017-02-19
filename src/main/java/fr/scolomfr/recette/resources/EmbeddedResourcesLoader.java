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
package fr.scolomfr.recette.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.utils.log.Log;

/**
 * Handles files and directories from classpath
 */
@Component
public class EmbeddedResourcesLoader implements ResourcesLoader {

	@Log
	Logger logger;

	/**
	 * Loads resource file from classpath
	 * 
	 * @param path
	 *            Relative to classpath root
	 * @return
	 */
	@Override
	public InputStream loadResource(final String path) {
		return EmbeddedResourcesLoader.class.getResourceAsStream(path);
	}

	/**
	 * Loads directory information from classpath
	 * 
	 * @param filePath
	 *            Relative to classpath root
	 * @return
	 * @throws IOException
	 */
	@Override
	public DirectoryStream<Path> loadDirectory(final String filePath) throws IOException {
		URL url = EmbeddedResourcesLoader.class.getResource(filePath);
		try {
			Path path = Paths.get(url.toURI());
			return Files.newDirectoryStream(path);
		} catch (URISyntaxException e) {
			logger.error("Impossible to parse URI {}", url, e);
		}
		return null;
	}
}
