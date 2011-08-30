package br.com.oncast.ontrack.shared.model.actions;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ModelAction extends Serializable {

	ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException;

	UUID getReferenceId();

}
