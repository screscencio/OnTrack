package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import org.apache.log4j.Logger;

public class FileUploadPostProcessor implements ActionPostProcessor<FileUploadAction> {

	private static final Logger LOGGER = Logger.getLogger(FileUploadPostProcessor.class);
	private final PersistenceService persistenceService;

	public FileUploadPostProcessor(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final FileUploadAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for '" + action.getClass().getSimpleName() + "' (" + action.toString()
				+ ").");
		try {
			persistenceService.persistOrUpdateFileRepresentation(projectContext.findFileRepresentation(action.getReferenceId()));
		}
		catch (final FileRepresentationNotFoundException e) {
			final String message = this.getClass().getSimpleName()
					+ ": Unable to persist or update file representation: It was not possible to fin the file representation.";
			LOGGER.error(message, e);
			throw new UnableToPostProcessActionException(message);
		}
		catch (final PersistenceException e) {
			final String message = this.getClass().getSimpleName() + ": Unable to persist or update file representation: There was an persistence exception.";
			LOGGER.error(message, e);
			throw new UnableToPostProcessActionException(message);
		}
	}
}
