package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.TeamRevogueInvitationAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.apache.log4j.Logger;

public class IntegrationServiceTeamInviteRevoguedNotifierPostProcessor implements ActionPostProcessor<TeamRevogueInvitationAction> {

	private static final Logger LOGGER = Logger.getLogger(IntegrationServiceTeamInviteRevoguedNotifierPostProcessor.class);

	private final IntegrationService integrationService;

	private final PersistenceService persistenceService;

	public IntegrationServiceTeamInviteRevoguedNotifierPostProcessor(final IntegrationService integrationService, final PersistenceService persistenceService) {
		this.integrationService = integrationService;
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final TeamRevogueInvitationAction action, final ActionContext actionContext, final ProjectContext projectContext) throws UnableToPostProcessActionException {
		LOGGER.debug("Executing Post processor '" + this.getClass().getSimpleName() + "' for " + action.toString() + action.getReferenceId().toString());

		try {
			final UUID removedUserId = action.getReferenceId();
			final User removedUser = persistenceService.retrieveUserById(removedUserId);
			integrationService.onUserInviteRevogued(projectContext.getProjectRepresentation(), removedUser);
		} catch (final Exception e) {
			LOGGER.error("Unable notify integration service about user invite revogation for action " + action.toString(), e);
		}

	}
}
