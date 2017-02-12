package fr.scolomfr.recette.model.sources;

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
