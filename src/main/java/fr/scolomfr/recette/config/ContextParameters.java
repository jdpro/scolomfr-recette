/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  MENESR (DNE), J.Dornbusch
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
package fr.scolomfr.recette.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.utils.log.Log;

@Component
public class ContextParameters {

	@Log
	Logger logger;

	public String get(final Keys parameterName) {
		InitialContext initialContext;
		try {
			initialContext = new javax.naming.InitialContext();
			return (String) initialContext.lookup("java:comp/env/" + parameterName.toString());
		} catch (NamingException e) {
			logger.trace("Unable to get {} from initial context", parameterName, e);
		}
		return "";
	}

	public String getFromCliProperties(final Keys parameterName) {
		return System.getProperty(parameterName.toString());
	}

	public enum Keys {
		SCOLOMFR_DEFAULT_VERSION_ENV_VAR_NAME("scolomfr_default_version"), SCOLOMFR_FILES_DIRECTORY_ENV_VAR_NAME(
				"scolomfr_files_directory");
		private String value;

		private Keys(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

}
