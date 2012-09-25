package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.Arrays;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

public class TeamInvitePostProcessor implements ActionPostProcessor<TeamInviteAction> {

	private static final Logger LOGGER = Logger.getLogger(TeamInvitePostProcessor.class);
	private final MulticastService multicastService;

	public TeamInvitePostProcessor(final MulticastService multicastService) {
		this.multicastService = multicastService;
	}

	@Override
	public void process(final TeamInviteAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for '" + action.getClass().getSimpleName() + "' (" + action.toString()
				+ "). " + action.getReferenceId().toStringRepresentation());

		final UUID projectId = projectContext.getProjectRepresentation().getId();
		final ModelActionSyncEvent syncEvent = new ModelActionSyncEvent(projectId,
				Arrays.asList(new ModelAction[] { action }), actionContext);

		multicastService.multicastToCurrentUserClientInSpecificProject(syncEvent, projectId);
	}
}
