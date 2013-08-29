package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	AuthenticationApiResponse() {}

	private AuthenticationApiResponse(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isAuthenticated() {
		return getErrorMessage() == null;
	}

	public static AuthenticationApiResponse success() {
		return new AuthenticationApiResponse(null);
	}

	public static AuthenticationApiResponse error(final String errorMessage) {
		if (errorMessage == null) throw new IllegalArgumentException("errorMessage should not be null");
		return new AuthenticationApiResponse(errorMessage);
	}

}
