package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.places.PlaceChangeListener;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;

public class ActionExecutionServiceImpl implements ActionExecutionService {

	private final ActionExecutionManager actionManager;
	private final ContextProviderService contextService;
	private final List<ActionExecutionListener> actionExecutionListeners;
	private final ClientAlertingService alertingService;
	private final AuthenticationService authenticationService;

	public ActionExecutionServiceImpl(final ContextProviderService contextService, final ClientAlertingService alertingService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ApplicationPlaceController applicationPlaceController, final AuthenticationService authenticationService) {
		this.alertingService = alertingService;
		this.authenticationService = authenticationService;
		this.actionExecutionListeners = new ArrayList<ActionExecutionListener>();
		this.contextService = contextService;
		this.actionManager = new ActionExecutionManager(new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
					final boolean isClientAction) {
				notifyActionExecutionListeners(action, context, actionContext, executionContext, isClientAction);
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
	public void onNonUserActionRequest(final ModelAction action) throws UnableToCompleteActionException {
		onNonUserActionRequest(action, createActionContext());
	}

	@Override
	public void onNonUserActionRequest(final ModelAction action, final ActionContext actionContext) throws UnableToCompleteActionException {
		actionManager.doNonUserAction(action, contextService.getCurrent(), actionContext);
	}

	@Override
	public void onUserActionExecutionRequest(final ModelAction action) {
		try {
			actionManager.doUserAction(action, contextService.getCurrent(), createActionContext());
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionUndoRequest() {
		try {
			actionManager.undoUserAction(contextService.getCurrent(), createActionContext());
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionRedoRequest() {
		try {
			actionManager.redoUserAction(contextService.getCurrent(), createActionContext());
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	private void notifyActionExecutionListeners(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final ActionExecutionContext executionContext,
			final boolean isUserAction) {
		for (final ActionExecutionListener handler : new ArrayList<ActionExecutionListener>(actionExecutionListeners)) {
			handler.onActionExecution(action, context, actionContext, executionContext, isUserAction);
		}
	}

	private ActionContext createActionContext() {
		return new ActionContext(authenticationService.getCurrentUserId(), new Date());
	}

	@Override
	public HandlerRegistration addActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		if (!this.actionExecutionListeners.contains(actionExecutionListener)) {
			this.actionExecutionListeners.add(actionExecutionListener);
		}
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				actionExecutionListeners.remove(actionExecutionListener);
			}
		};
	}

	@Override
	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		this.actionExecutionListeners.remove(actionExecutionListener);
	}
}