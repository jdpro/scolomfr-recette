/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.scolomfr.recette.cli.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

/**
 * @author Jarred Li
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MyBannerProvider extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append("  _____           _      ____  __  __ ______ _____  " + OsUtils.LINE_SEPARATOR);
		buf.append(" / ____|         | |    / __ \\|  \\/  |  ____|  __ \\ " + OsUtils.LINE_SEPARATOR);
		buf.append("| (___   ___ ___ | |   | |  | | \\  / | |__  | |__) |" + OsUtils.LINE_SEPARATOR);
		buf.append(" \\___ \\ / __/ _ \\| |   | |  | | |\\/| |  __| |  _  / " + OsUtils.LINE_SEPARATOR);
		buf.append(" ____) | (_| (_) | |___| |__| | |  | | |    | | \\ \\ " + OsUtils.LINE_SEPARATOR);
		buf.append("|_____/ \\___\\___/|______\\____/|_|  |_|_|    |_|  \\_\\" + OsUtils.LINE_SEPARATOR);
		buf.append("                      | | | |                           " + OsUtils.LINE_SEPARATOR);
		buf.append("     _ __ ___  ___ ___| |_| |_ ___                      " + OsUtils.LINE_SEPARATOR);
		buf.append("    | '__/ _ \\/ __/ _ \\ __| __/ _ \\                     " + OsUtils.LINE_SEPARATOR);
		buf.append("    | | |  __/ (_|  __/ |_| ||  __/                     " + OsUtils.LINE_SEPARATOR);
		buf.append("    |_|  \\___|\\___\\___|\\__|\\__\\___|                     " + OsUtils.LINE_SEPARATOR);

		buf.append("========================================================" + OsUtils.LINE_SEPARATOR);
		buf.append("Version:" + this.getVersion());
		return buf.toString();
	}

	public String getVersion() {
		return "1.2.3";
	}

	public String getWelcomeMessage() {
		return "Outils de test vocabulaires scolomfr";
	}

	@Override
	public String getProviderName() {
		return "Scolomfr recette banner";
	}
}