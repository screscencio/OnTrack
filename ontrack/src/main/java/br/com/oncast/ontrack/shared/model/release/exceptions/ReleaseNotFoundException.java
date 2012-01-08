package br.com.oncast.ontrack.shared.model.release.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class ReleaseNotFoundException extends ModelBeanNotFoundException {
	private static final long serialVersionUID = 1L;

	public ReleaseNotFoundException() {
		super();
	}

	public ReleaseNotFoundException(final String message) {
		super(message);
	}
}
