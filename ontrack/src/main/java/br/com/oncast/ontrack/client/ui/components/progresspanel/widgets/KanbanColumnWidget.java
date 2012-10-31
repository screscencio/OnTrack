package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.TextInputPopup.EditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnWidget extends Composite {

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

	@UiField
	protected KanbanColumnCreateWidget createColumn;

	@UiField
	protected Panel highlightBlock;

	@UiField
	protected KanbanScopeContainer scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final KanbanColumn column;

	private final ProgressPanelWidgetInteractionHandler interactionHandler;

	private final int insertionIndex;

	@UiFactory
	protected KanbanScopeContainer createScopeContainer() {
		return new KanbanScopeContainer(scopeWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected KanbanColumnCreateWidget createNewColumnWidget() {
		return new KanbanColumnCreateWidget(interactionHandler, insertionIndex);
	}

	public KanbanColumnWidget(final KanbanColumn column, final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory,
			final ProgressPanelWidgetInteractionHandler interactionHandler, final int insertionIndex) {
		this.column = column;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.interactionHandler = interactionHandler;
		this.insertionIndex = insertionIndex;
		initWidget(uiBinder.createAndBindUi(this));
		scopeContainer.setKanbanColumn(column);
		this.title.setText(column.getDescription());
		if (column.isStaticColumn()) {
			draggableAnchor.setVisible(false);
			deleteButton.setVisible(false);
			if (ProgressState.DONE.getDescription().equals(column.getDescription())) createColumn.setVisible(false);
		}
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

	public KanbanColumnWidget addScopes(final List<Scope> scopes) {
		scopeContainer.update(scopes);
		return this;
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
}
