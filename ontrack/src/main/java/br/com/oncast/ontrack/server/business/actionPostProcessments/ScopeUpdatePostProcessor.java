package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.context.ProjectRenamedEvent;

public class ScopeUpdatePostProcessor implements ActionPostProcessor<ScopeUpdateAction> {

	private final PersistenceService persistenceService;
	private final MulticastService multicastService;

	public ScopeUpdatePostProcessor(final PersistenceService persistenceService, final MulticastService multicastService) {
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
	}

	@Override
	public void process(final ScopeUpdateAction action, final ActionContext actionContext, final ProjectContext context)
			throws UnableToPostProcessActionException {
		if (!action.getReferenceId().equals(new UUID("0"))) return;

		try {
			final ProjectRepresentation representation = persistenceService.retrieveProjectRepresentation(context.getProjectRepresentation().getId());
			representation.setName(action.getDescription());
			persistenceService.persistOrUpdateProjectRepresentation(representation);

			multicastService.multicastToAllUsersInSpecificProject(new ProjectRenamedEvent(representation), representation.getId());

		}
		catch (final Exception e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}
	}
}
