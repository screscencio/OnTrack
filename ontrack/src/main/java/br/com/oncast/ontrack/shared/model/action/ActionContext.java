package br.com.oncast.ontrack.shared.model.action;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.User;

public class ActionContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userEmail;
	private Date timestamp;

	protected ActionContext() {}

	public ActionContext(final User user, final Date timestamp) {
		this.timestamp = timestamp;
		this.userEmail = user.getEmail();

	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getUserEmail() {
		return userEmail;
	}

}
