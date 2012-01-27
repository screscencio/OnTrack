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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnWidget extends Composite {

	private static KanbanColumnWidgetUiBinder uiBinder = GWT.create(KanbanColumnWidgetUiBinder.class);

	interface KanbanColumnWidgetUiBinder extends UiBinder<Widget, KanbanColumnWidget> {}

	@UiField
	Label title;

	@UiField
	VerticalModelWidgetContainer<Scope, ScopeWidget> scopeContainer;

	private ModelWidgetContainerListener containerUpdateListener;

	private ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	@UiFactory
	protected VerticalModelWidgetContainer<Scope, ScopeWidget> createScopeContainer() {
		scopeWidgetFactory = new ScopeWidgetFactory(new ProgressPanelWidgetInteractionHandler() {});
		return new VerticalModelWidgetContainer<Scope, ScopeWidget>(scopeWidgetFactory, containerUpdateListener);
	}

	public KanbanColumnWidget(final KanbanColumn column) {
		initWidget(uiBinder.createAndBindUi(this));
		this.title.setText(column.getTitle());
	}

	public KanbanColumnWidget addScopes(final List<Scope> scopes) {
		for (final Scope scope : scopes)
			scopeContainer.createChildModelWidget(scope);
		return this;
	}
}
