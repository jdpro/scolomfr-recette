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
package fr.scolomfr.recette.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.resources.EmbeddedResourcesLoader;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Controller for sources pages
 */
@Controller
public class SourcesController {

	@Autowired
	private Catalog catalog;

	@Autowired
	private EmbeddedResourcesLoader resourcesLoader;

	@Log
	Logger logger;

	/**
	 * Displays vocabularies files by version or format
	 * 
	 * @param response
	 * @param by
	 *            'format' or 'version'
	 * @param criterium
	 *            The requested format or version. Version should be formatted
	 *            in the semantic way (x.y.z)
	 * @return
	 */
	@RequestMapping(value = "/sources/{by}/{criterium}")
	public ModelAndView sources(HttpServletResponse response, @PathVariable("by") String by,
			@PathVariable("criterium") String criterium) {
		ModelAndView modelAndView = new ModelAndView("sources");
		List<String> headers = new ArrayList<>();
		List<?> lines = Collections.emptyList();
		switch (by) {
		case "format":
			headers.addAll(Arrays.asList("Version", "Vocabulaire", "Fichier"));
			lines = getFileFromProvidedFormatString(criterium);
			break;
		case "version":
			headers.addAll(Arrays.asList("Format", "Vocabulaire", "Fichier"));
			lines = getFilesFromProvidedVersionString(criterium);
			break;
		default:
			// Nothing to do
			break;
		}
		modelAndView.addObject("by", by);
		modelAndView.addObject("criterium", criterium);
		modelAndView.addObject("headers", headers);
		modelAndView.addObject("lines", lines);
		return modelAndView;
	}

	private List<?> getFileFromProvidedFormatString(String criterium) {
		return catalog.getFilesByFormat(criterium);
	}

	private List<?> getFilesFromProvidedVersionString(String criterium) {
		List<?> lines = Collections.emptyList();
		try {
			Version requestedVersion = Version.valueOf(criterium);
			lines = catalog.getFilesByVersion(requestedVersion);
		} catch (Exception e) {
			logger.error("Impossible to parse string {} as version identifier", criterium, e);
		}
		return lines;
	}

	/**
	 * Dispaly raw source file in browser
	 * 
	 * @param response
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value = "/display/raw/**")
	public void getFile(HttpServletResponse response, HttpServletRequest request) {
		// Trick to catch path parameter containing forward slashes
		String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String fileName = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
		logger.info("Request to display resource file {}", fileName);
		try (InputStream inputStream = resourcesLoader
				.loadResource(catalog.getVocabulariesDirectory() + "/" + fileName)) {
			if (null == inputStream) {
				logger.info("Resource file {} not found", fileName);
			} else {
				IOUtils.copy(inputStream, response.getOutputStream());
				response.flushBuffer();
			}

		} catch (IOException e) {
			logger.info("Unable to display resource file {}", fileName, e);
		}

	}
}
