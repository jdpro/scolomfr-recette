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
package fr.scolomfr.recette.model.sources.representation.utils;

import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import fr.scolomfr.recette.model.tests.organization.TestCase;

public class CustomStreamProgressMonitor implements IProgressMonitor {
	private int prevPercentage = 0;
	private int prevTenPercentage = 0;
	private TestCase testCaseToNotify;
	private String description;

	public CustomStreamProgressMonitor(TestCase testCaseToNotify) {
		this.testCaseToNotify = testCaseToNotify;
	}

	@Override
	public void onUpdateProgress(float progress) {
		int percentage = Math.round(progress * 100);
		int tenPercentage = (int) Math.floor(progress * 10);
		if (percentage > prevPercentage) {
			if (tenPercentage > prevTenPercentage) {
				System.out.print(tenPercentage * 10 + "%");
				prevTenPercentage = tenPercentage;
			} else {
				System.out.print(".");
			}
			prevPercentage = percentage;
			testCaseToNotify.progressionMessage(description, percentage);
		}
	}

	@Override
	public void setTaskDescription(String description) {
		this.description = description;
		testCaseToNotify.progressionMessage(description, 0);
	}

	@Override
	public void reset() {
		prevPercentage = 0;
	}

	@Override
	public void onFinish() {
		testCaseToNotify.progressionMessage("", 100);
	}
}
