package br.com.oncast.ontrack.server.services.integration.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

@XmlRootElement
public class UserInvitedNotificationRequest {

	@XmlElement
	UUID projectId;

	@XmlElement
	UUID invitorId;

	@XmlElement
	UUID invitedUserId;

	@XmlElement
	String invitedUserEmail;

	UserInvitedNotificationRequest() {}

	public UserInvitedNotificationRequest(final UUID projectId, final UUID invitorId, final UUID invitedUserId, final String invitedUserEmail) {
		this.projectId = projectId;
		this.invitorId = invitorId;
		this.invitedUserId = invitedUserId;
		this.invitedUserEmail = invitedUserEmail;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public UUID getInvitorId() {
		return invitorId;
	}

	public UUID getInvitedUserId() {
		return invitedUserId;
	}

	public String getInvitedUserEmail() {
		return invitedUserEmail;
	}

}
