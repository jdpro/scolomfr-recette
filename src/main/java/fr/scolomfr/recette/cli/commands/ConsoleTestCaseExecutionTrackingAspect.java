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
package fr.scolomfr.recette.cli.commands;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTracker;
import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTrackingAspect;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl.State;

@Aspect
@Component
@Profile({ "!web" })
public class ConsoleTestCaseExecutionTrackingAspect implements TestCaseExecutionTrackingAspect {
	private TestCaseExecutionTracker tracker;

	@After("execution(* fr.scolomfr.recette.model.tests.execution.result.Result.addMessage(fr.scolomfr.recette.model.tests.execution.result.Message)) && args(message,..)")
	public void messageAdded(Message message) {
		tracker.notify(message);
	}

	@Around("execution(* fr.scolomfr.recette.model.tests.execution.result.Result.setState(..))")
	public void messageAdded(ProceedingJoinPoint joinPoint) {
		State state = (State) joinPoint.getArgs()[0];
		if (state.equals(State.FINAL)) {
			tracker.notifyTestCaseTermination((Result) joinPoint.getTarget());
		}
	}

	@Override
	public void setOwner(TestCaseExecutionTracker tracker) {
		this.tracker = tracker;
	}
}
