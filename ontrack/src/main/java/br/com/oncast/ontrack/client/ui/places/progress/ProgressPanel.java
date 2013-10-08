package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanPanel;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanWidgetDisplay;
import br.com.oncast.ontrack.client.ui.components.releasepanel.interaction.ReleasePanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ScopeWidgetDropController;
import br.com.oncast.ontrack.client.ui.generalwidgets.DraggableMembersListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.client.ui.places.planning.dnd.UserAssociationDragHandler;
import br.com.oncast.ontrack.client.ui.places.progress.details.ProgressDetailPanel;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite implements ProgressView {

	private static ProgressPanelUiBinder uiBinder = GWT.create(ProgressPanelUiBinder.class);

	interface ProgressPanelUiBinder extends UiBinder<Widget, ProgressPanel> {}

	interface ProgressPanelStyle extends CssResource {
		String kanbanPanelOnDragTarget();
	}

	@UiField
	protected ProgressPanelStyle style;

	@UiField
	protected ApplicationMenuAndWidgetContainer rootPanel;

	@UiField(provided = true)
	protected ProgressDetailPanel detailWidget;

	@UiField(provided = true)
	protected ReleasePanelWidget releaseWidget;

	@UiField(provided = true)
	protected KanbanPanel kanbanPanel;

	@UiField(provided = true)
	protected DraggableMembersListWidget members;

	@UiField(provided = true)
	protected ReleaseChart chart;

	private final ReleasePanelInteractionHandler interactionHandler;

	public ProgressPanel(final Release release, final Kanban kanban) {
		interactionHandler = new ReleasePanelInteractionHandler();
		final DragAndDropManager userDragAndDropManager = new DragAndDropManager();
		userDragAndDropManager.configureBoundaryPanel(RootPanel.get());
		userDragAndDropManager.addDragHandler(new UserAssociationDragHandler());
		final DropControllerFactory userDropControllerFactory = new DropControllerFactory() {
			@Override
			public DropController create(final Widget panel) {
				return new ScopeWidgetDropController((ScopeWidget) panel);
			}
		};
		members = new DraggableMembersListWidget(userDragAndDropManager);
		releaseWidget = new ReleasePanelWidget(interactionHandler, userDragAndDropManager, userDropControllerFactory, true);
		kanbanPanel = new KanbanPanel(kanban, release, userDragAndDropManager, userDropControllerFactory);
		detailWidget = new ProgressDetailPanel(release);

		chart = new ReleaseChart(release, false);

		initWidget(uiBinder.createAndBindUi(this));

		releaseWidget.setRelease(release);
		releaseWidget.registerDropController(new ProgressReleaseDropManager(kanbanPanel, interactionHandler, style.kanbanPanelOnDragTarget()));
		chart.updateData();
	}

	@Override
	public void registerActionExecutionHandler(final ActionExecutionService actionExecutionService) {
		interactionHandler.configureActionExecutionRequestHandler(actionExecutionService);
		actionExecutionService.addActionExecutionListener(releaseWidget.getActionExecutionListener());
	}

	@Override
	public void unregisterActionExecutionHandler(final ActionExecutionService actionExecutionService) {
		actionExecutionService.removeActionExecutionListener(releaseWidget.getActionExecutionListener());
		interactionHandler.deconfigure();
	}

	@Override
	public ApplicationMenu getApplicationMenu() {
		return rootPanel.getMenu();
	}

	@Override
	public KanbanWidgetDisplay getKanbanPanel() {
		return kanbanPanel;
	}

	@Override
	public FlowPanel getAlertingPanel() {
		return rootPanel.getContentPanelWidget();
	}

	@Override
	public ProgressDetailPanel getProgressDetailWidget() {
		return detailWidget;
	}

	@Override
	public ReleasePanelWidget getReleaseWidget() {
		return releaseWidget;
	}
}