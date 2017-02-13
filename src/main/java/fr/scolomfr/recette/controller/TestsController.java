/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE)2016 Direction du Numérique pour l'Éducation - Ministère de l'Éducation nationale, de l'enseignement supérieur et de la Recherche /
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

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.tests.organization.TestsRepository;

/**
 * Controller for tests pages
 */
@Controller
public class TestsController {

	@Autowired
	TestsRepository testsRepository;

	/**
	 * Displays tests pages
	 * 
	 * @param response
	 * @param requirement
	 * @param format
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/tests/{requirement}/{folder}/{format}/{id}")
	public ModelAndView test(HttpServletResponse response, @PathVariable("requirement") String requirement,
			@PathVariable("format") String format, @PathVariable("folder") String folder,
			@PathVariable("id") String id) {
		ModelAndView modelAndView = new ModelAndView("tests");
		Object testUnit = testsRepository.getTestCasesRegistry().getTestUnit(id);
		if (testUnit != null) {
			modelAndView.addObject("implemented", true);
			modelAndView.addObject("implementation", testUnit.getClass().getSimpleName());
			modelAndView.addObject("parameters",
					((TestParameters) testUnit.getClass().getAnnotation(TestParameters.class)) != null
							? (testUnit.getClass().getAnnotation(TestParameters.class)).names()
							: Collections.emptyList());
		} else {
			modelAndView.addObject("implemented", false);
		}

		return modelAndView;
	}
}
