package br.com.oncast.ontrack.shared.services.user;

public class UserOfflineEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private String userEmail;

	protected UserOfflineEvent() {}

	public UserOfflineEvent(final String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}
}