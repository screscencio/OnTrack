package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import static br.com.oncast.ontrack.shared.model.progress.Progress.DEFAULT_NOT_STARTED_NAME;
import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.NOT_STARTED;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.TextInputPopup.EditionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd.KanbanPositioningDropControllerFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

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

public class KanbanColumnWidget extends Composite implements ModelWidget<KanbanColumn> {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	private static KanbanColumnWidgetMessages messages = GWT.create(KanbanColumnWidgetMessages.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	public interface KanbanColumnWidgetStyle extends CssResource {
		String highlight();
	}

	@UiField
	protected KanbanColumnWidgetStyle style;

	@UiField
	protected FocusPanel rootPanel;

	@UiField
	protected Label title;

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

	public KanbanColumnWidget(final Release release, final KanbanColumn column, final DragAndDropManager dragAndDropManager,
			final ProgressPanelWidgetInteractionHandler interactionHandler, final DragAndDropManager userDragAndDropManager,
			final DropControllerFactory userDropControllerFactory) {
		this.release = release;
		this.column = column;
		this.interactionHandler = interactionHandler;

		this.scopeContainer = new KanbanScopeContainer(column, new KanbanScopeWidgetFactory(dragAndDropManager, interactionHandler, userDragAndDropManager,
				userDropControllerFactory));
		this.createColumn = new KanbanColumnCreateWidget(interactionHandler, column.getDescription());

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
		if (column.isStaticColumn()) return;
		PopupConfig.configPopup().popup(new TextInputPopup(messages.newDescription(), column.getDescription(), new EditionHandler() {
			@Override
			public boolean onEdition(final String text) {
				final String trimmedText = text.trim();
				if (trimmedText.isEmpty() || trimmedText.equals(column.getDescription())) return false;
				interactionHandler.onKanbanColumnRename(column, text);
				return true;
			}
		})).alignVertical(VerticalAlignment.TOP, new AlignmentReference(title, VerticalAlignment.BOTTOM))
				.alignHorizontal(HorizontalAlignment.CENTER, new AlignmentReference(title, HorizontalAlignment.CENTER))
				.pop();
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
		this.title.setText(column.getDescription());
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
