package br.com.oncast.ontrack.shared.model;

public class ModelBeanNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ModelBeanNotFoundException(final String message) {
		super(message);
	}

	public ModelBeanNotFoundException() {
		super();
	}
}
