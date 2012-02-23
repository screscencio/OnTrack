package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.ComponentInteractionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.BreadcrumbWidget;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.ActivityActionExecutionListener;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.interation.PlanningShortcutMappings;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlanningActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ActivityActionExecutionListener activityActionExecutionListener;
	private PlanningView view;

	public PlanningActivity() {
		activityActionExecutionListener = new ActivityActionExecutionListener();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		view = new PlanningPanel();
		view.setVisible(false);

		final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();
		final ProjectRepresentation currentProjectRepresentation = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrentProjectRepresentation();
		final long currentProjectId = currentProjectRepresentation.getId();
		final ProjectContext projectContext = SERVICE_PROVIDER.getContextProviderService().getProjectContext(currentProjectId);

		actionExecutionService.addActionExecutionListener(activityActionExecutionListener);
		activityActionExecutionListener.setActionExecutionListeners(getActionExecutionSuccessListeners(view));

		view.getScopeTree().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setActionExecutionRequestHandler(actionExecutionService);
		view.getReleasePanel().setComponentInteractionHandler(new ComponentInteractionHandler() {

			@Override
			public void onScopeSelectionRequest(final Scope scope) {
				try {
					view.getScopeTree().setSelectedScope(scope);
				}
				catch (final ScopeNotFoundException e) {
					// TODO Think about how to treat this error properly.
					e.printStackTrace();
					throw new RuntimeException("The tree could not reflect the selection made in the release panel because the scope could not be found.", e);
				}
			}
		});

		view.getScopeTree().setContext(projectContext);
		final Release release = projectContext.getProjectRelease();

		view.getReleasePanel().setRelease(release);
		view.setExporterPath(URLBuilder.buildMindMapExportURL(currentProjectId));

		addBreadcrumbToMenu(currentProjectRepresentation);

		panel.setWidget(view);
		ShortcutService.register(view, SERVICE_PROVIDER.getActionExecutionService(), UndoRedoShortCutMapping.values());
		ShortcutService.register(view, this, PlanningShortcutMappings.values());
		view.setVisible(true);
		view.getScopeTree().setFocus(true);
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getActionExecutionService().removeActionExecutionListener(activityActionExecutionListener);
	}

	private List<ActionExecutionListener> getActionExecutionSuccessListeners(final PlanningView view) {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(view.getScopeTree().getActionExecutionListener());
		list.add(view.getReleasePanel().getActionExecutionListener());
		return list;
	}

	public void showSearchScope() {
		view.getScopeTree().showSearchWidget();
	}

	private void addBreadcrumbToMenu(final ProjectRepresentation project) {
		final BreadcrumbWidget breadcrumb = new BreadcrumbWidget();
		view.getApplicationMenu().setCustomItem(breadcrumb);
		breadcrumb.addItem(project.getName(), new Command() {
			@Override
			public void execute() {}
		});
	}
}