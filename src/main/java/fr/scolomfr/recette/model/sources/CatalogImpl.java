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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.config.ContextParameters;
import fr.scolomfr.recette.model.sources.manifest.Manifest;
import fr.scolomfr.recette.model.sources.manifest.ManifestParser;
import fr.scolomfr.recette.resources.EmbeddedResourcesLoader;
import fr.scolomfr.recette.resources.FileSystemResourcesLoader;
import fr.scolomfr.recette.resources.ResourcesLoader;
import fr.scolomfr.recette.utils.log.Log;

/**
 * @see Catalog
 */
@Component
public class CatalogImpl implements Catalog {
	public static final String CLASSPATH_VOCABULARIES_DIRECTORY = "/scolomfr";
	private static final String MANIFEST_FILE_NAME = "manifest.yml";

	@Log
	Logger logger;

	@Autowired
	ContextParameters contextParameters;

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
				newManifest.setFolder(scolomfrFolder.getFileName().toString());
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
	public List<Pair<Version, Pair<String, String>>> getFilePathsByFormat(String requestedFormat) {
		List<Pair<Version, Pair<String, String>>> filesByFormat = new ArrayList<>();
		Iterator<Version> it = getManifests().keySet().iterator();
		while (it.hasNext()) {
			Version version = it.next();
			Manifest manifest = getManifests().get(version);
			String folder = manifest.getFolder();
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
					Pair<String, String> vocabularyPair = Pair.of(vocabulary,
							folder + "/" + vocabularies.get(vocabulary));
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
	public List<Pair<String, Pair<String, String>>> getFilePathsByVersion(Version requestedVersion) {
		List<Pair<String, Pair<String, String>>> filesByVersion = new ArrayList<>();
		Manifest manifest = getManifests().get(requestedVersion);
		if (null != manifest) {
			String folder = manifest.getFolder();
			Map<String, Map<String, String>> content = manifest.getContent();
			Iterator<String> it = content.keySet().iterator();
			while (it.hasNext()) {
				String format = it.next();
				Map<String, String> vocabularies = content.get(format);
				Iterator<String> it2 = vocabularies.keySet().iterator();
				while (it2.hasNext()) {
					String vocabulary = it2.next();
					Pair<String, String> vocabularyPair = Pair.of(vocabulary,
							folder + "/" + vocabularies.get(vocabulary));
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
		return contextParameters.get(ContextParameters.Keys.SCOLOMFR_FILES_DIRECTORY_ENV_VAR_NAME);
	}

	private ResourcesLoader getResourcesLoader() {
		if (StringUtils.isEmpty(getScolomfrFilesDirectoryFromContext())) {
			return embeddedResourcesLoader;
		}
		return fileSystemResourcesLoader;

	}

	@Override
	public InputStream getFileInputStreamByPath(String filePath) {
		String completePath = MessageFormat.format("{0}/{1}", this.getVocabulariesDirectory(), filePath);
		return getResourcesLoader().loadResource(completePath);
	}

	@Override
	public File getFileByPath(String filePath, String suffix) {
		if (null == filePath) {
			return null;
		}
		String prefix = "recette_tmp_";
		InputStream in = getFileInputStreamByPath(filePath);
		if (null == in) {
			return null;
		}
		File tempFile = null;
		try {
			tempFile = File.createTempFile(prefix, suffix);
			tempFile.deleteOnExit();
			try (OutputStream out = new FileOutputStream(tempFile)) {
				IOUtils.copy(in, out);
			}
		} catch (IOException e) {
			logger.error("Impossible to create temp file for filepath {}", filePath, e);
		}
		return tempFile;

	}

	@Override
	public String getFilePathByVersionFormatAndVocabulary(Version version, String format, String vocabulary) {
		Manifest manifest = getManifests().get(version);
		String filePath = null;
		String folder;
		if (null != manifest) {
			folder = manifest.getFolder();
			Map<String, Map<String, String>> content = manifest.getContent();
			Map<String, String> vocabularies = content.get(format);
			if (null != vocabularies) {
				if (org.apache.commons.lang3.StringUtils.isEmpty(vocabularies.get(vocabulary))) {
					return null;
				}
				filePath = folder + "/" + vocabularies.get(vocabulary);
			}
		}
		return filePath;
	}

	@Override
	public Map<String, String> getFilePathsByVersionAndFormat(Version version, String format) {
		Manifest manifest = getManifests().get(version);
		Map<String, String> filePaths = new LinkedHashMap<>();
		String folder;
		if (null != manifest) {
			folder = manifest.getFolder();
			Map<String, Map<String, String>> content = manifest.getContent();
			Map<String, String> vocabularies = content.get(format);
			Iterator<String> it = vocabularies.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				filePaths.put(key, folder + "/" + vocabularies.get(key));
			}

		}
		return filePaths;
	}

	@Override
	public String getDtddirByVersionAndFormat(Version version, String format) {
		Manifest manifest = getManifests().get(version);
		if (null != manifest) {
			Map<String, String> dtddirs = manifest.getDtddir();
			if (null != dtddirs) {
				return getVocabulariesDirectory() + "/" + manifest.getFolder() + "/" + dtddirs.get(format);
			}

		}
		return null;
	}

}
