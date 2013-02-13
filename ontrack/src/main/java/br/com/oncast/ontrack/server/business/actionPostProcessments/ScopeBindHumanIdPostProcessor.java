package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class ScopeBindHumanIdPostProcessor implements ActionPostProcessor<ScopeBindReleaseAction> {

	@Override
	public void process(final ScopeBindReleaseAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		try {
			final Scope scope = projectContext.findScope(action.getReferenceId());
			final List<Metadata> metadataList = projectContext.getMetadataList(scope, MetadataType.HUMAN_ID);
			if (!metadataList.isEmpty()) return;

			final String humanId = "" + (projectContext.getAllMetadata(MetadataType.HUMAN_ID).size() + 1);
			launchAction(projectContext, scope, humanId);
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}
		catch (final UnableToHandleActionException e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}
		catch (final AuthorizationException e) {
			throw new UnableToPostProcessActionException("Could not bind human id", e);
		}

	}

	private void launchAction(final ProjectContext projectContext, final Scope scope, final String humanId) throws UnableToHandleActionException,
			AuthorizationException {
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeBindHumanIdAction(scope.getId(), humanId));
		ServerServiceProvider.getInstance().getBusinessLogic()
				.handleIncomingActionSyncRequest(new ModelActionSyncRequest(projectContext.getProjectRepresentation(), actionList));
	}
}
