package br.com.oncast.ontrack.client.services.serverPush;

public class ServerPushException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServerPushException(final String message, final Throwable exception) {
		super(message, exception);
	}

}
