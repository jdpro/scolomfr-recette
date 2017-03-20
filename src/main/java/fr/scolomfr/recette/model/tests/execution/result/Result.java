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

import fr.scolomfr.recette.model.tests.execution.result.Message.Type;
import fr.scolomfr.recette.model.tests.execution.result.ResultImpl.State;

public interface Result {

	void setComplianceIndicator(float f);

	void setState(State state);

	void incrementErrorCount(boolean ignored);

	void addMessage(Message message);

	Deque<Message> getMessages();

	float getComplianceIndicator();

	int getFalsePositiveCount();

	int getErrorCount();

	State getState();

	void addMessage(Type type, String key, String title, String content);

	void setFalsePositiveCount(int falsePositiveCount);

	void reset();

}
