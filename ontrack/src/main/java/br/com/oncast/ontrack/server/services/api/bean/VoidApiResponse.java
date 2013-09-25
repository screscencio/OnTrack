package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VoidApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	VoidApiResponse() {}

	private VoidApiResponse(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static VoidApiResponse success() {
		return new VoidApiResponse(null);
	}

	public static VoidApiResponse failed(final String errorMessage) {
		return new VoidApiResponse(errorMessage);
	}

	public boolean hasFailed() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage == null ? "" : errorMessage;
	}

}
