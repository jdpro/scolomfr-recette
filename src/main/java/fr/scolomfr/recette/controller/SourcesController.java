package fr.scolomfr.recette.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Controller for sources pages
 */
@Controller
public class SourcesController {

	@Autowired
	private Catalog catalog;

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
		List<?> lines = Collections.emptyList();
		switch (by) {
		case "format":
			lines = getFileFromProvidedFormatString(criterium);
			break;
		case "version":
			lines = getFilesFromProvidedVersionString(criterium);
			break;
		default:
			modelAndView.addObject("lines", lines);
			break;
		}
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
}
