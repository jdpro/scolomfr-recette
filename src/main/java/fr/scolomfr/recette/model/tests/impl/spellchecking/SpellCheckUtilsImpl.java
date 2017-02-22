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
package fr.scolomfr.recette.model.tests.impl.spellchecking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class SpellCheckUtilsImpl implements SpellCheckUtils {

	private static Pattern abbreviationsPattern = Pattern
			.compile("^([A-Za-zàèéêîôûù]+\\.\\s*)+([A-Za-zàèéêîôûù]+\\.*\\s*)$");

	@Override
	public boolean isAbbr(String label) {
		Matcher matcher = abbreviationsPattern.matcher(label);
		return matcher.find();
	}

	@Override
	public boolean isAWord(CharSequence label) {
		if (StringUtils.isAllUpperCase(label) || StringUtils.containsAny(label, "1234567890/") || label.length() < 3
				|| Character.isUpperCase(label.charAt(1))) {
			return false;
		}
		return true;
	}

	@Override
	public String clean(String expression) {
		return expression.replaceAll("[«»/\\-\\[\\]\\\"(),'_:;\\\\.…»\\–]", " ");
	}

}
