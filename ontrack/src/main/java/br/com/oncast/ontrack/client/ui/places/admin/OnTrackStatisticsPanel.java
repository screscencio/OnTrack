package br.com.oncast.ontrack.client.ui.places.admin;

import java.util.Date;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.YAxis;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class OnTrackStatisticsPanel extends Composite {

	private static OnTrackStatisticsPanelUiBinder uiBinder = GWT.create(OnTrackStatisticsPanelUiBinder.class);

	interface OnTrackStatisticsPanelUiBinder extends UiBinder<Widget, OnTrackStatisticsPanel> {}

	@UiField
	Button updateButton;

	@UiField
	FocusPanel onlineUsersPanel;

	private final Chart onlineUsersChart;

	private Series onlineUsersSeries;

	public OnTrackStatisticsPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		onlineUsersChart = initCharts();
		onlineUsersPanel.setWidget(onlineUsersChart);

		update();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onlineUsersChart.setSizeToMatchContainer();
			}
		});
	}

	private String formatTime(final long time) {
		return dateTimeFormat.format(new Date(time));
	}

	static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy/MM/dd - HH:mm:ss");

	private Chart initCharts() {
		final Chart chart = new Chart()
				.setToolTip(new ToolTip()
						.setFormatter(new ToolTipFormatter() {
							@Override
							public String format(final ToolTipData toolTipData) {
								return formatTime(toolTipData.getXAsLong()) + "<br/><b>" + toolTipData.getSeriesName() + ":</b> " +
										toolTipData.getYAsLong();
							}
						})
				);

		chart.getXAxis()
				.setAxisTitle(null)
				.setType(Type.DATE_TIME);

		final YAxis yAxis = chart.getYAxis();
		yAxis
				.setAxisTitle(null)
				.setMin(0);

		onlineUsersSeries = chart.createSeries()
				.setXAxis(0)
				.setYAxis(0)
				.setName("Online Users");
		chart.addSeries(onlineUsersSeries);

		return chart;
	}

	@UiHandler("updateButton")
	void onClick(final ClickEvent e) {
		update();
	}

	private void updateView(final OnTrackServerStatisticsResponse result) {
		onlineUsersSeries.addPoint(result.getTimestamp().getTime(), result.getOnlineUsers().size());
		onlineUsersChart.redraw();
	}

	private void update() {
		updateButton.setEnabled(false);
		ClientServiceProvider.getInstance().getOnTrackAdminService().getStatistics(new DispatchCallback<OnTrackServerStatisticsResponse>() {
			@Override
			public void onSuccess(final OnTrackServerStatisticsResponse result) {
				updateView(result);
				updateButton.setEnabled(true);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				showError(caught);
				updateButton.setEnabled(true);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				showError(caught);
				updateButton.setEnabled(true);
			}

			private void showError(final Throwable caught) {
				ClientServiceProvider.getInstance().getClientAlertingService().showError(caught.getLocalizedMessage());
			}
		});
	}

}
