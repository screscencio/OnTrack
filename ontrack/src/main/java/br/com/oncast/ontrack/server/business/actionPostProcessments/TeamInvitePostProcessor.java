package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.Arrays;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.user.UserDataUpdateEvent;

public class TeamInvitePostProcessor implements ActionPostProcessor<TeamInviteAction> {

	private static final Logger LOGGER = Logger.getLogger(TeamInvitePostProcessor.class);
	private final MulticastService multicastService;
	private final PersistenceService persistenceService;

	public TeamInvitePostProcessor(final MulticastService multicastService, final PersistenceService persistenceService) {
		this.multicastService = multicastService;
		this.persistenceService = persistenceService;
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

		try {
			final User user = persistenceService.retrieveUserById(action.getReferenceId());
			multicastService.multicastToAllUsersInSpecificProject(new UserDataUpdateEvent(user), projectId);
		}
		catch (final NoResultFoundException e) {
			LOGGER.error("Unable to post process TeamInviteAction", e);
		}
		catch (final PersistenceException e) {
			LOGGER.error("Unable to post process TeamInviteAction", e);
		}

	}
}
