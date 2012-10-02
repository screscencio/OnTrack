package br.com.oncast.ontrack.shared.services.user;

public class UserOnlineEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private String userEmail;

	protected UserOnlineEvent() {}

	public UserOnlineEvent(final String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}
}