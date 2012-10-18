package br.com.oncast.ontrack.shared.model.action;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActionContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID userId;
	private Date timestamp;

	protected ActionContext() {}

	public ActionContext(final User user, final Date timestamp) {
		this.timestamp = timestamp;
		this.userId = user.getId();

	}

	public Date getTimestamp() {
		return timestamp;
	}

	public UUID getUserId() {
		return userId;
	}

}
