package br.com.oncast.ontrack.server.services.integration.bean;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProjectCreationNotificationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private UUID projectId;

	@XmlElement
	private String projectName;

	@XmlElement
	private UUID creatorId;

	ProjectCreationNotificationRequest() {}

	public ProjectCreationNotificationRequest(final UUID projectId, final String projectName, final UUID creatorId) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.creatorId = creatorId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public UUID getCreatorId() {
		return creatorId;
	}

}