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

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.scolomfr.recette.model.tests.execution.ignore.IgnoreRequestResult;
import fr.scolomfr.recette.model.tests.persistence.PersistenceManager;
import fr.scolomfr.recette.utils.log.Log;

/**
 * Controller for tests pages
 */
@Controller
@Profile("web")
public class TestsIgnoreController {

	@Log
	Logger logger;

	@Autowired
	PersistenceManager persistenceManager;

	@RequestMapping(value = "/tests/ignore-false-positive", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<IgnoreRequestResult> ignoreFalsePositive(HttpServletResponse response,
			@RequestParam("key") String key) {
		IgnoreRequestResult result = new IgnoreRequestResult();
		if (persistenceManager.hasKey(key)) {
			result.setState(IgnoreRequestResult.State.DUPLICATE);
		} else {
			result.setState(IgnoreRequestResult.State.ACCEPTED);
			persistenceManager.setKey(key, "IGNORE");
		}
		result.setKey(key);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/tests/restore-true-positive", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<IgnoreRequestResult> restoreTruePositive(HttpServletResponse response,
			@RequestParam("key") String key) {
		IgnoreRequestResult result = new IgnoreRequestResult();
		if (persistenceManager.hasKey(key)) {
			persistenceManager.delete(key);
			result.setState(IgnoreRequestResult.State.ACCEPTED);
		} else {
			result.setState(IgnoreRequestResult.State.NOT_FOUND);

		}
		result.setKey(key);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
