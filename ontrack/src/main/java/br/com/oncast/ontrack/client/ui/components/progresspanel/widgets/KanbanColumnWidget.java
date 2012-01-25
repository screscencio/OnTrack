package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnWidget extends Composite {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	@UiField
	Label title;

	@UiField
	ScopeWidgetContainer scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final KanbanColumn column;

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		scopeWidgetFactory = new ScopeWidgetFactory(new ProgressPanelWidgetInteractionHandler() {});
		return new ScopeWidgetContainer(scopeWidgetFactory, containerUpdateListener);
	}

	public KanbanColumnWidget(final KanbanColumn column) {
		this.column = column;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	private void update() {
		this.title.setText(column.getTitle());
		final List<Scope> scopes = column.getScopes();
		for (final Scope scope : scopes) {
			scopeContainer.createChildModelWidget(scope);
		}
	}
}
