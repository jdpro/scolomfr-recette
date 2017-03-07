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

import java.util.ArrayList;
import java.util.List;

public class SpellCheckResult {

	public SpellCheckResult() {
		state = State.UNKNOWN;
	}

	private State state;

	private String suggestedlanguage;

	private List<String> invalidFragments = new ArrayList<>();
	private List<String> nonEvaluatedFragments = new ArrayList<>();

	public enum State {
		// evaluated and valid
		VALID,
		// evaluated and invalid
		INVALID,
		// no evaluated
		NOT_EVALUABLE,
		// partially evaluated and valid
		PARTIALY_VALID,
		// partially evaluated and invalid
		PARTIALY_INVALID,
		// default
		UNKNOWN;

	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public List<String> getInvalidFragments() {
		return invalidFragments;
	}

	public void setInvalidFragments(List<String> invalidFragments) {
		this.invalidFragments = invalidFragments;
	}

	public void addInvalidFragment(String invalidFragment) {
		this.invalidFragments.add(invalidFragment);
	}

	public String getSuggestedlanguage() {
		return suggestedlanguage;
	}

	public void setSuggestedlanguage(String suggestedlanguage) {
		this.suggestedlanguage = suggestedlanguage;
	}

	public List<String> getNonEvaluatedFragments() {
		return nonEvaluatedFragments;
	}

	public void setNonEvaluatedFragments(List<String> nonEvaluatedFragments) {
		this.nonEvaluatedFragments = nonEvaluatedFragments;
	}

	public void addNonEvaluatedFragment(String nonEvaluatedFragment) {
		this.nonEvaluatedFragments.add(nonEvaluatedFragment);
	}

	public String getInvalidFragmentsAsString() {
		return concatenate(getInvalidFragments());
	}

	public String getNonEvaluatedFragmentsAsString() {
		return concatenate(getNonEvaluatedFragments());
	}

	public String concatenate(List<String> fragments) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String fragment : fragments) {
			if (!first) {
				sb.append(", ");
			}
			sb.append("\"").append(fragment).append("\"");
			first = false;
		}
		return sb.toString();
	}

}
