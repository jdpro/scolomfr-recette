/*
 * 
 */
package fr.scolomfr.recette.cli.commands;

import org.springframework.context.annotation.Profile;
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
@Profile({ "!web" })
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
		buf.append("Version:" + this.getVersion() + OsUtils.LINE_SEPARATOR);
		buf.append(
				"Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche");
		return buf.toString();
	}

	public String getVersion() {
		return "0.0.1";
	}

	public String getWelcomeMessage() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Metadata vocabulary quality tool for french learning resources.");
		stringBuilder.append(OsUtils.LINE_SEPARATOR);
		stringBuilder.append("Type a command, 'help' or 'help + command'");
		return stringBuilder.toString();
	}

	@Override
	public String getProviderName() {
		return "Scolomfr recette banner";
	}
}