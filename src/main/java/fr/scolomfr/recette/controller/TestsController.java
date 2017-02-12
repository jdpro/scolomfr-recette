package fr.scolomfr.recette.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for tests pages
 */
@Controller
public class TestsController {
	/**
	 * Displays tests pages
	 * 
	 * @param response
	 * @param requirement
	 * @param format
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/tests/{requirement}/{format}/{id}")
	public ModelAndView test(HttpServletResponse response, @PathVariable("requirement") String requirement,
			@PathVariable("format") String format, @PathVariable("id") String id) {

		return new ModelAndView("tests");
	}
}
