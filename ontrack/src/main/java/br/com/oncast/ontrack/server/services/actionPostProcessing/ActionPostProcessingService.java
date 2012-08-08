package br.com.oncast.ontrack.server.services.actionPostProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.business.actionPostProcessments.AnnotationCreatePostProcessor;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ActionPostProcessingService {

	private static final Logger LOGGER = Logger.getLogger(ActionPostProcessingService.class);

	// FIXME LOBO Remove this
	private final PersistenceService persistenceService;

	private final Map<Class<?>, List<ActionPostProcessor<?>>> postProcessorsMap = new HashMap<Class<?>, List<ActionPostProcessor<?>>>();

	public ActionPostProcessingService(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public <T extends ModelAction> void registerPostProcessor(final ActionPostProcessor<T> postProcessor,
			@SuppressWarnings("unchecked") final Class<? extends T>... classes) {
		for (final Class<? extends T> clazz : classes)
			registerPostProcessor(postProcessor, clazz);
	}

	public <T extends ModelAction> void registerPostProcessor(final ActionPostProcessor<T> postProcessor, final Class<? extends T> clazz) {
		if (!postProcessorsMap.containsKey(clazz)) postProcessorsMap.put(clazz, new ArrayList<ActionPostProcessor<?>>());

		final List<ActionPostProcessor<?>> postProcessorList = postProcessorsMap.get(clazz);
		postProcessorList.add(postProcessor);
	}

	public void postProcessActions(final ProjectContext context, final ActionContext actionContext, final List<ModelAction> actionList)
			throws UnableToHandleActionException {
		for (final ModelAction action : actionList) {
			postProcessAction(context, actionContext, action);
		}
	}

	public <T extends ModelAction> void postProcessAction(final ProjectContext projectContext, final ActionContext actionContext, final T modelAction)
			throws UnableToPostProcessActionException {
		try {
			if (modelAction instanceof ScopeDeclareProgressAction) ((ScopeDeclareProgressAction) modelAction).setTimestamp(actionContext.getTimestamp());
			if (modelAction instanceof FileUploadAction) {
				persistenceService.persistOrUpdateFileRepresentation(projectContext.findFileRepresentation(modelAction.getReferenceId()));
			}
			// FIXME Lobo adapt annotationCreateAction post processor
			if (modelAction instanceof AnnotationCreateAction) new AnnotationCreatePostProcessor(persistenceService).process(
					(AnnotationCreateAction) modelAction, actionContext,
					projectContext);

			final List<ActionPostProcessor<?>> postProcessors = postProcessorsMap.get(modelAction.getClass());
			if (postProcessors == null) return;
			for (final ActionPostProcessor<?> processor : postProcessors) {
				callPostProcessor(projectContext, actionContext, modelAction, processor);
			}
		}
		catch (final UnableToPostProcessActionException e) {
			throw e;
		}
		catch (final Exception e) {
			LOGGER.error("Post-Processing of the actions failed.", e);
			throw new UnableToPostProcessActionException("Unable to post-process action. The incoming action is invalid.");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void callPostProcessor(final ProjectContext projectContext, final ActionContext actionContext, final ModelAction modelAction,
			final ActionPostProcessor processor) throws UnableToPostProcessActionException {
		processor.process(modelAction, actionContext, projectContext);
	}
}
