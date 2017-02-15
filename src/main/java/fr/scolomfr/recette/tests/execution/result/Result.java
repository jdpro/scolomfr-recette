package fr.scolomfr.recette.tests.execution.result;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
@XStreamAlias("result")
public class Result {

	private Map<String, String> errors;

	private Map<String, String> comments;

	public Result() {
		errors = new LinkedHashMap<>();
		comments = new LinkedHashMap<>();
	}

	public Map<String, String> getComments() {
		return comments;
	}

	public void addComment(String key, String message) {
		this.comments.put(key, message);
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
