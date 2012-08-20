package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.Arrays;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

public class TeamInvitePostProcessor implements ActionPostProcessor<TeamInviteAction> {

	private static final Logger LOGGER = Logger.getLogger(TeamInvitePostProcessor.class);
	private final NotificationService notificationService;

	public TeamInvitePostProcessor(final NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void process(final TeamInviteAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for '" + action.getClass().getSimpleName() + "' (" + action.toString()
				+ ").");
		final ModelActionSyncEvent syncEvent = new ModelActionSyncEvent(projectContext.getProjectRepresentation().getId(),
				Arrays.asList(new ModelAction[] { action }), actionContext);
		notificationService.notifyActionToCurrentUser(syncEvent);
	}
}
