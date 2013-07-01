package br.com.oncast.ontrack.server.services.api.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserCreationRequest {

	@XmlElement
	private String email;

	@XmlElement
	private boolean isSuperUser;

	UserCreationRequest() {}

	public UserCreationRequest(final String email, final boolean isSuperUser) {
		this.email = email;
		this.isSuperUser = isSuperUser;
	}

	public String getEmail() {
		return email;
	}

	public boolean isSuperUser() {
		return isSuperUser;
	}

}
