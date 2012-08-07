package br.com.oncast.ontrack.server.services;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class AnnotationCreatePostProcessor {

	public void process(final PersistenceService persistenceService, final ProjectContext context, final ActionContext actionContext,
			final AnnotationCreateAction action) throws UnableToCompleteActionException {
		try {
			persistenceService.persistOrUpdateAnnotation(context.getProjectRepresentation().getId(), action.getReferenceId(),
					action.getAnnotation(context, actionContext));
		}
		catch (final UnableToCompleteActionException e) {
			throw new UnableToCompleteActionException("Action post process failed: Action execiton problem", e);
		}
		catch (final PersistenceException e) {
			throw new UnableToCompleteActionException("Action post process failed: Persistence problem", e);
		}
	}

}
