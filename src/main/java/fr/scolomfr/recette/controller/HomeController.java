package fr.scolomfr.recette.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for home page
 */
@Controller
public class HomeController {

	/**
	 * Display home page
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/")
	public ModelAndView home(HttpServletResponse response) throws IOException {
		return new ModelAndView("home");
	}
}
