package br.com.oncast.ontrack.shared.services.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.user.User;

public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	private String message;
	private List<User> recipients = new ArrayList<User>();

	public Notification() {}

	public Notification(final String message, final List<User> recipients) {
		this.message = message;
		this.recipients = recipients;
	}

	public List<User> getRecipients() {
		return recipients;
	}

	public String getMessage() {
		return message;
	}
}
