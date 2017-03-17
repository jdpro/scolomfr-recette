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
package fr.scolomfr.recette.model.tests.impl.standardizedvocabularies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.scolomfr.recette.config.MvcConfiguration;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result;
import fr.scolomfr.recette.model.tests.organization.TestParameters;;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("web")
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class MimeTypesCompletenessTest {

	@Autowired
	private MimeTypesCompleteness mimeTypesCompleteness;

	@Test
	public void testVariousdefaultsUri() {
		mimeTypesCompleteness.reset();
		Map<String, String> executionParameters = new HashMap<>();
		executionParameters.put(TestParameters.Values.SKOSTYPE, "skos");
		executionParameters.put(TestParameters.Values.VERSION, "0.0.0");
		executionParameters.put(TestParameters.Values.GLOBAL, "special");
		mimeTypesCompleteness.setExecutionParameters(executionParameters);
		mimeTypesCompleteness.run();
		Result result = mimeTypesCompleteness.getExecutionResult();
		boolean fancyMimeTypeFound = false;
		boolean missingMimeTypeFound = false;
		boolean caseErrorMimeTypeFound = false;
		boolean obsoleteMimeTypeFound = false;
		boolean deprecatedMimeTypeFound = false;
		String fancyMimeType = "text/does-not-exist";
		String missingMimeType = "application/1d-interleaved-parityfec";
		String caseErrorMimeType = "application/vnd.openxmlformats-officedocument.presentationml.slideUpdateInfo+xml";
		String obsoleteMimeType = "application/vnd.arastra.swi";
		String deprecatedMimeType = "audio/vnd.qcelp";
		//
		assertTrue("There should be multiple errors", result.getErrorCount() > 0);
		for (Message message : result.getMessages()) {
			if (message.getType().equals(Message.Type.ERROR)) {
				boolean fancyMimeTypeInThisMessage = message.getContent().indexOf(fancyMimeType) > 0;
				fancyMimeTypeFound = fancyMimeTypeFound || fancyMimeTypeInThisMessage;
				if (fancyMimeTypeInThisMessage) {
					assertEquals("Message key shuld indicate that the type is missing in iana",
							"a4_missinginiana_" + fancyMimeType, message.getKey());
				}
				boolean missingMimeTypeInThisMessage = message.getContent().indexOf(missingMimeType) > 0;
				missingMimeTypeFound = missingMimeTypeFound || missingMimeTypeInThisMessage;
				if (missingMimeTypeInThisMessage) {
					assertEquals("Message key shuld indicate that the type is missing in sos",
							"a4_missinginskos_" + missingMimeType, message.getKey());
				}
				boolean caseErrorMimeTypeInThisMessage = message.getContent().indexOf(caseErrorMimeType) > 0;
				caseErrorMimeTypeFound = caseErrorMimeTypeFound || caseErrorMimeTypeInThisMessage;
				if (caseErrorMimeTypeInThisMessage) {
					assertEquals("Message key shuld indicate that the type has wrong case",
							"a4_caseerrorinskos_" + caseErrorMimeType, message.getKey());
				}
				boolean obsoleteMimeTypeInThisMessage = message.getContent().indexOf(obsoleteMimeType) > 0;
				obsoleteMimeTypeFound = obsoleteMimeTypeFound || obsoleteMimeTypeInThisMessage;
				if (obsoleteMimeTypeInThisMessage) {
					assertEquals("Message key shuld indicate that the type is obsolete",
							"a4_obsolete_" + obsoleteMimeType, message.getKey());
				}
				boolean deprecatedMimeTypeInThisMessage = message.getContent().indexOf(deprecatedMimeType) > 0;
				deprecatedMimeTypeFound = deprecatedMimeTypeFound || deprecatedMimeTypeInThisMessage;
				if (deprecatedMimeTypeInThisMessage) {
					assertEquals("Message key shuld indicate that the type is deprecated",
							"a4_deprecated_" + deprecatedMimeType, message.getKey());
				}
			}

		}
		assertTrue(fancyMimeType + " should be found in one of the error messages", fancyMimeTypeFound);
		assertTrue(missingMimeType + " should be found in one of the error messages", missingMimeTypeFound);
		assertTrue(caseErrorMimeType + " should be found in one of the error messages", caseErrorMimeTypeFound);
		assertTrue(obsoleteMimeType + " should be found in one of the error messages", obsoleteMimeTypeFound);
		assertTrue(deprecatedMimeType + " should be found in one of the error messages", deprecatedMimeTypeFound);
	}

}
