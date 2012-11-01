package br.com.oncast.ontrack.client.ui.components.progresspanel;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanColumnDragHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanScopeItemDragHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedHorizontalContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.KanbanColumnDropControllerFactory;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanPanel extends Composite implements KanbanWidgetDisplay {

	private static KanbanPanelUiBinder uiBinder = GWT.create(KanbanPanelUiBinder.class);

	interface KanbanPanelUiBinder extends UiBinder<Widget, KanbanPanel> {}

	@UiField(provided = true)
	KanbanColumnWidget notStartedColumn;

	@UiField(provided = true)
	KanbanColumnWidget doneColumn;

	@UiField
	protected ModelWidgetContainer<KanbanColumn, KanbanColumnWidget> draggableColumns;

	@UiFactory
	ModelWidgetContainer<KanbanColumn, KanbanColumnWidget> createBoard() {
		return new ModelWidgetContainer<KanbanColumn, KanbanColumnWidget>(new ModelWidgetFactory<KanbanColumn, KanbanColumnWidget>() {
			@Override
			public KanbanColumnWidget createWidget(final KanbanColumn modelBean) {
				final int index = kanban.indexOf(modelBean.getDescription());
				final KanbanColumnWidget w = new KanbanColumnWidget(release, index, modelBean, scopeDragAndDropMangager, interactionHandler);
				kanbanColumnDragAndDropMangager.monitorNewDraggableItem(w, w.getDraggableAnchor());
				return w;
			}
		}, new AnimatedHorizontalContainer());
	}

	private final Kanban kanban;
	private final Release release;
	final DragAndDropManager scopeDragAndDropMangager;
	final DragAndDropManager kanbanColumnDragAndDropMangager;
	private final ProgressPanelInteractionHandler interactionHandler;

	public KanbanPanel(final Kanban kanban, final Release release) {
		this.kanban = kanban;
		this.release = release;

		scopeDragAndDropMangager = new DragAndDropManager();
		scopeDragAndDropMangager.configureBoundaryPanel(RootPanel.get());
		interactionHandler = new ProgressPanelInteractionHandler(release);
		scopeDragAndDropMangager.setDragHandler(new KanbanScopeItemDragHandler(interactionHandler));

		notStartedColumn = new KanbanColumnWidget(release, 0, kanban.getNotStartedColumn(), scopeDragAndDropMangager,
				interactionHandler);
		doneColumn = new KanbanColumnWidget(release, kanban.size(), kanban.getDoneColumn(), scopeDragAndDropMangager, interactionHandler);

		initWidget(uiBinder.createAndBindUi(this));

		kanbanColumnDragAndDropMangager = new DragAndDropManager();
		kanbanColumnDragAndDropMangager.configureBoundaryPanel(RootPanel.get());
		kanbanColumnDragAndDropMangager.setDragHandler(new KanbanColumnDragHandler(interactionHandler));
		kanbanColumnDragAndDropMangager.monitorDropTarget(draggableColumns.getCallPanel(), new KanbanColumnDropControllerFactory());
		addStyleName("kanban");

		update();
	}

	@Override
	public void update() {
		notStartedColumn.update();
		draggableColumns.update(kanban.getNonStaticColumns());
		doneColumn.update();
	}

	@Override
	public void setActionExecutionService(final ActionExecutionService actionExecutionService) {
		interactionHandler.configureActionExecutionRequestHandler(actionExecutionService);
	}

}
