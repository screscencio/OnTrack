package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private final ContextProviderService contextProviderService;
	private final ActionExecutionService actionExecutionService;
	private final ActivityActionExecutionListener activityActionExecutionListener;

	public PlanningActivity(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService) {
		this.contextProviderService = contextProviderService;
		this.actionExecutionService = actionExecutionService;

		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final PlanningView view = new PlanningPanel();

		activityActionExecutionListener.setActionExecutionListeners(view.getActionExecutionSuccessListeners());
		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);

		view.setActionExecutionRequestHandler(actionExecutionService);
		view.setScope(contextProviderService.getProjectContext().getProjectScope());
		view.setRelease(contextProviderService.getProjectContext().getProjectRelease());
		view.setExporterPath(GWT.getModuleBaseURL() + "servlet/exporttomindmap");

		panel.setWidget(view);
	}

	@Override
	public void onStop() {
		actionExecutionService.removeActionExecutionListener(activityActionExecutionListener);
	}
}