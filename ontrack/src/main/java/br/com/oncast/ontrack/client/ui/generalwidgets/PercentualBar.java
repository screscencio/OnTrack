package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PercentualBar extends Composite {

	private static PercetualBarUiBinder uiBinder = GWT.create(PercetualBarUiBinder.class);

	interface PercetualBarUiBinder extends UiBinder<Widget, PercentualBar> {}

	@UiField
	SimplePanel bar;
	private int percentual = -1;

	public PercentualBar() {
		initWidget(uiBinder.createAndBindUi(this));
		setPercentual(0);
	}

	public void setPercentual(final int percentual) {
		if (percentual == this.percentual) return;
		this.percentual = percentual;
		update();
	}

	private void update() {
		bar.setWidth(percentual + "%");
	}
}
