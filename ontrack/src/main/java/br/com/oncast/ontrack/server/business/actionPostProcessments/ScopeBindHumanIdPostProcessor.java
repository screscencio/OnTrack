package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class ScopeBindHumanIdPostProcessor implements ActionPostProcessor<ScopeBindReleaseAction> {

	private final PersistenceService persistenceService;
	private final MulticastService multicastService;

	public ScopeBindHumanIdPostProcessor(final PersistenceService persistenceService, final MulticastService multicastService) {
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
	}

	@Override
	public void process(final ScopeBindReleaseAction action, final ActionContext actionContext, final ProjectContext context)
			throws UnableToPostProcessActionException {
		try {
			final Scope scope = context.findScope(action.getReferenceId());
			if (action.isUnbinding() || !context.getMetadataList(scope, MetadataType.HUMAN_ID).isEmpty()) return;

			final ProjectRepresentation representation = persistenceService.retrieveProjectRepresentation(context.getProjectRepresentation().getId());
			final String humanId = "" + representation.incrementHumanIdCounter();
			persistenceService.persistOrUpdateProjectRepresentation(representation);

			launchAction(new ScopeBindHumanIdAction(scope.getId(), humanId), representation.getId(), actionContext);
		}
		catch (final Exception e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}
	}

	private void launchAction(final ScopeBindHumanIdAction action, final UUID projectId, final ActionContext actionContext)
			throws UnableToHandleActionException, AuthorizationException {

		final List<ModelAction> list = new ArrayList<ModelAction>();
		list.add(action);
		ServerServiceProvider.getInstance().getBusinessLogic().handleIncomingActionSyncRequest(new ModelActionSyncRequest(projectId, list));
		multicastService.multicastToCurrentUserClientInSpecificProject(new ModelActionSyncEvent(projectId, list, actionContext), projectId);
	}
}
