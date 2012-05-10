package br.com.oncast.ontrack.shared.services.authentication;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class UserInformationChangeEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;

	private String email;

	private int projectInvitationQuota;

	private int projectCreationQuota;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected UserInformationChangeEvent() {}

	public UserInformationChangeEvent(final User user) {
		email = user.getEmail();
		projectCreationQuota = user.getProjectCreationQuota();
		projectInvitationQuota = user.getProjectInvitationQuota();
	}

	public String getUserEmail() {
		return email;
	}

	public int getProjectInvitationQuota() {
		return projectInvitationQuota;
	}

	public int getProjectCreationQuota() {
		return projectCreationQuota;
	}
}
