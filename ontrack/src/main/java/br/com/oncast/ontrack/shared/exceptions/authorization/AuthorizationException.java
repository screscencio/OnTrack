package br.com.oncast.ontrack.shared.exceptions.authorization;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO +++++Threat this exception in the client.
public class AuthorizationException extends Exception {
	private static final long serialVersionUID = 1L;
	private UUID projectId;

	public AuthorizationException() {
		super();
	}

	public AuthorizationException(final String message) {
		super(message);
	}

	public AuthorizationException(final String message, final Throwable e) {
		super(message, e);
	}

	public UUID getProjectId() {
		return projectId;
	}

	public AuthorizationException setProjectId(final UUID projectId) {
		this.projectId = projectId;
		return this;
	}
}
