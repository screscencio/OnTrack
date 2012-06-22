package br.com.oncast.ontrack.shared.model.file.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class FileRepresentationNotFoundException extends ModelBeanNotFoundException {

	public FileRepresentationNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
