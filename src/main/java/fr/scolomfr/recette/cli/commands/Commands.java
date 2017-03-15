package fr.scolomfr.recette.cli.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.model.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.model.tests.organization.TestCasesRepository;

/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017 MENESR (DNE), J.Dornbusch
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
@Component
public class Commands implements CommandMarker {
	// @Log
	// Logger logger;

	@Autowired
	TestCasesRepository testsRepository;
	// //
	@Autowired
	TestCaseExecutionRegistry testCaseExecutionRegistry;

	private boolean simpleCommandExecuted = false;

	@CliAvailabilityIndicator({ "hw simple" })
	public boolean isSimpleAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "hw complex", "hw enum" })
	public boolean isComplexAvailable() {
		if (simpleCommandExecuted) {
			return true;
		} else {
			return false;
		}
	}

	@CliCommand(value = "hw simple", help = "Print a simple hello world message")
	public String simple(
			@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
			@CliOption(key = {
					"location" }, mandatory = false, help = "Where you are saying hello", specifiedDefaultValue = "At work") final String location) {
		simpleCommandExecuted = true;
		return "Message = [" + message + "] Location = [" + location + "]";
	}

	@CliCommand(value = "hw complex", help = "Print a complex hello world message")
	public String hello(
			@CliOption(key = { "message" }, mandatory = true, help = "The hello world message") final String message,
			@CliOption(key = { "name1" }, mandatory = true, help = "Say hello to the first name") final String name1,
			@CliOption(key = { "name2" }, mandatory = true, help = "Say hello to a second name") final String name2,
			@CliOption(key = {
					"time" }, mandatory = false, specifiedDefaultValue = "now", help = "When you are saying hello") final String time,
			@CliOption(key = {
					"location" }, mandatory = false, help = "Where you are saying hello") final String location) {
		return "Hello " + name1 + " and " + name2 + ". Your special message is " + message + ". time=[" + time
				+ "] location=[" + location + "]";
	}

	@CliCommand(value = "hw enum", help = "Print a simple hello world message from an enumerated value")
	public String eenum(@CliOption(key = {
			"message" }, mandatory = true, help = "The hello world message") final MessageType message) {
		return "Hello.  Your special enumerated message is " + message;
	}

	enum MessageType {
		Type1("type1"), Type2("type2"), Type3("type3");

		private String type;

		private MessageType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
}
