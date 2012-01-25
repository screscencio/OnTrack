package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProgressPanel extends Composite implements ProgressView {

	private static ProgressPanelUiBinder uiBinder = GWT.create(ProgressPanelUiBinder.class);

	interface ProgressPanelUiBinder extends UiBinder<Widget, ProgressPanel> {}

	@UiField
	protected ApplicationMenu applicationMenu;

	@UiField
	protected KanbanPanel kanbanPanel;

	public ProgressPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public ApplicationMenu getApplicationMenu() {
		return applicationMenu;
	}

	@Override
	public KanbanPanel getKanbanPanel() {
		return kanbanPanel;
	}
}