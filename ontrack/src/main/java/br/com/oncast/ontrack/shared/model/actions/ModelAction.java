package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface ModelAction extends IsSerializable {

	ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException;

	UUID getReferenceId();
}
