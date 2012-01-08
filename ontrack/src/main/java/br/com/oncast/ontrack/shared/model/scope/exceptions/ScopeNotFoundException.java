package br.com.oncast.ontrack.shared.model.scope.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class ScopeNotFoundException extends ModelBeanNotFoundException {
	private static final long serialVersionUID = 1L;

	public ScopeNotFoundException(final String message) {
		super(message);
	}

	public ScopeNotFoundException() {
		super();
	}
}
