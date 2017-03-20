package fr.scolomfr.recette.model.tests.execution.result;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlType(namespace = "http://recette.scolomfr.fr/2017/1")
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
@Component
@Scope("prototype")
public class ResultImpl implements Result {

	@XmlElementWrapper(name = "messages")
	@XmlElement(name = "message")
	private Deque<Message> messages;

	private State state;

	private int errorCount;

	private int falsePositiveCount;

	private float complianceIndicator;

	public ResultImpl() {
		reset();
	}

	public Deque<Message> getMessages() {
		return messages;
	}

	@Override
	public void addMessage(Message message) {
		this.messages.push(message);
	}

	@Override
	public void addMessage(Message.Type type, String key, String title, String content) {
		this.addMessage(new Message(type, key, title, content));
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	@Override
	public void incrementErrorCount(boolean ignored) {
		if (ignored) {
			this.falsePositiveCount++;
		} else {
			this.errorCount++;
		}

	}

	@Override
	public float getComplianceIndicator() {
		return complianceIndicator;
	}

	@Override
	public void setComplianceIndicator(float complianceIndicator) {
		this.complianceIndicator = complianceIndicator;
	}

	@Override
	public int getFalsePositiveCount() {
		return falsePositiveCount;
	}

	@Override
	public void setFalsePositiveCount(int falsePositiveCount) {
		this.falsePositiveCount = falsePositiveCount;
	}

	public enum State {
		TEMPORARY, FINAL, ABORTED;
	}

	@Override
	public void reset() {
		messages = new ConcurrentLinkedDeque<>();
		setErrorCount(0);
		setFalsePositiveCount(0);
		setComplianceIndicator(-1);
		setState(State.TEMPORARY);
	}

}
