package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedVerticalContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.VerticalPanelWithSpacer;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.HasVerticalAlignment;

public class KanbanScopeContainer extends VerticalModelWidgetContainer<Scope, ScopeWidget> {

	private KanbanColumn kanbanColumn;

	public KanbanScopeContainer(final ModelWidgetFactory<Scope, ScopeWidget> modelWidgetFactory, final ModelWidgetContainerListener listener) {
		super(modelWidgetFactory, createVerticalContainer(), listener);
	}

	private static AnimatedVerticalContainer createVerticalContainer() {
		final VerticalPanelWithSpacer verticalPanelWithSpacer = new VerticalPanelWithSpacer();
		verticalPanelWithSpacer.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		return new AnimatedVerticalContainer(verticalPanelWithSpacer);
	}

	public KanbanColumn getKanbanColumn() {
		return kanbanColumn;
	}

	public void setKanbanColumn(final KanbanColumn kanbanColumn) {
		this.kanbanColumn = kanbanColumn;
	}

}
