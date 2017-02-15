package fr.scolomfr.recette.tests.execution.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://recette.scolomfr.fr/2017/1")
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

	@XmlElementWrapper(name = "errors")
	@XmlElement(name = "message")
	private List<Message> errors;

	@XmlElementWrapper(name = "infos")
	@XmlElement(name = "message")
	private List<Message> infos;

	public Result() {
		errors = new ArrayList<>();
		infos = new ArrayList<>();
	}

	public List<Message> getInfos() {
		return infos;
	}

	public void addInfo(Message message) {
		this.infos.add(message);
	}

	public void addInfo(String key, String content) {
		this.addError(new Message(key, content));
	}

	public List<Message> getErrors() {
		return errors;
	}

	public void setErrors(List<Message> errors) {
		this.errors = errors;
	}

	public void addError(Message message) {
		this.errors.add(message);
	}

	public void addError(String key, String content) {
		this.addError(new Message(key, content));

	}

}
