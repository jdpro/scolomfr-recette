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
package fr.scolomfr.recette.model.sources.manifest;

import java.util.Map;

import com.github.zafarkhaja.semver.Version;

/**
 * Inner memory representation of manifest file if embedded in scolomfr
 * vocabularies delivery
 */
public class Manifest {

	private String version;

	private Version semanticVersion;
	private Map<String, Map<String, String>> content;

	/**
	 * Get version as raw string
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, Map<String, String>> getContent() {
		return content;
	}

	public void setContent(Map<String, Map<String, String>> content) {
		this.content = content;
	}

	/**
	 * Get version as semantic version object
	 * 
	 * @return
	 */
	public Version getSemanticVersion() {
		if (null == semanticVersion) {
			semanticVersion = Version.valueOf(version);
		}
		return semanticVersion;
	}

}
