package fr.scolomfr.recette.controller.advice;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.zafarkhaja.semver.Version;

import fr.scolomfr.recette.model.sources.Catalog;
import fr.scolomfr.recette.model.sources.Manifest;

/**
 * Controller advice to add navbar information (sources structure by version and
 * format)
 */
@ControllerAdvice(basePackages = { "fr.scolomfr.recette.controller" })
public class SourcesStructureControllerAdvice {

	@Autowired
	private Catalog catalog;

	/**
	 * Adds "versions" and "formats" variable to jsp context
	 * 
	 * @param model
	 */
	@ModelAttribute
	public void formatAttributes(Model model) {
		Map<Version, Manifest> manifests = catalog.getManifests();
		List<Version> versions = new LinkedList<>();
		Set<String> formats = new LinkedHashSet<>();
		Iterator<Version> it = manifests.keySet().iterator();
		Manifest manifest;
		while (it.hasNext()) {
			Version version = it.next();
			versions.add(version);
			manifest = manifests.get(version);
			Map<String, Map<String, String>> content = manifest.getContent();
			formats.addAll(content.keySet());
		}
		model.addAttribute("versions", versions);
		model.addAttribute("formats", formats);
	}

}
