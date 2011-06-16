package br.com.oncast.ontrack.shared.scope.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface ScopeAction extends IsSerializable {

	void execute(final ProjectContext context) throws UnableToCompleteActionException;

	void rollback(final ProjectContext context) throws UnableToCompleteActionException;

	UUID getScopeId();
}
