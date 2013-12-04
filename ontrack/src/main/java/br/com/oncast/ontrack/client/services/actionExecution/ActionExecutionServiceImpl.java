package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.places.PlaceChangeListener;
import br.com.oncast.ontrack.client.services.time.TimeProviderService;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ShowsUndoAlertAfterActionExecution;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;

public class ActionExecutionServiceImpl implements ActionExecutionService {

	private static final UndoWarningMessages MESSAGES = GWT.create(UndoWarningMessages.class);

	private final ActionExecutionManager actionManager;
	private final ContextProviderService contextService;
	private final List<ActionExecutionListener> actionExecutionListeners;
	private final ClientAlertingService alertingService;
	private final AuthenticationService authenticationService;

	private TimeProviderService timeProvider;

	public ActionExecutionServiceImpl(final ContextProviderService contextService, final ClientAlertingService alertingService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ApplicationPlaceController applicationPlaceController, final AuthenticationService authenticationService, final TimeProviderService timeProvider) {
		this.alertingService = alertingService;
		this.authenticationService = authenticationService;
		this.timeProvider = timeProvider;
		this.actionExecutionListeners = new ArrayList<ActionExecutionListener>();
		this.contextService = contextService;
		this.actionManager = new ActionExecutionManager(contextService, new ActionExecutionListener() {

			private AlertRegistration alertRegistration;

			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isClientAction) {

				notifyActionExecutionListeners(execution, context, isClientAction);
				if (!isClientAction || !(execution.getModelAction() instanceof ShowsUndoAlertAfterActionExecution)) return;

				hideAlert();

				final String warningMessage = ((ShowsUndoAlertAfterActionExecution) execution.getModelAction()).getAlertMessage(MESSAGES);
				alertRegistration = alertingService.showInfoWithButton(warningMessage, MESSAGES.undo(), new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						hideAlert();
						undo(execution);
					}

				});
			}

			private void hideAlert() {
				if (alertRegistration != null) alertRegistration.hide();
			}
		});
		applicationPlaceController.addPlaceChangeListener(new PlaceChangeListener() {
			@Override
			public void onPlaceChange(final Place newPlace) {
				actionManager.cleanActionExecutionHistory();
			}
		});
	}

	private void undo(final ActionExecutionContext executionContext) {
		try {
			actionManager.undo(executionContext);
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onNonUserActionRequest(final UserAction action) throws UnableToCompleteActionException {
		actionManager.doNonUserAction(action);
	}

	@Override
	public void onUserActionExecutionRequest(final ModelAction action) {
		try {
			actionManager.doUserAction(new UserAction(action, authenticationService.getCurrentUserId(), contextService.getCurrentProjectId(), timeProvider.now()));
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionUndoRequest() {
		try {
			actionManager.undoUserAction();
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUserActionRedoRequest() {
		try {
			actionManager.redoUserAction();
		} catch (final UnableToCompleteActionException e) {
			alertingService.showWarning(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	private void notifyActionExecutionListeners(final ActionExecutionContext executionContext, final ProjectContext context, final boolean isUserAction) {
		for (final ActionExecutionListener handler : new ArrayList<ActionExecutionListener>(actionExecutionListeners)) {
			handler.onActionExecution(executionContext, context, isUserAction);
		}
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