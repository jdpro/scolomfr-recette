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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import fr.scolomfr.recette.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.tests.execution.result.AsyncResult;
import fr.scolomfr.recette.tests.execution.result.CommonMessageKeys;
import fr.scolomfr.recette.tests.execution.result.Message;
import fr.scolomfr.recette.tests.execution.result.Result;
import fr.scolomfr.recette.tests.organization.TestCase;
import fr.scolomfr.recette.tests.organization.TestParameters;
import fr.scolomfr.recette.tests.organization.TestsRepository;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Controller for tests pages
 */
@Controller
public class TestsController {

	@Log
	Logger logger;

	@Autowired
	TestsRepository testsRepository;

	@Autowired
	TestCaseExecutionRegistry testCaseExecutionRegistry;

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
	@RequestMapping(value = { "/tests/{requirement}/{format}/{id:.+}" }, method = RequestMethod.GET)
	public ModelAndView displayTest(HttpServletResponse response, @PathVariable("requirement") String requirement,
			@PathVariable("format") String format, @PathVariable("id") String id) {
		ModelAndView modelAndView = new ModelAndView("tests");
		Object testCase = testsRepository.getTestCasesRegistry().getTestCase(id);
		if (testCase != null) {
			modelAndView.addObject("implemented", true);
			modelAndView.addObject("implementation", testCase.getClass().getSimpleName());
			modelAndView.addObject("parameters", testCase.getClass().getAnnotation(TestParameters.class) != null
					? (testCase.getClass().getAnnotation(TestParameters.class)).names() : Collections.emptyList());
			modelAndView.addObject("testCaseIndex", id);
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
	@RequestMapping(value = { "/tests/{requirement}/{format}/{id:.+}" }, method = RequestMethod.POST, produces = {
			"application/xml", MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<AsyncResult> executeTest(HttpServletResponse response, HttpServletRequest request,
			@PathVariable("requirement") String requirement, @PathVariable("format") String format,
			@PathVariable("id") String id, @RequestParam Map<String, String> executionParameters) {
		return executeTestCaseAsync(id, executionParameters, request);
	}

	@RequestMapping(value = "/tests/exec/{id:.+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Result> executeTest(HttpServletResponse response, @PathVariable("id") String id,
			@RequestParam Map<String, String> executionParameters) {
		try {
			return executeTestCaseSync(id, executionParameters);
		} catch (CloneNotSupportedException e) {
			logger.error("Should not happen", e);
		}
		return null;
	}

	private ResponseEntity<AsyncResult> executeTestCaseAsync(String id, Map<String, String> executionParameters,
			HttpServletRequest request) {
		TestCase testCase = testsRepository.getTestCasesRegistry().getTestCase(id);
		if (testCase == null) {
			AsyncResult result = new AsyncResult();
			result.setStatus(AsyncResult.Status.MISSING);
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
		testCase.reset();
		testCase.setExecutionParameters(executionParameters);
		Integer executionIdentifier = testCaseExecutionRegistry.newTestCaseExecution(testCase);
		AsyncResult result = new AsyncResult();
		result.setStatus(AsyncResult.Status.INITIATED);

		String executionTrackingUri = getExecutionTrackingUri(executionIdentifier, request.getRequestURL().toString());
		result.setUri(executionTrackingUri);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private String getExecutionTrackingUri(Integer executionIdentifier, String urlStr) {
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			logger.error("Problem with urrent URI : {}", urlStr, e);
			return null;
		}
		String scheme = url.getProtocol();
		String host = url.getHost();
		String path = url.getPath().split("/test")[0];
		int port = url.getPort();
		String portPart = "";
		if (port != 80 && port != 443) {
			portPart = ":" + port;
		}
		return scheme + "://" + host + portPart + path + "/tests/async/" + executionIdentifier;
	}

	private ResponseEntity<Result> executeTestCaseSync(String id, Map<String, String> executionParameters)
			throws CloneNotSupportedException {
		TestCase testCase = testsRepository.getTestCasesRegistry().getTestCase(id);
		if (testCase == null) {
			Result result = new Result();
			result.addMessage(new Message(Message.Type.FAILURE, CommonMessageKeys.NO_TEST.toString(),
					"No such test case", "There's no test under identifier " + id));
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
		testCase.setExecutionParameters(executionParameters);
		testCase.run();
		return new ResponseEntity<>(testCase.getExecutionResult(), HttpStatus.OK);
	}

	@RequestMapping(value = "/tests/async/{id:[0-9]+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Result> trackTestExecution(HttpServletResponse response,
			@PathVariable("id") Integer executionIdentifier) {
		TestCase testCase = testCaseExecutionRegistry.getTestCase(executionIdentifier);
		Result result;
		if (testCase == null) {
			result = new Result();
			result.addMessage(new Message(Message.Type.FAILURE, CommonMessageKeys.NO_EXECUTION.toString(),
					"No execution running", "There's no execution under identifier " + executionIdentifier));
			return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(testCase.temporaryResult(), HttpStatus.OK);
	}
}
