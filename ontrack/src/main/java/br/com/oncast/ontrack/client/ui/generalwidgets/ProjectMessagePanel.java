package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationWidgetContainer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMessagePanel extends Composite implements ProjectMessageView {

	private static ProjectMessagePanelUiBinder uiBinder = GWT.create(ProjectMessagePanelUiBinder.class);

	interface ProjectMessagePanelUiBinder extends UiBinder<Widget, ProjectMessagePanel> {}

	@UiField
	ApplicationWidgetContainer container;

	public ProjectMessagePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setMainMessage(final String message) {
		container.setText(message);
	}
}