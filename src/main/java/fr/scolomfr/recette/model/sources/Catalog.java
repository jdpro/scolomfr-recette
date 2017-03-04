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
package fr.scolomfr.recette.model.sources;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.manifest.Manifest;

/**
 * Keeps track of all files loaded from directory structure
 */
@Component
public interface Catalog {
	/**
	 * Get manifest objects by version (reflect manifest.yml files)
	 * 
	 * @return
	 */
	Map<Version, Manifest> getManifests();

	/**
	 * Get list of files by format (from any versions)
	 * 
	 * @param criterium
	 * @return
	 */
	List<Pair<Version, Pair<String, String>>> getFilePathsByFormat(String criterium);

	/**
	 * Get list of files by version (in any format)
	 * 
	 * @param version
	 * @return
	 */
	List<Pair<String, Pair<String, String>>> getFilePathsByVersion(Version version);

	/**
	 * Return a path to the directory containing scolomfr vocabularies, either
	 * absolute or relative to classpath
	 * 
	 * @return
	 */
	String getVocabulariesDirectory();

	/**
	 * Opens the file from vocabularies directory
	 * 
	 * @param filePath
	 * @return
	 */
	InputStream getFileInputStreamByPath(String filePath);

	/**
	 * Returns temp file instead of stream
	 * 
	 * @see Catalog#getFileInputStreamByPath(String)
	 * @param filePath
	 * @param suffix
	 * @return
	 */
	File getFileByPath(String filePath, String suffix);

	/**
	 * 
	 * @param version
	 * @param format
	 * @param vocabulary
	 * @return
	 */
	String getFilePathByVersionFormatAndVocabulary(Version version, String format, String vocabulary);

	/**
	 * 
	 * @param version
	 * @param vocabulary
	 * @return
	 */
	Map<String, String> getFilePathsByVersionAndFormat(Version version, String format);

	/**
	 * 
	 * @param version
	 * @param format
	 * @return
	 */
	String getDtddirByVersionAndFormat(Version version, String format);
}
