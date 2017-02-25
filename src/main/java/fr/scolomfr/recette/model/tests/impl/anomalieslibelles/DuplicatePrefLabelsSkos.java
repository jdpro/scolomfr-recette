/**
 * 
 * Scolomfr Recette
 * 
 * Copyright (C) 2017  Direction du Numérique pour l'éducation - Ministère de l'éducation nationale, de l'enseignement supérieur et de la Recherche
 * Copyright (C) 2017 Joachim Dornbusch
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
package fr.scolomfr.recette.model.tests.impl.anomalieslibelles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.data.util.Pair;

import fr.scolomfr.recette.model.sources.representation.utils.JenaEngine;
import fr.scolomfr.recette.model.tests.execution.result.Message;
import fr.scolomfr.recette.model.tests.execution.result.Result.State;
import fr.scolomfr.recette.model.tests.impl.AbstractJenaTestCase;
import fr.scolomfr.recette.model.tests.impl.DuplicateErrorCodeException;
import fr.scolomfr.recette.model.tests.organization.TestCaseIndex;
import fr.scolomfr.recette.model.tests.organization.TestParameters;

/**
 * Two
 */
@TestCaseIndex(index = "a6")
@TestParameters(names = { TestParameters.Values.VERSION, TestParameters.Values.VOCABULARY })
public class DuplicatePrefLabelsSkos extends AbstractJenaTestCase {

	@Override
	public void run() {
		Model model = getModel(getVersion(), getVocabulary(), "skos");
		Map<String, List<Pair<String, Node>>> preflabelsOfChildren = new HashMap<>();
		Property children = model.getProperty(JenaEngine.Constant.SKOS_CORE_NS.toString(),
				JenaEngine.Constant.SKOS_NARROWER_PROPERTY.toString());
		Selector childrenSelector = new SimpleSelector((Resource) null, children, (RDFNode) null);
		StmtIterator stmts = model.listStatements(childrenSelector);
		Resource parent;
		Node child;
		List<Pair<String, Node>> prefLabels;
		while (stmts.hasNext()) {
			Statement statement = stmts.next();
			parent = statement.getSubject();
			child = statement.getObject().asNode();
			String label = jenaEngine.getPrefLabelFor(child, model);
			if (StringUtils.isEmpty(label)) {
				continue;
			}
			if (preflabelsOfChildren.containsKey(parent.getURI())) {
				prefLabels = preflabelsOfChildren.get(parent.getURI());
				for (Pair<String, Node> preflabel : prefLabels) {
					if (StringUtils.equals(label, preflabel.getFirst())) {
						// duplicate siblings						
						String errorCode = null;
						try {
							errorCode = generateUniqueErrorCode(label + parent.getURI() + child.getURI());
						} catch (DuplicateErrorCodeException e) {
							logger.error("Errorcode {} generated twice ", errorCode, e);
							continue;
						}
						boolean ignored = errorIsIgnored(errorCode);
						result.incrementErrorCount(ignored);
						result.addMessage(
								new Message(ignored ? Message.Type.IGNORED : Message.Type.ERROR, errorCode, i18n.tr("tests.impl.a6.result.title"),
										i18n.tr("tests.impl.a6.result.content", new Object[] { child.getURI(),
												preflabel.getSecond().getURI(), label, parent.getURI() })));
					}
				}
			} else {
				preflabelsOfChildren.put(parent.getURI(), new ArrayList<>());
			}
			preflabelsOfChildren.get(parent.getURI()).add(Pair.of(label, child));
		}

		result.setState(State.FINAL);
		

	}

}
