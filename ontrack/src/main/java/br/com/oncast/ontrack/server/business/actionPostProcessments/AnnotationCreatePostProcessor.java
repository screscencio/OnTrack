package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class AnnotationCreatePostProcessor implements ActionPostProcessor<AnnotationCreateAction> {

	private static final Logger LOGGER = Logger.getLogger(AnnotationCreatePostProcessor.class);
	private final PersistenceService persistenceService;

	public AnnotationCreatePostProcessor(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final AnnotationCreateAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		try {
			persistenceService.persistOrUpdateAnnotation(projectContext.getProjectRepresentation().getId(), action.getReferenceId(),
					action.getAnnotation(projectContext, actionContext));
		}
		catch (final UnableToCompleteActionException e) {
			final String message = "Action post process failed: Action execiton problem";
			LOGGER.error(message, e);
			throw new UnableToPostProcessActionException(message);
		}
		catch (final PersistenceException e) {
			final String message = "Action post process failed: Persistence problem";
			LOGGER.error(message, e);
			throw new UnableToPostProcessActionException(message);
		}
	}

}
