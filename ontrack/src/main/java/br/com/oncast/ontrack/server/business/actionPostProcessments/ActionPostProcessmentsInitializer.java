package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;

public class ActionPostProcessmentsInitializer {

	private boolean initialized;
	private final ActionPostProcessingService postProcessingService;
	private final PersistenceService persistenceService;
	private final NotificationService notificationService;

	public ActionPostProcessmentsInitializer(final ActionPostProcessingService actionPostProcessingService, final PersistenceService persistenceService,
			final NotificationService notificationService) {
		this.postProcessingService = actionPostProcessingService;
		this.persistenceService = persistenceService;
		this.notificationService = notificationService;
	}

	public synchronized void initialize() {
		if (initialized) return;
		postProcessingService.registerPostProcessor(createFileUploadPostProcessor(), FileUploadAction.class);
		postProcessingService.registerPostProcessor(createScopeDeclareProgressPostProcessor(), ScopeDeclareProgressAction.class);
		postProcessingService.registerPostProcessor(createTeamInvitePostProcessor(), TeamInviteAction.class);
		initialized = true;
	}

	private ActionPostProcessor<TeamInviteAction> createTeamInvitePostProcessor() {
		return new TeamInvitePostProcessor(notificationService);
	}

	private ActionPostProcessor<ScopeDeclareProgressAction> createScopeDeclareProgressPostProcessor() {
		return new ScopeDeclareProgressPostProcessor();
	}

	private ActionPostProcessor<FileUploadAction> createFileUploadPostProcessor() {
		return new FileUploadPostProcessor(persistenceService, notificationService);
	}

}
