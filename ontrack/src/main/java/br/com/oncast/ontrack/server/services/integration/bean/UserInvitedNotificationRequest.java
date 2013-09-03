package br.com.oncast.ontrack.server.services.integration.bean;

import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

	@XmlElement
	private Profile profile;

	UserInvitedNotificationRequest() {}

	public UserInvitedNotificationRequest(final UUID projectId, final UUID invitorId, final UUID invitedUserId, final String invitedUserEmail, final Profile profile) {
		super();
		this.projectId = projectId;
		this.invitorId = invitorId;
		this.invitedUserId = invitedUserId;
		this.invitedUserEmail = invitedUserEmail;
		this.profile = profile;
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

	public Profile getProfile() {
		return profile;
	}

}