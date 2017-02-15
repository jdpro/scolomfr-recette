package fr.scolomfr.recette.tests.execution;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
@XStreamAlias("result")
public class Result<T> {

	public Result() {
		errors = new LinkedHashMap<>();
	}

	private Map<String, String> errors;

	@XmlAnyElement
	private T comments;

	public T getContent() {
		return comments;
	}

	public void setContent(T content) {
		this.comments = content;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public void addError(String key, String message) {
		this.errors.put(key, message);
	}

}
