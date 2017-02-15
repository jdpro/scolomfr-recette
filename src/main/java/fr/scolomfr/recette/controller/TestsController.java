/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche2016 Direction du Numérique pour l'Éducation - Ministère de l'Éducation nationale, de l'enseignement supérieur et de la Recherche /
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
package fr.scolomfr.recette.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import fr.scolomfr.recette.tests.execution.Result;
import fr.scolomfr.recette.tests.organization.TestCase;
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
	 * @param folder
	 * @param format
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/tests/{requirement}/{folder}/{format}/{id:.+}", method = RequestMethod.GET)
	public ModelAndView displayTest(HttpServletResponse response, @PathVariable("requirement") String requirement,
			@PathVariable("format") String format, @PathVariable("folder") String folder,
			@PathVariable("id") String id) {
		ModelAndView modelAndView = new ModelAndView("tests");
		Object testCase = testsRepository.getTestCasesRegistry().getTestCase(id);
		if (testCase != null) {
			modelAndView.addObject("implemented", true);
			modelAndView.addObject("implementation", testCase.getClass().getSimpleName());
			modelAndView.addObject("parameters", testCase.getClass().getAnnotation(TestParameters.class) != null
					? (testCase.getClass().getAnnotation(TestParameters.class)).names() : Collections.emptyList());
			modelAndView.addObject("testCaseIndex", id);
			modelAndView.addObject("folderLabel",
					testsRepository.getTestOrganization().getFolderLabel(requirement, folder));
			modelAndView.addObject("testCaseLabel", testsRepository.getTestOrganization().getTestCaseLabel(id));
		} else {
			modelAndView.addObject("implemented", false);
		}

		return modelAndView;
	}

	/**
	 * Launches test execution
	 * 
	 * @param response
	 * @param requirement
	 * @param folder
	 * @param format
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/tests/{requirement}/{folder}/{format}/{id:.+}", method = RequestMethod.POST, produces = "application/xml")
	@ResponseBody
	public ResponseEntity<Result<?>> executeTest(HttpServletResponse response,
			@PathVariable("requirement") String requirement, @PathVariable("format") String format,
			@PathVariable("folder") String folder, @PathVariable("id") String id,
			@RequestParam Map<String, String> executionParameters) {
		return executeTestCase(id, executionParameters);
	}

	@RequestMapping(value = "/tests/exec/{id:.+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Result<?>> executeTest(HttpServletResponse response, @PathVariable("id") String id,
			@RequestParam Map<String, String> executionParameters) {
		return executeTestCase(id, executionParameters);
	}

	private ResponseEntity<Result<?>> executeTestCase(String id, Map<String, String> executionParameters) {
		TestCase testCase = testsRepository.getTestCasesRegistry().getTestCase(id);
		if (testCase == null) {
			Result<?> result = new Result<>();
			result.addError("no_test", "There's no test under identifier " + id);
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
		Result<?> result = testCase.getExecutionResult(executionParameters);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
