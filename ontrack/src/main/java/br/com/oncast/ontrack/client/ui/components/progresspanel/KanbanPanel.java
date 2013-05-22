package br.com.oncast.ontrack.client.ui.components.progresspanel;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanScopeWidgetFactory;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanColumnDragHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanScopeItemDragHandler;
import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.HideAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.NoHideShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.ShowAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.KanbanColumnDropControllerFactory;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

	private final KanbanScopeWidgetFactory scopeWidgetFactory;

	@UiFactory
	ModelWidgetContainer<KanbanColumn, KanbanColumnWidget> createBoard() {
		return new ModelWidgetContainer<KanbanColumn, KanbanColumnWidget>(new ModelWidgetFactory<KanbanColumn, KanbanColumnWidget>() {

			@Override
			public KanbanColumnWidget createWidget(final KanbanColumn modelBean) {
				final KanbanColumnWidget w = new KanbanColumnWidget(release, modelBean, scopeDragAndDropMangager, interactionHandler, scopeWidgetFactory);
				kanbanColumnDragAndDropMangager.monitorNewDraggableItem(w, w.getDraggableAnchor());
				return w;
			}
		}, new AnimatedContainer(new HorizontalPanel(), new AnimationFactory() {

			@Override
			public ShowAnimation createShowAnimation(final Widget widget) {
				return new NoHideShowAnimation(widget);
			}

			@Override
			public HideAnimation createHideAnimation(final Widget widget) {
				return new NoHideShowAnimation(widget);
			}
		}));
	}

	private final Release release;
	final DragAndDropManager scopeDragAndDropMangager;
	final DragAndDropManager kanbanColumnDragAndDropMangager;
	private final ProgressPanelInteractionHandler interactionHandler;

	public KanbanPanel(final Kanban kanban, final Release release, final DragAndDropManager userDragAndDropManager,
			final DropControllerFactory userDropControllerFactory) {
		this.release = release;

		scopeDragAndDropMangager = new DragAndDropManager();
		scopeDragAndDropMangager.configureBoundaryPanel(RootPanel.get());
		interactionHandler = new ProgressPanelInteractionHandler(release, kanban);
		scopeDragAndDropMangager.addDragHandler(new KanbanScopeItemDragHandler(interactionHandler));

		scopeWidgetFactory = new KanbanScopeWidgetFactory(scopeDragAndDropMangager, userDragAndDropManager, userDropControllerFactory);

		notStartedColumn = new KanbanColumnWidget(release, kanban.getNotStartedColumn(), scopeDragAndDropMangager, interactionHandler, scopeWidgetFactory);
		doneColumn = new KanbanColumnWidget(release, kanban.getDoneColumn(), scopeDragAndDropMangager, interactionHandler, scopeWidgetFactory);

		initWidget(uiBinder.createAndBindUi(this));

		kanbanColumnDragAndDropMangager = new DragAndDropManager();
		kanbanColumnDragAndDropMangager.configureBoundaryPanel(RootPanel.get());
		kanbanColumnDragAndDropMangager.addDragHandler(new KanbanColumnDragHandler(interactionHandler));
		kanbanColumnDragAndDropMangager.monitorDropTarget(draggableColumns.getContainningPanel(), new KanbanColumnDropControllerFactory());
		addStyleName("kanban");

		update(kanban);
	}

	@Override
	public void update(final Kanban kanban) {
		notStartedColumn.update();
		draggableColumns.update(kanban.getNonStaticColumns());
		doneColumn.update();
	}

	@Override
	public void setActionExecutionService(final ActionExecutionService actionExecutionService) {
		interactionHandler.configureActionExecutionRequestHandler(actionExecutionService);
	}

	@Override
	public ScopeCardWidget getWidgetFor(final Scope scope) {
		final KanbanColumnWidget columnWidget = getColumnFor(scope.getProgress());
		if (columnWidget == null) return null;

		return columnWidget.getScopeContainter().getWidgetFor(scope);
	}

	private KanbanColumnWidget getColumnFor(final Progress progress) {
		if (progress.isDone()) return doneColumn;
		if (!progress.isUnderWork()) return notStartedColumn;

		for (int i = 0; i < draggableColumns.getWidgetCount(); i++) {
			final KanbanColumnWidget column = draggableColumns.getWidget(i);
			if (column.getKanbanColumn().getDescription().equals(progress.getDescription())) return column;
		}

		return null;
	}
}
