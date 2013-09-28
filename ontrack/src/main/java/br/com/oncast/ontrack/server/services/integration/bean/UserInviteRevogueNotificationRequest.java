package br.com.oncast.ontrack.server.services.integration.bean;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInviteRevogueNotificationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID userId;

	private UUID projectId;

	UserInviteRevogueNotificationRequest() {}

	public UserInviteRevogueNotificationRequest(final UUID projectId, final UUID userId) {
		this.projectId = projectId;
		this.userId = userId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public UUID getUserId() {
		return userId;
	}

}
