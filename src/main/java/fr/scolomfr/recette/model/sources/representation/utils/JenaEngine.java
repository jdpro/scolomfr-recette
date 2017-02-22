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

import java.io.InputStream;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class JenaEngine {

	public Model getModel(InputStream fileInputStream) {
		Model model = ModelFactory.createDefaultModel();
		model.read(fileInputStream, null);
		return model;
	}

	public String getPrefLabelFor(Node node, Model model) {
		Resource resource = model.getResource(node.getURI());
		Property prefLabel = model.getProperty(Constant.SKOS_CORE_NS.toString(),
				Constant.SKOS_PRELABEL_PROPERTY.toString());
		Selector selector = new SimpleSelector(resource, prefLabel, (RDFNode) null);
		StmtIterator stmts = model.listStatements(selector);
		while (stmts.hasNext()) {
			Statement statement = stmts.next();
			return ((Literal) statement.getObject()).getString();
		}
		return null;
	}

	public boolean memberOfVocab(Resource vocab, Resource subject, Model model) {
		Property member = model.getProperty(Constant.SKOS_CORE_NS.toString(), Constant.SKOS_MEMBER_PROPERTY.toString());
		Selector memberSelector = new SimpleSelector(vocab, member, subject);
		StmtIterator stmtIterator = model.listStatements(memberSelector);
		return stmtIterator.hasNext();
	}

	public enum Constant {
		SKOS_CORE_NS("http://www.w3.org/2004/02/skos/core#"), SKOS_NARROWER_PROPERTY("narrower"), SKOS_BROADER_PROPERTY(
				"broader"), SKOS_PRELABEL_PROPERTY("prefLabel"), SKOS_ALTLABEL_PROPERTY(
						"altlabel"), SKOS_SCOPENOTE_PROPERTY("scopeNote"), SKOS_MEMBER_PROPERTY("member");
		private String value;

		private Constant(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

	}
}
