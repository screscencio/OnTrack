package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.TeamRevogueInvitationAction;

public class ActionPostProcessmentsInitializer {

	private boolean initialized;
	private final ActionPostProcessingService postProcessingService;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final NotificationServerService notificationServerService;
	private NotificationCreationPostProcessor notificationCreationPostProcessor;
	private SendActionToCurrentClientPostProcessor sendActionToCurrentClientPostProcessor;
	private FileUploadPostProcessor fileUploadPostProcessor;

	public ActionPostProcessmentsInitializer(final ActionPostProcessingService actionPostProcessingService, final PersistenceService persistenceService,
			final MulticastService multicastService, final NotificationServerService notificationServerService) {
		this.postProcessingService = actionPostProcessingService;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.notificationServerService = notificationServerService;
	}

	@SuppressWarnings("unchecked")
	public synchronized void initialize() {
		if (initialized) return;
		postProcessingService.registerPostProcessor(getFileUploadPostProcessor(), FileUploadAction.class);
		postProcessingService.registerPostProcessor(getSendActionToCurrentClientPostProcessor(), TeamInviteAction.class, TeamRevogueInvitationAction.class);
		postProcessingService.registerPostProcessor(getNotificationCreationPostProcessor(), ImpedimentCreateAction.class,
				ImpedimentSolveAction.class, ScopeDeclareProgressAction.class, AnnotationCreateAction.class, AnnotationDeprecateAction.class,
				TeamInviteAction.class, TeamRevogueInvitationAction.class);
		initialized = true;
	}

	public synchronized NotificationCreationPostProcessor getNotificationCreationPostProcessor() {
		if (notificationCreationPostProcessor == null) {
			notificationCreationPostProcessor = new NotificationCreationPostProcessor(notificationServerService, persistenceService);
		}
		return notificationCreationPostProcessor;
	}

	public synchronized ActionPostProcessor<ModelAction> getSendActionToCurrentClientPostProcessor() {
		if (sendActionToCurrentClientPostProcessor == null) {
			sendActionToCurrentClientPostProcessor = new SendActionToCurrentClientPostProcessor(multicastService, persistenceService);
		}
		return sendActionToCurrentClientPostProcessor;
	}

	public synchronized ActionPostProcessor<FileUploadAction> getFileUploadPostProcessor() {
		if (fileUploadPostProcessor == null) {
			fileUploadPostProcessor = new FileUploadPostProcessor(persistenceService);
		}
		return fileUploadPostProcessor;
	}

}
