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
package fr.scolomfr.recette.model.tests.execution.result;

import java.util.Deque;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.scolomfr.recette.model.tests.execution.TestCaseExecutionTracker;
import fr.scolomfr.recette.model.tests.execution.result.Message.Type;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl.State;

@Component
@Scope("prototype")
public class ResultAopDecorator implements Result {

	private Result result;

	public ResultAopDecorator() {
		reset();
	}

	public Result getResult() {
		return result;
	}

	public void reset() {
		this.result = new ResultImpl();
	}

	@Override
	public void setTestCaseExecutionTracker(TestCaseExecutionTracker testCaseExecutionTracker) {
		result.setTestCaseExecutionTracker(testCaseExecutionTracker);
	}

	@Override
	public State getState() {
		return this.result.getState();
	}

	@Override
	public int getErrorCount() {
		return this.result.getErrorCount();
	}

	@Override
	public int getFalsePositiveCount() {
		return this.result.getFalsePositiveCount();
	}

	@Override
	public float getComplianceIndicator() {
		return this.result.getComplianceIndicator();
	}

	@Override
	public Deque<Message> getMessages() {
		return this.result.getMessages();
	}

	@Override
	public void addMessage(Message message) {
		this.result.addMessage(message);

	}

	@Override
	public void incrementErrorCount(boolean ignored) {
		result.incrementErrorCount(ignored);

	}

	@Override
	public void setState(State state) {
		result.setState(state);

	}

	@Override
	public void setComplianceIndicator(float f) {
		result.setComplianceIndicator(f);
	}

	@Override
	public void addMessage(Type type, String key, String title, String content) {
		result.addMessage(type, key, title, content);
	}

	@Override
	public void setFalsePositiveCount(int falsePositiveCount) {
		result.setFalsePositiveCount(falsePositiveCount);

	}

}
