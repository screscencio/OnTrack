package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.client.ui.components.ComponentInteractionHandler;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final GlobalNativeEventService globalNativeEventService = GlobalNativeEventService.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private final NativeEventListener globalKeyUpListener;
	private PlanningView view;

	public PlanningActivity() {

		activityActionExecutionListener = new ActivityActionExecutionListener();
		globalKeyUpListener = new NativeEventListener() {

			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				PlanningShortcutMappings.interpretKeyboardCommand(
						PlanningActivity.this,
						nativeEvent.getKeyCode(),
						nativeEvent.getCtrlKey(),
						nativeEvent.getShiftKey(),
						nativeEvent.getAltKey());
			}
		};
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		view = new PlanningPanel();

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
		final ProjectRepresentation currentProjectRepresentation = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrentProjectRepresentation();
		final long currentProjectId = currentProjectRepresentation.getId();
		final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(currentProjectId);

		Window.setTitle(currentProjectRepresentation.getName());

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

		view.getScopeTree().setContext(projectContext);
		view.getReleasePanel().setRelease(projectContext.getProjectRelease());
		view.setExporterPath(URLBuilder.buildMindMapExportURL(currentProjectId));

		panel.setWidget(view);
		globalNativeEventService.addKeyUpListener(globalKeyUpListener);
		view.getScopeTree().setFocus(true);
	}

	@Override
	public void onStop() {
		Window.setTitle(DefaultViewSettings.TITLE);

		globalNativeEventService.removeKeyUpListener(globalKeyUpListener);
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners(final PlanningView view) {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}

	public ActionExecutionService getActionRequestHandler() {
		return SERVICE_PROVIDER.getActionExecutionService();
	}

	public void showSearchScope() {
		view.getScopeTree().showSearchWidget();
	}
}