package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.TextInputPopup.EditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnWidget extends Composite {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	@UiField
	FocusPanel rootPanel;

	@UiField
	Label title;

	@UiField
	FocusPanel draggableAnchor;

	@UiField
	Label deleteButton;

	@UiField
	KanbanScopeContainer scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final KanbanColumn column;

	private final ProgressPanelInteractionHandler interactionHandler;

	@UiFactory
	protected KanbanScopeContainer createScopeContainer() {
		return new KanbanScopeContainer(scopeWidgetFactory, containerUpdateListener);
	}

	public KanbanColumnWidget(final KanbanColumn column, final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory,
			final ProgressPanelInteractionHandler interactionHandler) {
		this.column = column;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.interactionHandler = interactionHandler;
		initWidget(uiBinder.createAndBindUi(this));
		scopeContainer.setKanbanColumn(column);
		this.title.setText(column.getDescription());
		if (column.isStaticColumn()) {
			draggableAnchor.setVisible(false);
			deleteButton.setVisible(false);
		}
	}

	@UiHandler("title")
	protected void onDoubleClick(final DoubleClickEvent event) {
		if (column.isStaticColumn()) return;
		PopupConfig.configPopup().popup(new TextInputPopup("new Description", column.getDescription(), new EditionHandler() {
			@Override
			public boolean onEdition(final String text) {
				final String trimmedText = text.trim();
				if (trimmedText.isEmpty() || trimmedText.equals(column.getDescription())) return false;
				interactionHandler.onKanbanColumnRename(column, text);
				return true;
			}
		})).alignBelow(title).alignRight(title).pop();
	}

	@UiHandler("deleteButton")
	protected void onClick(final ClickEvent event) {
		interactionHandler.onKanbanColumnRemove(column);
	}

	public KanbanColumnWidget addScopes(final List<Scope> scopes) {
		for (final Scope scope : scopes)
			scopeContainer.createChildModelWidget(scope);
		return this;
	}

	public VerticalModelWidgetContainer<Scope, ScopeWidget> getScopeContainter() {
		return scopeContainer;
	}

	public Widget getDraggableAnchor() {
		return draggableAnchor;
	}

	public KanbanColumn getKanbanColumn() {
		return column;
	}
}
