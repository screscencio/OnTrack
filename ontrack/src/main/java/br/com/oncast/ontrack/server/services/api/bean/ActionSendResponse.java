package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionSendResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute
	private long successfullyExecutedActionId = -1;

	private Throwable exception = null;

	ActionSendResponse() {}

	public ActionSendResponse(final long successfullyExecutedActionId) {
		this.successfullyExecutedActionId = successfullyExecutedActionId;
	}

	public ActionSendResponse(final Throwable exception) {
		this.exception = exception;
	}

	public boolean hasSucceeded() {
		return this.successfullyExecutedActionId > 0 && this.exception == null;
	}

	public long getSuccessfullyExecutedActionId() {
		return successfullyExecutedActionId;
	}

	public Throwable getException() {
		return exception;
	}

}
