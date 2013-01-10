package br.com.oncast.ontrack.shared.model.metadata.exceptions;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class MetadataNotFoundException extends ModelBeanNotFoundException {

	public MetadataNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
