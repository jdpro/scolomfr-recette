package fr.scolomfr.recette.model.sources;

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
@Scope("application")
public class ResourcesLoader {

	@Log
	Logger logger;

	/**
	 * Loads resource file from classpath
	 * 
	 * @param path
	 *            Relative to classpath root
	 * @return
	 */
	public InputStream loadResource(final String path) {
		return ResourcesLoader.class.getResourceAsStream(path);
	}

	/**
	 * Loads directory information from classpath
	 * 
	 * @param filePath
	 *            Relative to classpath root
	 * @return
	 * @throws IOException
	 */
	public DirectoryStream<Path> loadDirectory(final String filePath) throws IOException {
		URL url = ResourcesLoader.class.getResource(filePath);
		try {
			Path path = Paths.get(url.toURI());
			return Files.newDirectoryStream(path);
		} catch (URISyntaxException e) {
			logger.error("Impossible to parse URI {}", url, e);
		}
		return null;
	}
}
