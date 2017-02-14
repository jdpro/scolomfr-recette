/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE)
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.resources.EmbeddedResourcesLoader;
import fr.scolomfr.recette.resources.FileSystemResourcesLoader;
import fr.scolomfr.recette.resources.ResourcesLoader;
import fr.scolomfr.recette.utils.log.Log;

/**
 * @see Catalog
 */
@Component
public class CatalogImpl implements Catalog {
	private static final String SCOLOMFR_FILES_DIRECTORY_ENV_VAR_NAME = "scolomfr_files_directory";
	public static final String CLASSPATH_VOCABULARIES_DIRECTORY = "/scolomfr";
	private static final String MANIFEST_FILE_NAME = "manifest.yml";

	@Log
	Logger logger;

	@Autowired
	EmbeddedResourcesLoader embeddedResourcesLoader;

	@Autowired
	FileSystemResourcesLoader fileSystemResourcesLoader;

	@Autowired
	ManifestParser manifestParser;

	private SortedMap<Version, Manifest> manifests = new TreeMap<>();

	@Override
	public Map<Version, Manifest> getManifests() {
		return manifests;
	}

	/**
	 * Loads manifests from scolomfr directories at server startup
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void init() throws IOException {
		ResourcesLoader resourcesLoader = getResourcesLoader();

		logger.info("Looking for scolomfr packages in folder " + getVocabulariesDirectory());

		DirectoryStream<Path> scolomfrFolders = resourcesLoader.loadDirectory(getVocabulariesDirectory());

		Manifest newManifest;
		for (Path path : scolomfrFolders) {

			Path scolomfrFolder = path.getFileName();
			String manifestPath = String.format("%s/%s/%s", getVocabulariesDirectory(), scolomfrFolder,
					MANIFEST_FILE_NAME);
			logger.info("Looking for manifest in folder " + manifestPath);

			try (InputStream manifestInputStream = resourcesLoader.loadResource(manifestPath)) {
				if (null == manifestInputStream) {
					logger.warn("No manifest file " + manifestPath + ", skipping.");
					continue;
				}
				logger.info("Manifest found, parsing data from " + manifestPath);
				newManifest = manifestParser.load(manifestInputStream).getManifest();
				logger.info("Version declared in manifest is " + newManifest.getSemanticVersion());
				manifests.put(newManifest.getSemanticVersion(), newManifest);
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.scolomfr.recette.model.sources.Catalog#getFilesByFormat(java.lang.
	 * String)
	 */
	@Override
	public List<Pair<Version, Pair<String, String>>> getFilesByFormat(String requestedFormat) {
		List<Pair<Version, Pair<String, String>>> filesByFormat = new ArrayList<>();
		Iterator<Version> it = getManifests().keySet().iterator();
		while (it.hasNext()) {
			Version version = it.next();
			Manifest manifest = getManifests().get(version);
			Map<String, Map<String, String>> content = manifest.getContent();
			Iterator<String> it2 = content.keySet().iterator();
			while (it2.hasNext()) {
				String format = it2.next();
				if (!requestedFormat.equals(format)) {
					continue;
				}
				Map<String, String> vocabularies = content.get(format);
				Iterator<String> it3 = vocabularies.keySet().iterator();
				while (it3.hasNext()) {
					String vocabulary = it3.next();
					Pair<String, String> vocabularyPair = Pair.of(vocabulary, vocabularies.get(vocabulary));
					Pair<Version, Pair<String, String>> entryPair = Pair.of(version, vocabularyPair);
					filesByFormat.add(entryPair);
				}

			}

		}

		return filesByFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.scolomfr.recette.model.sources.Catalog#getFilesByVersion(com.github.
	 * zafarkhaja.semver.Version)
	 */
	@Override
	public List<Pair<String, Pair<String, String>>> getFilesByVersion(Version requestedVersion) {
		List<Pair<String, Pair<String, String>>> filesByVersion = new ArrayList<>();
		Manifest manifest = getManifests().get(requestedVersion);
		if (null != manifest) {
			Map<String, Map<String, String>> content = manifest.getContent();
			Iterator<String> it = content.keySet().iterator();
			while (it.hasNext()) {
				String format = it.next();
				Map<String, String> vocabularies = content.get(format);
				Iterator<String> it2 = vocabularies.keySet().iterator();
				while (it2.hasNext()) {
					String vocabulary = it2.next();
					Pair<String, String> vocabularyPair = Pair.of(vocabulary, vocabularies.get(vocabulary));
					Pair<String, Pair<String, String>> entryPair = Pair.of(format, vocabularyPair);
					filesByVersion.add(entryPair);
				}

			}
		}
		return filesByVersion;
	}

	@Override
	public String getVocabulariesDirectory() {
		String scolomfrFilesDirectoryFromContext = getScolomfrFilesDirectoryFromContext();
		logger.info("Scolomfr file directory fetched from xml context : {}", scolomfrFilesDirectoryFromContext);
		if (StringUtils.isEmpty(scolomfrFilesDirectoryFromContext)) {
			logger.info("Using memory classpath instead");
			return CLASSPATH_VOCABULARIES_DIRECTORY;
		}
		return scolomfrFilesDirectoryFromContext;

	}

	private String getScolomfrFilesDirectoryFromContext() {
		InitialContext initialContext;
		try {
			initialContext = new javax.naming.InitialContext();
			return (String) initialContext.lookup("java:comp/env/" + SCOLOMFR_FILES_DIRECTORY_ENV_VAR_NAME);
		} catch (NamingException e) {
			logger.error("Unable to get {} from initial context", SCOLOMFR_FILES_DIRECTORY_ENV_VAR_NAME, e);
		}
		return "";
	}

	private ResourcesLoader getResourcesLoader() {
		if (StringUtils.isEmpty(getScolomfrFilesDirectoryFromContext())) {
			return embeddedResourcesLoader;
		}
		return fileSystemResourcesLoader;

	}

}
