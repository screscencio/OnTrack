package br.com.oncast.ontrack.client.ui.generalwidgets.kanban;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.SubjectDetailWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnDetailsWidget extends Composite implements SubjectDetailWidget, ModelWidget<KanbanColumn> {

	private static KanbanColumnDetailsWidgetUiBinder uiBinder = GWT.create(KanbanColumnDetailsWidgetUiBinder.class);

	interface KanbanColumnDetailsWidgetUiBinder extends UiBinder<Widget, KanbanColumnDetailsWidget> {}

	private final KanbanColumn column;

	public KanbanColumnDetailsWidget(final KanbanColumn column) {
		this.column = column;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public KanbanColumn getModelObject() {
		return column;
	}

}
