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
package fr.scolomfr.recette.model.tests.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Message.Type;
import fr.scolomfr.recette.model.tests.execution.result.Result;

public class ResultTestHelper {

	public static void assertContainsMessage(Result result, Type type, String[] inTitle, String[] inContent) {
		boolean foundInResult = false;
		for (Message message : result.getMessages()) {
			boolean foundInMessage = true;
			if (message.getType().equals(type)) {
				for (String string : inContent) {
					if (message.getContent().indexOf(string) < 0) {
						foundInMessage = false;
					}
				}
				for (String string : inTitle) {
					if (message.getTitle().indexOf(string) < 0) {
						foundInMessage = false;
					}
				}
				if (foundInMessage) {
					foundInResult = true;
				}
			}
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("A message Of type ");
		stringBuilder.append(type);
		boolean titleCondition = (null != inTitle && inTitle.length > 0);
		boolean contentCondition = (null != inContent && inContent.length > 0);
		if (titleCondition) {
			stringBuilder.append(" containing ");
			stringBuilder.append(Arrays.toString(inTitle));
			stringBuilder.append(" in title ");
		}
		if (titleCondition && contentCondition) {
			stringBuilder.append(" and ");
		}
		if (contentCondition) {
			stringBuilder.append(" containing ");
			stringBuilder.append(Arrays.toString(inContent));
			stringBuilder.append(" in content should have been found");
		}
		assertTrue(stringBuilder.toString(), foundInResult);

	}

}
