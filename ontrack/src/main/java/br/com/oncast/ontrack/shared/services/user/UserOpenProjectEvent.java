package br.com.oncast.ontrack.shared.services.user;

public class UserOpenProjectEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private String userEmail;

	protected UserOpenProjectEvent() {}

	public UserOpenProjectEvent(final String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}
}