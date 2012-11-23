package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.VerticalPanelWithSpacer;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class KanbanScopeContainer extends ModelWidgetContainer<Scope, ScopeWidget> {

	private final KanbanColumn kanbanColumn;

	public KanbanScopeContainer(final KanbanColumn column, final ModelWidgetFactory<Scope, ScopeWidget> modelWidgetFactory) {
		super(modelWidgetFactory, createVerticalContainer());
		this.kanbanColumn = column;
	}

	private static AnimatedContainer createVerticalContainer() {
		final VerticalPanelWithSpacer verticalPanelWithSpacer = new VerticalPanelWithSpacer();
		verticalPanelWithSpacer.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		return new AnimatedContainer(verticalPanelWithSpacer);
	}

	public KanbanColumn getKanbanColumn() {
		return kanbanColumn;
	}

}
