package fr.scolomfr.recette.model.tests.execution.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://recette.scolomfr.fr/2017/1")
@XmlRootElement(name = "async")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsyncResult {

	@XmlElement(name = "uri")
	private String uri;

	@XmlElement(name = "status")
	private Status status;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		INITIATED("initiated"), MISSING("missing"), RUNNING("pending"), UNKNOWN("unknown"), DONE("done");
		private String value;

		private Status(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

	}
}
