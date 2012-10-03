package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;

public class ActionPostProcessmentsInitializer {

	private boolean initialized;
	private final ActionPostProcessingService postProcessingService;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final NotificationServerService notificationServerService;

	public ActionPostProcessmentsInitializer(final ActionPostProcessingService actionPostProcessingService, final PersistenceService persistenceService,
			final MulticastService multicastService, final NotificationServerService notificationServerService) {
		this.postProcessingService = actionPostProcessingService;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.notificationServerService = notificationServerService;
	}

	public synchronized void initialize() {
		if (initialized) return;
		postProcessingService.registerPostProcessor(createFileUploadPostProcessor(), FileUploadAction.class);
		postProcessingService.registerPostProcessor(createTeamInvitePostProcessor(), TeamInviteAction.class);
		postProcessingService.registerPostProcessor(createImpedimentNotificationCreationPostProcessor(), ImpedimentCreateAction.class);
		initialized = true;
	}

	private ActionPostProcessor<ImpedimentCreateAction> createImpedimentNotificationCreationPostProcessor() {
		return new ImpedimentNotificationCreationPostProcessor(notificationServerService, persistenceService);
	}

	private ActionPostProcessor<TeamInviteAction> createTeamInvitePostProcessor() {
		return new TeamInvitePostProcessor(multicastService);
	}

	private ActionPostProcessor<FileUploadAction> createFileUploadPostProcessor() {
		return new FileUploadPostProcessor(persistenceService);
	}

}
