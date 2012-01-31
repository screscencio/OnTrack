package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanScopeWidgetFactory;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanScopeItemDragHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanPanel extends Composite implements KanbanWigetDisplay {

	private static KanbanPanelUiBinder uiBinder = GWT.create(KanbanPanelUiBinder.class);

	interface KanbanPanelUiBinder extends UiBinder<Widget, KanbanPanel> {}

	@UiField
	protected HorizontalPanel board;
	private Kanban kanban;
	private Release release;
	final DragAndDropManager dragAndDropMangager;
	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;
	private final ProgressPanelInteractionHandler interactionHandler;

	public KanbanPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		dragAndDropMangager = new DragAndDropManager();
		dragAndDropMangager.configureBoundaryPanel(RootPanel.get());
		interactionHandler = new ProgressPanelInteractionHandler();
		dragAndDropMangager.setDragHandler(new KanbanScopeItemDragHandler(interactionHandler));
		scopeWidgetFactory = new KanbanScopeWidgetFactory(dragAndDropMangager, interactionHandler);
	}

	@Override
	public void configureKanbanPanel(final Kanban kanban, final Release release) {
		this.kanban = kanban;
		this.release = release;
		update();
	}

	@Override
	public void update() {
		board.clear();
		for (final Entry<KanbanColumn, List<Scope>> entry : getScopesByColumn(kanban.getColumns(), release.getScopeList()).entrySet()) {
			final KanbanColumnWidget kanbanColumnWidget = new KanbanColumnWidget(entry.getKey(), scopeWidgetFactory);
			dragAndDropMangager.monitorDropTarget(kanbanColumnWidget.getScopeContainter().getVerticalContainer());
			board.add(kanbanColumnWidget.addScopes(entry.getValue()));
		}

	}

	private Map<KanbanColumn, List<Scope>> getScopesByColumn(final List<KanbanColumn> columns, final List<Scope> scopeList) {
		final Map<KanbanColumn, List<Scope>> map = new HashMap<KanbanColumn, List<Scope>>();
		for (final KanbanColumn c : columns)
			map.put(c, new ArrayList<Scope>());
		for (final Scope scope : scopeList)
			map.get(kanban.columnForDescription(scope.getProgress().getDescription())).add(scope);
		return map;
	}

	@Override
	public void setActionExecutionService(final ActionExecutionService actionExecutionService) {
		interactionHandler.configureActionExecutionRequestHandler(actionExecutionService);
	}
}
