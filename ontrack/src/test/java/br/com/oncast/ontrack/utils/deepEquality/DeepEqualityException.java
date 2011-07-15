package br.com.oncast.ontrack.utils.deepEquality;

public class DeepEqualityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DeepEqualityException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DeepEqualityException(final String message) {
		super(message);
	}
}
