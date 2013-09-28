package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.TeamAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.apache.log4j.Logger;

public class IntegrationServiceProfileUpdateNotifierPostProcessor implements ActionPostProcessor<TeamAction> {

	private static final Logger LOGGER = Logger.getLogger(IntegrationServiceProfileUpdateNotifierPostProcessor.class);

	private final IntegrationService integrationService;

	private final PersistenceService persistenceService;

	public IntegrationServiceProfileUpdateNotifierPostProcessor(final IntegrationService integrationService, final PersistenceService persistenceService) {
		this.integrationService = integrationService;
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final TeamAction action, final ActionContext actionContext, final ProjectContext projectContext) throws UnableToPostProcessActionException {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for " + action.toString() + action.getReferenceId().toString());

		try {
			final UUID projectId = projectContext.getProjectRepresentation().getId();
			final User invitor = persistenceService.retrieveUserById(actionContext.getUserId());

			final UUID inveitedUserId = action.getReferenceId();
			final User invitedUser = persistenceService.retrieveUserById(inveitedUserId);
			final Profile invitedUsersProfile = projectContext.findUser(inveitedUserId).getProjectProfile();
			// TODO replace this method for updateProjectMember's profile;
			integrationService.onUserInvited(projectId, invitor, invitedUser, invitedUsersProfile);
		} catch (final Exception e) {
			LOGGER.error("Unable notify integration service for action " + action.toString(), e);
		}

	}
}
