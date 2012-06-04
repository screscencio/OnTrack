package br.com.oncast.ontrack.shared.model.user.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class UserNotFoundException extends ModelBeanNotFoundException {

	public UserNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
