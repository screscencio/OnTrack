package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActionContext implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1L;

	private UUID userId;
	private Date timestamp;

	protected ActionContext() {}

	public ActionContext(final UUID userId, final Date timestamp) {
		this.timestamp = timestamp;
		this.userId = userId;

	}

	public Date getTimestamp() {
		return timestamp;
	}

	public UUID getUserId() {
		return userId;
	}

}
