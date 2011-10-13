package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PopupChartPanel extends Composite {

	private static ProgressChartPanelUiBinder uiBinder = GWT.create(ProgressChartPanelUiBinder.class);

	interface ProgressChartPanelUiBinder extends UiBinder<Widget, PopupChartPanel> {}

	@UiField
	protected ChartPanel chartPanel;

	@UiField
	protected Label progressLabel;

	public PopupChartPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		chartPanel.hide();
	}

	public void setProgress(final String text) {
		progressLabel.setText(text);
	}

	@UiHandler("progressLabel")
	protected void onClick(final ClickEvent e) {
		showMenu();
		e.preventDefault();
		e.stopPropagation();
	}

	public void showMenu() {

		final List<Float> points = new ArrayList<Float>();
		points.add(0.0f);
		points.add(3.0f);
		points.add(8.0f);
		points.add(18.0f);

		final List<String> dates = new ArrayList<String>();
		dates.add("01/10");
		dates.add("02/10");
		dates.add("03/10");
		dates.add("04/10");
		dates.add("05/10");

		chartPanel.setMaxValue(27.0f).setXAxisLineValues(dates).setYAxisLineValues(points).show(progressLabel);
	}
}
