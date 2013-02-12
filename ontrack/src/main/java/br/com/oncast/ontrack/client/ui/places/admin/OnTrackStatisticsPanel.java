package br.com.oncast.ontrack.client.ui.places.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOverEvent;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOverEventHandler;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.admin.OnTrackServerStatistics;
import br.com.oncast.ontrack.client.services.admin.OnTrackServerStatisticsBag;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	Button wipeLocalData;

	@UiField
	FocusPanel onlineUsersPanel;

	@UiField
	FocusPanel usagePanel;

	private Series onlineUsersSeries;

	private Series activeConnectionsSeries;

	private Series actionsPerHourSeries;

	private Series usersCountSeries;

	private Series projectsCountSeries;

	private final OnTrackServerStatisticsBag statisticsList;

	private final Map<Long, OnTrackServerStatistics> statisticsCache;

	private final Map<String, User> usersCache;

	private final Timer autoUpdateTimer = new Timer() {
		@Override
		public void run() {
			update();
		}
	};

	private float autoUpdateInterval = 60;

	private Chart usageChart;

	private Chart clientsChart;

	public OnTrackStatisticsPanel() {
		statisticsCache = new HashMap<Long, OnTrackServerStatistics>();
		usersCache = new HashMap<String, User>();
		statisticsList = ClientServiceProvider.getInstance().getClientStorageService().loadOnTrackServerStatisticsList();
		initWidget(uiBinder.createAndBindUi(this));

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				initCharts();
			}
		});
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

	@UiHandler("wipeLocalData")
	void onWipeLocalDataClick(final ClickEvent e) {
		statisticsCache.clear();
		statisticsList.getStatisticsList().clear();
		storeStatistics();

		initCharts();
	}

	@UiHandler("updateButton")
	void onUpdateClick(final ClickEvent e) {
		update();
	}

	private void updateView(final OnTrackServerStatistics statistic) {
		onlineUsersSeries.addPoint(getOnlineUsersPoint(statistic));
		activeConnectionsSeries.addPoint(getActiveConnectionsPoint(statistic));
		actionsPerHourSeries.addPoint(getActionsPerHourPoint(statistic));
		usersCountSeries.addPoint(getUsersCountPoint(statistic));
		projectsCountSeries.addPoint(getProjectsCountPoint(statistic));

		statisticsCache.put(statistic.getTimestamp().getTime(), statistic);

		for (final String user : statistic.getOnlineUsers()) {
			ClientServiceProvider.getInstance().getUserDataService().loadRealUser(new UUID(user), new AsyncCallback<User>() {
				@Override
				public void onFailure(final Throwable caught) {}

				@Override
				public void onSuccess(final User result) {
					usersCache.put(user, result);
				}
			});
		}

	}

	private void update() {
		setOptionsEnabled(false);
		ClientServiceProvider.getInstance().getOnTrackAdminService().getStatistics(new AsyncCallback<OnTrackServerStatistics>() {

			@Override
			public void onSuccess(final OnTrackServerStatistics statistic) {
				updateView(statistic);
				setOptionsEnabled(true);
				final List<OnTrackServerStatistics> list = statisticsList.getStatisticsList();
				final int size = list.size();
				if (size > 1 && hasSameAttributes(list.get(size - 1), statistic) && hasSameAttributes(list.get(size - 2), statistic)) {
					list.remove(size - 1);
				}
				list.add(statistic);
				storeStatistics();
			}

			private boolean hasSameAttributes(final OnTrackServerStatistics o1, final OnTrackServerStatistics o2) {
				boolean equals = o1.getActiveConnectionsCount() == o2.getActiveConnectionsCount();
				equals &= o1.getOnlineUsers().size() == o2.getOnlineUsers().size();
				equals &= o1.getOnlineUsers().containsAll(o2.getOnlineUsers());
				equals &= o1.getActionsPerHour() == o2.getActionsPerHour();
				equals &= o1.getUsersCount() == o2.getUsersCount();
				return equals;
			}

			@Override
			public void onFailure(final Throwable caught) {
				showError(caught);
				setOptionsEnabled(true);
			}

			private void showError(final Throwable caught) {
				ClientServiceProvider.getInstance().getClientAlertingService().showError(caught.getLocalizedMessage());
			}
		});
	}

	private void initCharts() {
		createClientsChart();
		createUsageChart();

		for (final OnTrackServerStatistics statistic : statisticsList.getStatisticsList()) {
			updateView(statistic);
		}

		update();

		autoUpdateTimer.cancel();
		autoUpdateTimer.scheduleRepeating((int) (autoUpdateInterval * SECOND));
		autoUpdateIntervalTextBox.setText("" + autoUpdateInterval);
	}

	private void createClientsChart() {
		clientsChart = new Chart()
				.setChartTitleText("Clients")
				.setLegend(new Legend()
						.setEnabled(false))
				.setSeriesPlotOptions(new SeriesPlotOptions()
						.setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
							@Override
							public boolean onMouseOver(final PointMouseOverEvent pointMouseOverEvent) {
								final Point[] points = clientsChart.getSeries(pointMouseOverEvent.getSeriesId()).getPoints();
								for (int i = 0; i < points.length; i++) {
									if (points[i].getX().equals(pointMouseOverEvent.getPoint().getX())) {
										updateToolTip(i);
									}
								}
								return true;
							}
						}))
				.setToolTip(new ToolTip()
						.setShared(true)
						.setCrosshairs(true)
						.setFormatter(new ToolTipFormatter() {
							@Override
							public String format(final ToolTipData toolTipData) {
								final long timestamp = toolTipData.getXAsLong();
								final OnTrackServerStatistics statistic = statisticsCache.get(timestamp);
								final Set<String> onlineUsers = statistic.getOnlineUsers();
								String toolTip = formatTime(timestamp) +
										"<br/><b>Active Connections:</b> " + statistic.getActiveConnectionsCount() +
										"<br/><b>Online Users:</b> " + onlineUsers.size();

								for (final String userIdAsString : onlineUsers) {
									toolTip += "<br/>" + usersCache.get(userIdAsString).getEmail();
								}

								return toolTip;
							}

						})
				);

		clientsChart.getXAxis()
				.setAxisTitle(null)
				.setType(Type.DATE_TIME);

		clientsChart.getYAxis()
				.setAxisTitle(null)
				.setMin(0)
				.setAllowDecimals(false);

		onlineUsersSeries = clientsChart.createSeries()
				.setName("Online Users")
				.setPlotOptions(new LinePlotOptions()
						.setColor("#4444ff")
						.setLineWidth(1));

		activeConnectionsSeries = clientsChart.createSeries()
				.setName("Active Connections")
				.setPlotOptions(new LinePlotOptions()
						.setMarker(new Marker().setEnabled(false))
						.setColor("#ff4444")
						.setLineWidth(1));

		clientsChart.addSeries(activeConnectionsSeries);
		clientsChart.addSeries(onlineUsersSeries);

		onlineUsersPanel.setWidget(clientsChart);
	}

	private void createUsageChart() {
		usageChart = new Chart()
				.setChartTitleText("Server Usage")
				.setLegend(new Legend()
						.setEnabled(false))
				.setSeriesPlotOptions(new SeriesPlotOptions()
						.setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
							@Override
							public boolean onMouseOver(final PointMouseOverEvent pointMouseOverEvent) {
								final Point[] points = usageChart.getSeries(pointMouseOverEvent.getSeriesId()).getPoints();
								for (int i = 0; i < points.length; i++) {
									if (points[i].getX().equals(pointMouseOverEvent.getPoint().getX())) {
										updateToolTip(i);
									}
								}
								return true;
							}
						}))
				.setToolTip(new ToolTip()
						.setShared(true)
						.setCrosshairs(true)
						.setFormatter(new ToolTipFormatter() {
							@Override
							public String format(final ToolTipData toolTipData) {
								final long timestamp = toolTipData.getXAsLong();
								final OnTrackServerStatistics statistic = statisticsCache.get(timestamp);
								final String toolTip = formatTime(timestamp) +
										"<br/><b>Actions / Hour:</b> " + statistic.getActionsPerHour() +
										"<br/><b>Users Count:</b> " + statistic.getUsersCount() +
										"<br/><b>Projects Count:</b> " + statistic.getProjectsCount();
								return toolTip;
							}

						})
				);

		usageChart.getXAxis()
				.setAxisTitle(null)
				.setType(Type.DATE_TIME);

		usageChart.getYAxis()
				.setAxisTitle(null)
				.setMin(0)
				.setAllowDecimals(false);

		actionsPerHourSeries = usageChart.createSeries()
				.setName("Actions / Hour")
				.setPlotOptions(new LinePlotOptions()
						.setColor("#44CC44")
						.setLineWidth(1));

		usersCountSeries = usageChart.createSeries()
				.setName("Total Users")
				.setPlotOptions(new LinePlotOptions()
						.setColor("#CC44CC")
						.setLineWidth(1));

		projectsCountSeries = usageChart.createSeries()
				.setName("Total Users")
				.setPlotOptions(new LinePlotOptions()
						.setColor("#CCCC44")
						.setLineWidth(1));

		usageChart.addSeries(actionsPerHourSeries);
		usageChart.addSeries(usersCountSeries);
		usageChart.addSeries(projectsCountSeries);

		usagePanel.setWidget(usageChart);
	}

	private void updateToolTip(final int pointIndex) {
		usageChart.refreshTooltip(0, pointIndex);
		clientsChart.refreshTooltip(0, pointIndex);
	}

	private void setOptionsEnabled(final boolean enabled) {
		updateButton.setEnabled(enabled);
		wipeLocalData.setEnabled(enabled);
		autoUpdateIntervalTextBox.setEnabled(enabled);
	}

	private Point getOnlineUsersPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic, statistic.getOnlineUsers().size());
	}

	private Point getActiveConnectionsPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic, statistic.getActiveConnectionsCount());
	}

	private Point getActionsPerHourPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic, statistic.getActionsPerHour());
	}

	private Point getUsersCountPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic, statistic.getUsersCount());
	}

	private Point getProjectsCountPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic, statistic.getProjectsCount());
	}

	private Point createPoint(final OnTrackServerStatistics statistic, final Number number) {
		return new Point(statistic.getTimestamp().getTime(), number);
	}

	private String formatTime(final long time) {
		return dateTimeFormat.format(new Date(time));
	}

	private void storeStatistics() {
		ClientServiceProvider.getInstance().getClientStorageService().appendOnTrackServerStatistics(statisticsList);
	}

}
