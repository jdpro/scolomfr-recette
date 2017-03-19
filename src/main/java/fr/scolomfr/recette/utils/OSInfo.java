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
package fr.scolomfr.recette.utils;

import java.io.IOException;
import java.util.Locale;

public class OSInfo {

	public enum OS {
		WINDOWS, UNIX, POSIX_UNIX, MAC, OTHER;

		private String version;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	private static OS os = OS.OTHER;

	static {
		try {
			String osName = System.getProperty("os.name");
			if (osName == null) {
				throw new IOException("os.name not found");
			}
			osName = osName.toLowerCase(Locale.ENGLISH);
			if (osName.contains("windows")) {
				os = OS.WINDOWS;
			} else if (osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd")
					|| osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix")) {
				os = OS.UNIX;
			} else if (osName.contains("mac os")) {
				os = OS.MAC;
			} else if (osName.contains("sun os") || osName.contains("sunos") || osName.contains("solaris")) {
				os = OS.POSIX_UNIX;
			} else if (osName.contains("hp-ux") || osName.contains("aix")) {
				os = OS.POSIX_UNIX;
			} else {
				os = OS.OTHER;
			}

		} catch (Exception ex) {
			os = OS.OTHER;
		} finally {
			os.setVersion(System.getProperty("os.version"));
		}
	}

	public static OS getOs() {
		return os;
	}
}
