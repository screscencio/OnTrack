package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;

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
		postProcessingService.registerPostProcessor(createAnnotationCreatePostProcessor(), AnnotationCreateAction.class);
		postProcessingService.registerPostProcessor(createAnnotationRemovePostProcessor(), AnnotationRemoveAction.class);
		postProcessingService.registerPostProcessor(createFileUploadPostProcessing(), FileUploadAction.class);
		postProcessingService.registerPostProcessor(createScopeDeclareProgressPostProcessor(), ScopeDeclareProgressAction.class);
		initialized = true;
	}

	private ActionPostProcessor<AnnotationRemoveAction> createAnnotationRemovePostProcessor() {
		return new AnnotationRemovePostProcessor(persistenceService);
	}

	private ActionPostProcessor<ScopeDeclareProgressAction> createScopeDeclareProgressPostProcessor() {
		return new ScopeDeclareProgressPostProcessor();
	}

	private ActionPostProcessor<FileUploadAction> createFileUploadPostProcessing() {
		return new FileUploadPostProcessing(persistenceService);
	}

	private ActionPostProcessor<AnnotationCreateAction> createAnnotationCreatePostProcessor() {
		return new AnnotationCreatePostProcessor(persistenceService);
	}
}
