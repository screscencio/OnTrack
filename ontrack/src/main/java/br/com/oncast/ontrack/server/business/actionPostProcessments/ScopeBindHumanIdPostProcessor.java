package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import org.apache.log4j.Logger;

public class ScopeBindHumanIdPostProcessor implements ActionPostProcessor<ScopeBindReleaseAction> {

	private static final Logger LOGGER = Logger.getLogger(ScopeBindHumanIdPostProcessor.class);

	private final PersistenceService persistenceService;

	private boolean active;

	public ScopeBindHumanIdPostProcessor(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
		active = true;
	}

	@Override
	public void process(final ScopeBindReleaseAction action, final ActionContext actionContext, final ProjectContext context) throws UnableToPostProcessActionException {
		if (!active) {
			LOGGER.debug("Ignoring ScopeBindReleaseAction post processment of action '" + action + "': the post processor was deactivated.");
			return;
		}

		try {
			final Scope scope = context.findScope(action.getReferenceId());
			if (action.isUnbinding() || !context.getMetadataList(scope, MetadataType.HUMAN_ID).isEmpty()) return;

			final ProjectRepresentation representation = persistenceService.retrieveProjectRepresentation(context.getProjectRepresentation().getId());
			final String humanId = "" + representation.incrementHumanIdCounter();
			persistenceService.persistOrUpdateProjectRepresentation(representation);

			launchAction(new ScopeBindHumanIdAction(scope.getId(), humanId), representation.getId(), actionContext);
		} catch (final Exception e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}
	}

	private void launchAction(final ScopeBindHumanIdAction action, final UUID projectId, final ActionContext actionContext) throws UnableToHandleActionException, AuthorizationException {
		final UserAction ua = new UserAction(action, actionContext.getUserId(), projectId, actionContext.getTimestamp());
		ServerServiceProvider.getInstance().getBusinessLogic().handleIncomingActionSyncRequest(new ModelActionSyncRequest(ua).setShouldReturnToSender(true));
	}

	public void deactivate() {
		LOGGER.debug("Deactivating notification post processment.");
		active = false;
	}

	public void activate() {
		LOGGER.debug("Activating notification post processment.");
		active = true;
	}
}
