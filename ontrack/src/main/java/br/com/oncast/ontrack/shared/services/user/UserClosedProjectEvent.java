package br.com.oncast.ontrack.shared.services.user;

public class UserClosedProjectEvent implements UserStatusEvent {

	private static final long serialVersionUID = 1L;
	private String userEmail;

	protected UserClosedProjectEvent() {}

	public UserClosedProjectEvent(final String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String getUserEmail() {
		return userEmail;
	}
}