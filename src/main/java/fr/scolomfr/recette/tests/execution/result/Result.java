package fr.scolomfr.recette.tests.execution.result;

import java.util.Stack;

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

	@XmlElementWrapper(name = "messages")
	@XmlElement(name = "message")
	private Stack<Message> messages;

	private State state;

	public Result() {
		messages = new Stack<>();
		setState(State.TEMPORARY);
	}

	public synchronized Stack<Message> getMessages() {
		return messages;
	}

	public synchronized void addMessage(Message message) {
		this.messages.add(message);
	}

	public synchronized void addMessage(Message.Type type, String key, String title, String content) {
		this.addMessage(new Message(type, key, title, content));
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public enum State {
		TEMPORARY, FINAL;
	}

}
