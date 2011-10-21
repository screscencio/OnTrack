package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ChartPanel;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseChartPanel extends Composite implements HasClickHandlers {

	private static ReleaseChartPanelUiBinder uiBinder = GWT.create(ReleaseChartPanelUiBinder.class);

	interface ReleaseChartPanelUiBinder extends UiBinder<Widget, ReleaseChartPanel> {}

	@UiField
	protected ChartPanel chartPanel;

	@UiField
	protected Label progressLabel;

	private final Release release;

	public ReleaseChartPanel(final Release release) {
		this.release = release;
		initWidget(uiBinder.createAndBindUi(this));
		chartPanel.hide();
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public void setProgress(final String text) {
		progressLabel.setText(text);
	}

	public void showBurnUpChart(final ReleaseEstimator releaseEstimator) {
		final ReleaseChartDataProvider dataProvider = new ReleaseChartDataProvider(release, releaseEstimator);

		chartPanel.setMaxValue(dataProvider.getEffortSum())
					.setXAxisLineValues(dataProvider.getReleaseDays())
					.setYAxisLineValues(dataProvider.getAccomplishedEffortsByDate())
					.show(progressLabel);
	}
}
