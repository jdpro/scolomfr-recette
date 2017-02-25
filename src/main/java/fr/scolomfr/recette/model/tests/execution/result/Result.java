package fr.scolomfr.recette.model.tests.execution.result;

import java.util.ArrayDeque;
import java.util.Deque;
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

	private int errorCount;

	private float complianceIndicator;

	public Result() {
		messages = new Stack<>();
		setErrorCount(0);
		setComplianceIndicator(-1);
		setState(State.TEMPORARY);
	}

	public Stack<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		this.messages.push(message);
	}

	public void addMessage(Message.Type type, String key, String title, String content) {
		this.addMessage(new Message(type, key, title, content));
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public void incrementErrorCount() {
		this.errorCount++;
	}

	public float getComplianceIndicator() {
		return complianceIndicator;
	}

	public void setComplianceIndicator(float complianceIndicator) {
		this.complianceIndicator = complianceIndicator;
	}

	public enum State {
		TEMPORARY, FINAL, ABORTED;
	}

}
