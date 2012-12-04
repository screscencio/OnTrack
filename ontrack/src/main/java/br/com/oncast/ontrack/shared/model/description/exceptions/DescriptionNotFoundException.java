package br.com.oncast.ontrack.shared.model.description.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class DescriptionNotFoundException extends ModelBeanNotFoundException {

	public DescriptionNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}