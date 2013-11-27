package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class NullAction implements ModelAction {

	private static final long serialVersionUID = 1L;

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		return this;
	}

	@Override
	public UUID getReferenceId() {
		return new UUID();
	}

}