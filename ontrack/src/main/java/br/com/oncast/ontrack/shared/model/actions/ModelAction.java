package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.rpc.IsSerializable;

// TODO Study the possibility of dividing actions methods Execute and Rollback into two different actions; having that execution one generates the other
// equivalent action. This would make actions to be smaller and have less information when they would be stored in the server.
public interface ModelAction extends IsSerializable {

	void execute(final ProjectContext context) throws UnableToCompleteActionException;

	void rollback(final ProjectContext context) throws UnableToCompleteActionException;

	UUID getReferenceId();
}
