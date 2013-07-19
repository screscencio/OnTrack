package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Exception exception;

	protected BaseApiResponse() {}

	protected BaseApiResponse(final Exception exception) {
		this.exception = exception;
	}

	public boolean hasErrors() {
		return this.exception != null;
	}

	public Exception getError() {
		return this.exception;
	}

}
