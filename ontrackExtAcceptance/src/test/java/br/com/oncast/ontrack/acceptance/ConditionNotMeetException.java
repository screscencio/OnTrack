package br.com.oncast.ontrack.acceptance;

public class ConditionNotMeetException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConditionNotMeetException(final String message) {
		super(message);
	}
}
