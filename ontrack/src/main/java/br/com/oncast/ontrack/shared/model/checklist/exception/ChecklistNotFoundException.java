package br.com.oncast.ontrack.shared.model.checklist.exception;

import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;

public class ChecklistNotFoundException extends ModelBeanNotFoundException {

	public ChecklistNotFoundException(final String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
