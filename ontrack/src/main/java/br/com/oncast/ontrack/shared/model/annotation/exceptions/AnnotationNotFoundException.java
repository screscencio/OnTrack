package br.com.oncast.ontrack.shared.model.annotation.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class AnnotationNotFoundException extends ModelBeanNotFoundException {

	public AnnotationNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
