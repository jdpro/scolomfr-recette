package fr.scolomfr.recette.model.sources;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

/**
 * Converts manifest from any file format to inner memory representation
 */
@Component
public interface ManifestParser {
	/**
	 * Retrieve manifest object when it's built
	 * 
	 * @return
	 */
	Manifest getManifest();

	/**
	 * Load file data into parser for processing
	 * 
	 * @param manifestInputStream
	 * @return
	 * @throws IOException
	 */
	ManifestParser load(InputStream manifestInputStream) throws IOException;

}
