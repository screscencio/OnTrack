package br.com.oncast.ontrack.client.ui.components.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseSelectionWidget extends Composite {

	private static ReleaseSelectionWidgetUiBinder uiBinder = GWT.create(ReleaseSelectionWidgetUiBinder.class);

	interface ReleaseSelectionWidgetUiBinder extends UiBinder<Widget, ReleaseSelectionWidget> {}

	public ReleaseSelectionWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
