package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class FileUploadPostProcessing implements ActionPostProcessor<FileUploadAction> {

	private static final Logger LOGGER = Logger.getLogger(FileUploadPostProcessing.class);
	private final PersistenceService persistenceService;

	public FileUploadPostProcessing(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final FileUploadAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		try {
			persistenceService.persistOrUpdateFileRepresentation(projectContext.findFileRepresentation(action.getReferenceId()));
			// FIXME LOBO Notify action owner.
		}
		catch (FileRepresentationNotFoundException | PersistenceException e) {
			final String message = this.getClass().getSimpleName() + ": Unable to persist or update file representation.";
			LOGGER.error(message, e);
			throw new UnableToPostProcessActionException(message);
		}
	}
}
