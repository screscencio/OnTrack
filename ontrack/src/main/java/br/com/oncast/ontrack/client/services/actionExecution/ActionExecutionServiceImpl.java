package br.com.oncast.ontrack.client.services.actionExecution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.places.PlaceChangeListener;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.place.shared.Place;

public class ActionExecutionServiceImpl implements ActionExecutionService {

	private final ActionExecutionManager actionManager;
	private final ContextProviderService contextService;
	private final List<ActionExecutionListener> actionExecutionListeners;
	private final ClientNotificationService notificationService;
	private final AuthenticationService authenticationService;

	public ActionExecutionServiceImpl(final ContextProviderService contextService, final ClientNotificationService notificationService,
			final ProjectRepresentationProvider projectRepresentationProvider, final ApplicationPlaceController applicationPlaceController,
			final AuthenticationService authenticationService) {
		this.notificationService = notificationService;
		this.authenticationService = authenticationService;
		this.actionExecutionListeners = new ArrayList<ActionExecutionListener>();
		this.contextService = contextService;
		this.actionManager = new ActionExecutionManager(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context,
					final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isClientAction) {
				notifyActionExecutionListeners(action, context, actionContext, inferenceInfluencedScopeSet, isClientAction);
			}
		});
		applicationPlaceController.addPlaceChangeListener(new PlaceChangeListener() {
			@Override
			public void onPlaceChange(final Place newPlace) {
				actionManager.cleanActionExecutionHistory();
			}
		});
	}

	@Override
	public void onNonUserActionRequest(final ModelAction action, final ActionContext actionContext) throws UnableToCompleteActionException {
		actionManager.doNonUserAction(action, contextService.getCurrentProjectContext(), actionContext);
	}

	@Override
	public void onUserActionExecutionRequest(final ModelAction action) {
		try {
			actionManager.doUserAction(action, contextService.getCurrentProjectContext(), createActionContext());
		}
		catch (final UnableToCompleteActionException e) {
			notificationService.showWarning(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionUndoRequest() {
		try {
			actionManager.undoUserAction(contextService.getCurrentProjectContext(), createActionContext());
		}
		catch (final UnableToCompleteActionException e) {
			notificationService.showWarning(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionRedoRequest() {
		try {
			actionManager.redoUserAction(contextService.getCurrentProjectContext(), createActionContext());
		}
		catch (final UnableToCompleteActionException e) {
			notificationService.showWarning(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void notifyActionExecutionListeners(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet,
			final boolean isUserAction) {
		for (final ActionExecutionListener handler : new ArrayList<ActionExecutionListener>(actionExecutionListeners)) {
			handler.onActionExecution(action, context, actionContext, inferenceInfluencedScopeSet, isUserAction);
		}
	}

	private ActionContext createActionContext() {
		return new ActionContext(authenticationService.getCurrentUser(), new Date());
	}

	@Override
	public void addActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		if (this.actionExecutionListeners.contains(actionExecutionListener)) return;
		this.actionExecutionListeners.add(actionExecutionListener);
	}

	@Override
	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		this.actionExecutionListeners.remove(actionExecutionListener);
	}
}