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
package fr.scolomfr.recette.model.sources.representation;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

public class SourceRepresentationBuilder<T> {
	final Class<T> typeParameterClass;
	private InputStream inputStream;
	private SourceRepresentationFactory factory;
	private boolean withLineNumbers = true;
	private String dtdDirectory;

	public SourceRepresentationBuilder(Class<T> typeParameterClass) {
		this.typeParameterClass = typeParameterClass;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public SourceRepresentationBuilder<T> inputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	public T build() throws SourceRepresentationBuildException {
		if (typeParameterClass.equals(Document.class)) {
			if (isWithLineNumbers()) {
				factory = new DomDocumentWithLineNumbersSourceRepresentationFactory();
			} else {
				factory = new DomDocumentSourceRepresentationFactory();
			}
			factory.setDtdDirectory(getDtdDirectory());
		}
		@SuppressWarnings("unchecked")
		T representation = (T) factory.getSourceRepresentation(inputStream);
		return representation;
	}

	public boolean isWithLineNumbers() {
		return withLineNumbers;
	}

	public SourceRepresentationBuilder<T> setWithLineNumbers(boolean withLineNumbers) {
		this.withLineNumbers = withLineNumbers;
		return this;
	}

	public String getDtdDirectory() {
		return dtdDirectory;
	}

	public SourceRepresentationBuilder<T> setDtdDirectory(String dtdDirectory) {
		this.dtdDirectory = dtdDirectory;
		return this;
	}
}
