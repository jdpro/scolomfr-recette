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
package fr.scolomfr.recette.model.sources.respresentation.utils;

import javax.xml.xpath.XPath;

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
import fr.scolomfr.recette.model.sources.representation.utils.XPathEngineProvider;
import junit.framework.Assert;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("web")
@ContextConfiguration(classes = { MvcConfiguration.class })
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class XPathEngineProviderTest {

	@Autowired
	private XPathEngineProvider xPathEngineProvider;

	@Test
	public void testNameSpaceUris() {
		XPath xpath = xPathEngineProvider.getXpath();
		Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				xpath.getNamespaceContext().getNamespaceURI("rdf"));
		Assert.assertEquals("http://www.w3.org/2004/02/skos/core#",
				xpath.getNamespaceContext().getNamespaceURI("skos"));
		Assert.assertEquals("http://www.imsglobal.org/xsd/imsvdex_v1p0",
				xpath.getNamespaceContext().getNamespaceURI("vdex"));

	}

}
