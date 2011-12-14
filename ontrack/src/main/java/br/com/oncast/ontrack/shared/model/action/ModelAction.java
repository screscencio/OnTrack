package br.com.oncast.ontrack.shared.model.action;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ModelAction extends Serializable {

	ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException;

	UUID getReferenceId();

}
