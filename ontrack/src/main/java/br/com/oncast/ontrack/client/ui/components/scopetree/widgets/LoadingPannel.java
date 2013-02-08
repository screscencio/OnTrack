package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoadingPannel extends Composite {

	private static LoadingPannelUiBinder uiBinder = GWT.create(LoadingPannelUiBinder.class);

	interface LoadingPannelUiBinder extends UiBinder<Widget, LoadingPannel> {}

	public LoadingPannel() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}