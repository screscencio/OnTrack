package br.com.oncast.ontrack.server.services.api.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OnboardingApiRequest {

	private String email;

	OnboardingApiRequest() {}

	public OnboardingApiRequest(final String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

}
