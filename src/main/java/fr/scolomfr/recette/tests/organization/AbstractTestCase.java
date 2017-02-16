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
package fr.scolomfr.recette.tests.organization;

import java.util.Map;

import fr.scolomfr.recette.tests.execution.async.TestCaseExecutionRegistry;
import fr.scolomfr.recette.tests.execution.result.Result;

public abstract class AbstractTestCase implements TestCase {

	protected Map<String, String> executionParameters;
	protected Result result = new Result();
	protected Integer executionIdentifier;
	protected TestCaseExecutionRegistry testCaseExecutionRegistry;

	@Override
	public void setExecutionParameters(Map<String, String> executionParameters) {
		this.executionParameters = executionParameters;
	}

	@Override
	public Result getExecutionResult() {
		return result;
	}

	@Override
	public void setExecutionIdentifier(Integer executionIdentifier) {
		this.executionIdentifier = executionIdentifier;
	}

	@Override
	public void setExecutionRegistry(TestCaseExecutionRegistry testCaseExecutionRegistry) {
		this.testCaseExecutionRegistry = testCaseExecutionRegistry;
	}

}
