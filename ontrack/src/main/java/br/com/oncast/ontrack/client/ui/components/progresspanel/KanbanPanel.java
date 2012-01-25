package br.com.oncast.ontrack.client.ui.components.progresspanel;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanPanel extends Composite {

	private static KanbanPanelUiBinder uiBinder = GWT.create(KanbanPanelUiBinder.class);

	interface KanbanPanelUiBinder extends UiBinder<Widget, KanbanPanel> {}

	@UiField
	protected HorizontalPanel board;

	public KanbanPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setKanban(final Kanban kanban) {
		board.clear();
		final List<KanbanColumn> columns = kanban.getColumns();
		for (final KanbanColumn column : columns)
			board.add(new KanbanColumnWidget(column));
	}
}
