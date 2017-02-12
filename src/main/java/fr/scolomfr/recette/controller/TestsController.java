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
