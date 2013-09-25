package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectMemberProfileUpdateApiRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID projectId;

	private Profile projectProfile;

	private UUID userId;

	ProjectMemberProfileUpdateApiRequest() {}

	public ProjectMemberProfileUpdateApiRequest(final UUID projectId, final UUID userId, final Profile projectProfile) {
		this.projectId = projectId;
		this.userId = userId;
		this.projectProfile = projectProfile;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public Profile getProjectProfile() {
		return projectProfile;
	}

	public UUID getUserId() {
		return userId;
	}

}
