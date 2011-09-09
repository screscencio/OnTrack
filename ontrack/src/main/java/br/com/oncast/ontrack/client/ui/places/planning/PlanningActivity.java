package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.client.ui.components.ComponentInteractionHandler;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.shared.config.UriConfigurations;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private final ContextProviderService contextProviderService;
	private final ActionExecutionService actionExecutionService;
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private final GlobalNativeEventService globalNativeEventService;
	private final NativeEventListener globalKeyUpListener;

	public PlanningActivity(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final GlobalNativeEventService globalNativeEventService) {
		this.contextProviderService = contextProviderService;
		this.actionExecutionService = actionExecutionService;
		this.globalNativeEventService = globalNativeEventService;

		activityActionExecutionListener = new ActivityActionExecutionListener();
		globalKeyUpListener = new NativeEventListener() {

			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				PlanningShortcutMappings.interpretKeyboardCommand(actionExecutionService, nativeEvent.getKeyCode(), nativeEvent.getCtrlKey(),
						nativeEvent.getShiftKey(),
						nativeEvent.getAltKey());
			}
		};
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final PlanningView view = new PlanningPanel();

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

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

		view.getScopeTree().setContext(contextProviderService.getProjectContext());
		view.getReleasePanel().setRelease(contextProviderService.getProjectContext().getProjectRelease());
		view.setExporterPath(UriConfigurations.EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL);

		panel.setWidget(view);
		globalNativeEventService.addKeyUpListener(globalKeyUpListener);
	}

	@Override
	public void onStop() {
		globalNativeEventService.removeKeyUpListener(globalKeyUpListener);
		actionExecutionService.removeActionExecutionListener(activityActionExecutionListener);
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners(final PlanningView view) {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}
}