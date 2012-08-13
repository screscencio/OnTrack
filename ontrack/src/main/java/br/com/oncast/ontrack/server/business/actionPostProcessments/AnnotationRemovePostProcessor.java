package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationRemovePostProcessor implements ActionPostProcessor<AnnotationRemoveAction> {

	private static final Logger LOGGER = Logger.getLogger(AnnotationRemovePostProcessor.class);
	private final PersistenceService persistenceService;

	public AnnotationRemovePostProcessor(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final AnnotationRemoveAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		try {
			final UUID projectId = projectContext.getProjectRepresentation().getId();
			final Annotation annotation = persistenceService.retrieveAnnotationById(projectId, action.getAnnotationId());
			annotation.setDeprecated(true);
			persistenceService.persistOrUpdateAnnotation(projectId, action.getReferenceId(), annotation);
		}
		catch (final NoResultFoundException e) {
			final String message = "Action post process failed: Referenced annotation was not found";
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
