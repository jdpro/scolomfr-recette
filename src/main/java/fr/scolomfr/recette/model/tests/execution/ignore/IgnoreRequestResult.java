package fr.scolomfr.recette.model.tests.execution.ignore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://recette.scolomfr.fr/2017/1")
@XmlRootElement(name = "ignore")
@XmlAccessorType(XmlAccessType.FIELD)
public class IgnoreRequestResult {

	private State state;

	private String key;

	public IgnoreRequestResult() {
		setState(State.ERROR);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public enum State {
		NOT_FOUND, ACCEPTED, DUPLICATE, ERROR;
	}

}
