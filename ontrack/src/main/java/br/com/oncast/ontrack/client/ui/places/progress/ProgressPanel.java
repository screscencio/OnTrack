package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanPanel;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanWidgetDisplay;
import br.com.oncast.ontrack.client.ui.components.releasepanel.interaction.ReleasePanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
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
	protected ReleasePanelWidget releaseWidget;

	@UiField(provided = true)
	protected KanbanPanel kanbanPanel;

	private final ReleasePanelInteractionHandler interactionHandler;

	public ProgressPanel(final Release release, final Kanban kanban) {
		interactionHandler = new ReleasePanelInteractionHandler();
		// FIXME Mats add DnD handlers here
		releaseWidget = new ReleasePanelWidget(interactionHandler, null, null, true);
		kanbanPanel = new KanbanPanel(kanban, release);

		initWidget(uiBinder.createAndBindUi(this));

		releaseWidget.setRelease(release);
		releaseWidget.registerDropController(new ProgressReleaseDropManager(kanbanPanel, interactionHandler, style.kanbanPanelOnDragTarget()));
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
	public Widget getAlertingPanel() {
		return rootPanel.getContentPanelWidget();
	}
}