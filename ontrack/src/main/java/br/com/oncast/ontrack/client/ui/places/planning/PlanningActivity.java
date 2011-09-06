package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.ComponentInteractionHandler;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.shared.config.UriConfigurations;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private final ContextProviderService contextProviderService;
	private final ActionExecutionService actionExecutionService;
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;

	public PlanningActivity(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService) {
		this.contextProviderService = contextProviderService;
		this.actionExecutionService = actionExecutionService;

		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		view = new PlanningPanel();

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners());

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setComponentInteractionHandler(new ComponentInteractionHandler() {

			@Override
			public void onScopeSelectionRequest(final UUID scopeId) {
				try {
					view.getScopeTree().setSelectedScope(scopeId);
				}
				catch (final ScopeNotFoundException e) {
					// TODO Think about how to treat this error properly.
					e.printStackTrace();
					throw new RuntimeException("The tree could not reflect the selection made in the release panel because the scope could not be found.", e);
				}
			}
		});

		view.getScopeTree().setScope(contextProviderService.getProjectContext().getProjectScope());
		view.getReleasePanel().setRelease(contextProviderService.getProjectContext().getProjectRelease());
		view.setExporterPath(UriConfigurations.EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL);

		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		actionExecutionService.removeActionExecutionListener(activityActionExecutionListener);
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners() {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}
}