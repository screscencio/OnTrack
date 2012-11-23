package br.com.oncast.ontrack.shared.model.tags.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class TagNotFoundException extends ModelBeanNotFoundException {

	public TagNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
