package fr.scolomfr.recette.model.sources;

import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.github.zafarkhaja.semver.Version;

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
	List<Pair<Version, Pair<String, String>>> getFilesByFormat(String criterium);

	/**
	 * Get list of files by version (in any format)
	 * 
	 * @param version
	 * @return
	 */
	List<Pair<String, Pair<String, String>>> getFilesByVersion(Version version);

}
