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
		Set<String> vocabularies = new LinkedHashSet<>();
		Iterator<Version> it = manifests.keySet().iterator();
		Manifest manifest;
		while (it.hasNext()) {
			Version version = it.next();
			versions.add(version);
			manifest = manifests.get(version);
			Map<String, Map<String, String>> content = manifest.getContent();
			formats.addAll(content.keySet());
			Iterator<String> it2 = content.keySet().iterator();
			while (it2.hasNext()) {
				String format = it2.next();
				vocabularies.addAll(content.get(format).keySet());
			}
		}
		model.addAttribute("versions", versions);
		model.addAttribute("formats", formats);
		model.addAttribute("vocabularies", vocabularies);
	}

}
