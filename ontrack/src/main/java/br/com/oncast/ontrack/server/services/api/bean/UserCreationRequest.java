package br.com.oncast.ontrack.server.services.api.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserCreationRequest {

	private String email;

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
