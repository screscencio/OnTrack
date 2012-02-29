package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Separator extends Composite {

	private static SeparatorUiBinder uiBinder = GWT.create(SeparatorUiBinder.class);

	interface SeparatorUiBinder extends UiBinder<Widget, Separator> {}

	public Separator() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
