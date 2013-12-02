package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart.ReleaseChartUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseChartPopup extends Composite implements HasCloseHandlers<ReleaseChartPopup>, PopupAware {

	private static final ReleaseChartMessages MESSAGES = GWT.create(ReleaseChartMessages.class);

	private static ChartPanelUiBinder uiBinder = GWT.create(ChartPanelUiBinder.class);

	interface ChartPanelUiBinder extends UiBinder<Widget, ReleaseChartPopup> {}

	@UiField
	protected FocusPanel clickableChartPanel;

	@UiField(provided = true)
	protected ReleaseChart chartPanel;

	@UiField
	protected Image closeIcon;

	@UiField
	protected ReleaseChartEditableDateLabel startDate;

	@UiField
	protected ReleaseChartEditableDateLabel endDate;

	@UiField
	protected ReleaseChartEditableLabel velocity;

	@UiField
	protected Label actualVelocity;

	@UiField
	protected Label actualEndDay;

	@UiField
	protected Label helpText;

	public ReleaseChartPopup(final Release release) {
		this.chartPanel = new ReleaseChart(release, true).setUpdateListener(new ReleaseChartUpdateListener() {
			@Override
			public void onUpdate(final ReleaseChartDataProvider dataProvider) {
				updateData(dataProvider);
			}
		});
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("startDate")
	protected void onStartDateChange(final ValueChangeEvent<Date> event) {
		chartPanel.declareStartDate(event.getValue());
	}

	@UiHandler("endDate")
	protected void onEndDateChange(final ValueChangeEvent<Date> event) {
		chartPanel.declareEndDate(event.getValue());
	}

	@UiHandler("velocity")
	protected void onVelocityChange(final ValueChangeEvent<Float> event) {
		chartPanel.declareEstimatedVelocity(event.getValue());
	}

	@UiHandler("clickableChartPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (!(e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE)) return;

		hide();
		e.preventDefault();
		e.stopPropagation();
	}

	@UiHandler("closeIcon")
	protected void onCloseClick(final ClickEvent event) {
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ReleaseChartPopup> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		chartPanel.updateData();

		startDate.hidePicker();
		endDate.hidePicker();

		clickableChartPanel.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		CloseEvent.fire(this, this);
	}

	private void updateData(final ReleaseChartDataProvider dataProvider) {
		startDate.setValue(dataProvider.getEstimatedStartDay().getJavaDate(), false);
		startDate.setRemoveValueAvailable(dataProvider.hasDeclaredStartDay());

		endDate.setValue(dataProvider.getEstimatedEndDay().getJavaDate(), false);
		endDate.setRemoveValueAvailable(dataProvider.hasDeclaredEndDay());

		final float estimatedVelocity = dataProvider.getEstimatedVelocity();
		velocity.setValue(estimatedVelocity, false);
		velocity.setRemoveValueAvailable(dataProvider.hasDeclaredEstimatedVelocity());
		if (estimatedVelocity < ReleaseEstimator.MIN_SPEED) showWarning();

		final Float actualVelocityValue = dataProvider.getActualVelocity();
		this.actualVelocity.setText(actualVelocityValue != null ? round(actualVelocityValue) : "-");

		final WorkingDay actualEndDayValue = dataProvider.getActualEndDay();
		this.actualEndDay.setText(actualEndDayValue != null ? HumanDateFormatter.formatShortAbsoluteDate(actualEndDayValue.getJavaDate()) : "-");

		helpText.setVisible(hasDifferentEstimatives(dataProvider));
	}

	private void showWarning() {
		ClientServices.get().userGuide().addWarningTip(velocity, MESSAGES.lowEstimatedVelocityWarningTitle(), MESSAGES.lowEstimatedVelocityTips());
	}

	private boolean hasDifferentEstimatives(final ReleaseChartDataProvider dataProvider) {
		return !dataProvider.getEstimatedEndDay().equals(dataProvider.getInferedEstimatedEndDay());
	}

	private String round(final float currentPoints) {
		return ClientDecimalFormat.roundFloat(currentPoints, 1);
	}
}
