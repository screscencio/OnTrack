package br.com.oncast.ontrack.client.ui.places.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OnTrackStatisticsPanel extends Composite {

	private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy/MM/dd - HH:mm:ss");

	private static final int SECOND = 1000;

	private static OnTrackStatisticsPanelUiBinder uiBinder = GWT.create(OnTrackStatisticsPanelUiBinder.class);

	interface OnTrackStatisticsPanelUiBinder extends UiBinder<Widget, OnTrackStatisticsPanel> {}

	@UiField
	TextBox autoUpdateIntervalTextBox;

	@UiField
	Button updateButton;

	@UiField
	FocusPanel onlineUsersPanel;

	private Chart onlineUsersChart;

	private Series onlineUsersSeries;

	private final List<Point> points;

	private final Timer autoUpdateTimer = new Timer() {
		@Override
		public void run() {
			update();
		}
	};

	private float autoUpdateInterval = 30;

	public OnTrackStatisticsPanel() {
		points = new ArrayList<Point>();
		initWidget(uiBinder.createAndBindUi(this));

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onlineUsersChart = initCharts();
				onlineUsersPanel.setWidget(onlineUsersChart);
				update();
			}
		});

		autoUpdateTimer.scheduleRepeating((int) (autoUpdateInterval * SECOND));
		autoUpdateIntervalTextBox.setText("" + autoUpdateInterval);
	}

	@UiHandler("autoUpdateIntervalTextBox")
	void onAutoRefreshIntervalValueChange(final ValueChangeEvent<String> event) {
		try {
			final String val = event.getValue().trim();
			if (val.isEmpty()) {
				autoUpdateTimer.cancel();
				autoUpdateInterval = 0;
				return;
			}

			final Float interval = Float.valueOf(val);
			if (interval > 0.4 && !interval.equals(autoUpdateInterval)) {
				autoUpdateTimer.cancel();
				autoUpdateInterval = interval;
				autoUpdateTimer.scheduleRepeating((int) (autoUpdateInterval * SECOND));
			}
		}
		catch (final Exception e) {}

	}

	@UiHandler("updateButton")
	void onClick(final ClickEvent e) {
		update();
	}

	private void updateView() {
		onlineUsersSeries.addPoint(points.get(points.size() - 1));
		onlineUsersChart.redraw();
	}

	private void update() {
		updateButton.setEnabled(false);
		ClientServiceProvider.getInstance().getOnTrackAdminService().getStatistics(new DispatchCallback<OnTrackServerStatisticsResponse>() {
			@Override
			public void onSuccess(final OnTrackServerStatisticsResponse result) {
				points.add(new Point(result.getTimestamp().getTime(), result.getOnlineUsers().size()));
				updateView();
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

	private Chart initCharts() {
		final Chart chart = new Chart()
				.setChartTitleText("Online Users")
				.setLegend(new Legend()
						.setEnabled(false))
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

		chart.getYAxis()
				.setAxisTitle(null)
				.setMin(0)
				.setAllowDecimals(false);

		onlineUsersSeries = chart.createSeries()
				.setName("Online Users")
				.setPlotOptions(new LinePlotOptions()
						.setColor("#6eb28e")
						.setLineWidth(2))
				.setPoints(points.toArray(new Point[points.size()]));
		chart.addSeries(onlineUsersSeries);

		return chart;
	}

	private String formatTime(final long time) {
		return dateTimeFormat.format(new Date(time));
	}

}
