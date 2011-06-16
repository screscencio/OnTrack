package br.com.oncast.ontrack.client.ui.places.contextloading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ContextLoadingPanel extends Composite implements ContextLoadingView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, ContextLoadingPanel> {}

	public ContextLoadingPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}