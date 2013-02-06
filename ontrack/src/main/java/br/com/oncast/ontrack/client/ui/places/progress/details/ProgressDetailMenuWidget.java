package br.com.oncast.ontrack.client.ui.places.progress.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDetailMenuWidget extends Composite {

	private static ProgressDetailMenuWidgetUiBinder uiBinder = GWT.create(ProgressDetailMenuWidgetUiBinder.class);

	interface ProgressDetailMenuWidgetUiBinder extends UiBinder<Widget, ProgressDetailMenuWidget> {}

	public ProgressDetailMenuWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
