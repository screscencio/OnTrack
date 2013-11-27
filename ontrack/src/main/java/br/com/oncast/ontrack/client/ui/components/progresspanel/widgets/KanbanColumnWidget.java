package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanPositioningDropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.action.KanbanLockAction;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.shared.model.progress.Progress.DEFAULT_NOT_STARTED_NAME;
import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.NOT_STARTED;

public class KanbanColumnWidget extends Composite implements ModelWidget<KanbanColumn> {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	public interface KanbanColumnWidgetStyle extends CssResource {
		String highlight();
	}

	@UiField
	protected KanbanColumnWidgetStyle style;

	@UiField
	protected FocusPanel rootPanel;

	@UiField(provided = true)
	protected EditableLabel title;

	@UiField
	protected FocusPanel draggableAnchor;

	@UiField
	protected Label deleteButton;

	@UiField(provided = true)
	protected KanbanColumnCreateWidget createColumn;

	@UiField
	protected Panel highlightBlock;

	@UiField(provided = true)
	protected KanbanScopeContainer scopeContainer;

	private final KanbanColumn column;

	private final ProgressPanelWidgetInteractionHandler interactionHandler;

	private final Release release;

	public KanbanColumnWidget(final Release release, final KanbanColumn column, final DragAndDropManager dragAndDropManager, final ProgressPanelWidgetInteractionHandler interactionHandler,
			final KanbanScopeWidgetFactory modelWidgetFactory) {
		this.release = release;
		this.column = column;
		this.interactionHandler = interactionHandler;

		this.scopeContainer = new KanbanScopeContainer(column, modelWidgetFactory);
		this.createColumn = new KanbanColumnCreateWidget(interactionHandler, column.getDescription());

		this.title = new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public void onEditionStart() {}

			@Override
			public boolean onEditionRequest(final String text) {
				if (column.isStaticColumn()) return false;

				final String trimmedText = text.trim();
				if (trimmedText.isEmpty() || trimmedText.equals(column.getDescription())) return false;
				interactionHandler.onKanbanColumnRename(column, text);
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}
		});

		initWidget(uiBinder.createAndBindUi(this));

		dragAndDropManager.monitorDropTarget(scopeContainer.getContainningPanel(), new KanbanPositioningDropControllerFactory(this, release));

		if (column.isStaticColumn()) {
			draggableAnchor.setVisible(false);
			deleteButton.setVisible(false);
			if (ProgressState.DONE.getDescription().equals(column.getDescription())) createColumn.setVisible(false);
		}

		update();
	}

	@UiHandler("title")
	protected void onDoubleClick(final DoubleClickEvent event) {
		final Kanban kanban = ClientServices.getCurrentProjectContext().getKanban(release);
		if (!kanban.isLocked()) ClientServices.get().actionExecution().onUserActionExecutionRequest(new KanbanLockAction(release.getId()));
		ClientServices.get().details().showDetailsFor(column.getId());
	}

	@UiHandler("deleteButton")
	protected void onClick(final ClickEvent event) {
		interactionHandler.onKanbanColumnRemove(column);
	}

	public KanbanScopeContainer getScopeContainter() {
		return scopeContainer;
	}

	public Widget getDraggableAnchor() {
		return draggableAnchor;
	}

	public KanbanColumn getKanbanColumn() {
		return column;
	}

	public void setHighlight(final boolean shouldHighlight) {
		highlightBlock.setStyleName(style.highlight(), shouldHighlight);
	}

	@Override
	public boolean update() {
		this.title.setValue(column.getDescription(), false);
		return scopeContainer.update(getTasks());
	}

	private List<Scope> getTasks() {
		final List<Scope> tasks = new ArrayList<Scope>();

		for (final Scope scope : release.getScopeList()) {
			if (scope.getProgress().getState() == ProgressState.UNDER_WORK) addTasks(tasks, scope);
		}

		for (final Scope scope : release.getScopeList()) {
			if (scope.getProgress().getState() == ProgressState.DONE) addTasks(tasks, scope);
		}

		return tasks;
	}

	private void addTasks(final List<Scope> tasks, final Scope scope) {
		for (final Scope task : scope.getAllLeafs()) {
			final Progress progress = task.getProgress();
			final String progressDescription = progress.getState() == NOT_STARTED ? DEFAULT_NOT_STARTED_NAME : progress.getDescription();
			if (progressDescription.equals(column.getDescription())) tasks.add(task);
		}
	}

	@Override
	public KanbanColumn getModelObject() {
		return column;
	}
}
