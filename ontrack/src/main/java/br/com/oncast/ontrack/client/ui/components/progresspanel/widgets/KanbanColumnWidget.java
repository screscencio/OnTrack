package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnWidget extends Composite {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	@UiField
	Label title;

	@UiField
	FocusPanel draggableAnchor;

	@UiField
	KanbanScopeContainer scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final KanbanColumn column;

	@UiFactory
	protected KanbanScopeContainer createScopeContainer() {
		return new KanbanScopeContainer(scopeWidgetFactory, containerUpdateListener);
	}

	public KanbanColumnWidget(final KanbanColumn column, final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory) {
		this.column = column;
		this.scopeWidgetFactory = scopeWidgetFactory;
		initWidget(uiBinder.createAndBindUi(this));
		scopeContainer.setKanbanColumn(column);
		this.title.setText(column.getTitle());
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
