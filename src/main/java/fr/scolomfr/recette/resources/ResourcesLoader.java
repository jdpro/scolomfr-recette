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
package fr.scolomfr.recette.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

/**
 * Utility classes to load files either from classpath (if embedded) either from
 * file system
 */
public interface ResourcesLoader {

	/**
	 * Get file as stream
	 * 
	 * @param path
	 * @return
	 */
	InputStream loadResource(String path);

	/**
	 * Get directory as {@link Path}
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	DirectoryStream<Path> loadDirectory(String filePath) throws IOException;

}
