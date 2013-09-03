package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.user.Profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserCreationApiRequest {

	private String email;

	private Profile globalProfile;

	UserCreationApiRequest() {}

	public UserCreationApiRequest(final String email, final Profile globalProfile) {
		this.email = email;
		this.globalProfile = globalProfile;
	}

	public String getEmail() {
		return email;
	}

	public Profile getGlobalProfile() {
		return globalProfile;
	}

}
