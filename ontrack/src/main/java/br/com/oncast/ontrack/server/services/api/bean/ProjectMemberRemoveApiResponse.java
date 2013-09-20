package br.com.oncast.ontrack.server.services.api.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectMemberRemoveApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	ProjectMemberRemoveApiResponse() {}

	private ProjectMemberRemoveApiResponse(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public static ProjectMemberRemoveApiResponse success() {
		return new ProjectMemberRemoveApiResponse(null);
	}

	public static ProjectMemberRemoveApiResponse failed(final String errorMessage) {
		return new ProjectMemberRemoveApiResponse(errorMessage);
	}

	public boolean hasFailed() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage == null ? "" : errorMessage;
	}

}
