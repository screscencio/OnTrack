package br.com.oncast.ontrack.server.services.actionPostProcessing;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ActionPostProcessor<T extends ModelAction> {

	void process(T action, ActionContext actionContext, ProjectContext projectContext) throws UnableToPostProcessActionException, NoResultFoundException, PersistenceException;

}
