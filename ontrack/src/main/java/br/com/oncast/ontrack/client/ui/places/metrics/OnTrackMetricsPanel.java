package br.com.oncast.ontrack.client.ui.places.metrics;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.places.metrics.widgets.ProjectMetricsWidget;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetricsBag;
import br.com.oncast.ontrack.shared.services.metrics.ProjectMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Legend.Align;
import org.moxieapps.gwt.highcharts.client.Legend.VerticalAlign;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOverEvent;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOverEventHandler;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.PieDataLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.PiePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OnTrackMetricsPanel extends Composite {

	private static final int MAX_ACTIONS = 8;

	private static final String PROJECTS_COUNT_COLOR = "#CCCC44";

	private static final String USERS_COUNT_COLOR = "#CC44CC";

	private static final String ACTIONS_PER_HOUR_COLOR = "#44CC44";

	private static final String ACTIVE_CONNECTIONS_COLOR = "#ff4444";

	private static final String ONLINE_USERS_COLOR = "#4444ff";

	private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy/MM/dd - HH:mm:ss");

	private static final int SECOND = 1000;

	private static OnTrackStatisticsPanelUiBinder uiBinder = GWT.create(OnTrackStatisticsPanelUiBinder.class);

	interface OnTrackStatisticsPanelUiBinder extends UiBinder<Widget, OnTrackMetricsPanel> {}

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

	@UiField
	FocusPanel actionsPanel;

	@UiField
	FlowPanel projectsPanel;

	private Series onlineUsersSeries;

	private Series activeConnectionsSeries;

	private Series actionsPerHourSeries;

	private Series usersCountSeries;

	private Series projectsCountSeries;

	private final OnTrackServerMetricsBag metricsList;

	private final Map<Long, OnTrackServerMetrics> metricsCache;

	private final Map<String, User> usersCache;

	private final Map<String, ProjectMetricsWidget> projects;

	private final Timer autoUpdateTimer = new Timer() {
		@Override
		public void run() {
			update();
		}
	};

	private float autoUpdateInterval = 5 * 60;

	private Chart usageChart;

	private Chart clientsChart;

	private Chart actionsRatioChart;

	public OnTrackMetricsPanel() {
		projects = new HashMap<String, ProjectMetricsWidget>();
		metricsCache = new HashMap<Long, OnTrackServerMetrics>();
		usersCache = new HashMap<String, User>();
		metricsList = ClientServices.get().storage().loadOnTrackServerMetricsList();
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
		} catch (final Exception e) {}

	}

	@UiHandler("wipeLocalData")
	void onWipeLocalDataClick(final ClickEvent e) {
		metricsCache.clear();
		metricsList.getStatisticsList().clear();
		projects.clear();
		projectsPanel.clear();
		storeStatistics();

		initCharts();
	}

	@UiHandler("updateButton")
	void onUpdateClick(final ClickEvent e) {
		update();
	}

	private void updateView(final OnTrackServerMetrics statistic) {
		onlineUsersSeries.addPoint(getOnlineUsersPoint(statistic));
		activeConnectionsSeries.addPoint(getActiveConnectionsPoint(statistic));
		actionsPerHourSeries.addPoint(getActionsPerHourPoint(statistic));
		usersCountSeries.addPoint(getUsersCountPoint(statistic));
		projectsCountSeries.addPoint(getProjectsCountPoint(statistic));

		// FIXME removed to improve performance
		// updateActionsRatioChart(statistic);
		// updateProjectsStatistics(statistic);

		metricsCache.put(statistic.getTimestamp().getTime(), statistic);

		for (final String user : statistic.getOnlineUsers()) {
			ClientServices.get().userData().loadRealUser(new UUID(user), new AsyncCallback<User>() {
				@Override
				public void onFailure(final Throwable caught) {}

				@Override
				public void onSuccess(final User result) {
					usersCache.put(user, result);
				}
			});
		}
	}

	private void updateProjectsStatistics(final OnTrackServerMetrics statistic) {
		final List<ProjectMetrics> activeProjectsMetrics = statistic.getActiveProjectsMetrics();
		if (activeProjectsMetrics == null) return;
		for (final ProjectMetrics metrics : activeProjectsMetrics) {
			final String projectName = metrics.getProjectName();
			if (projects.containsKey(projectName)) projects.get(projectName).removeFromParent();

			final ProjectMetricsWidget widget = new ProjectMetricsWidget(metrics);
			projects.put(projectName, widget);
			projectsPanel.add(widget);
		}
	}

	private void updateActionsRatioChart(final OnTrackServerMetrics statistic) {
		actionsRatioChart.removeAllSeries();
		final Series series = actionsRatioChart.createSeries();
		final Map<String, Integer> actionsMap = statistic.getActionsRatio();
		if (actionsMap == null) return;

		final ArrayList<Entry<String, Integer>> orderedList = new ArrayList<Map.Entry<String, Integer>>(actionsMap.entrySet());
		Collections.sort(orderedList, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		int sumOfOthers = 0;
		for (int i = 0; i < orderedList.size(); i++) {
			final Entry<String, Integer> e = orderedList.get(i);
			if (i <= MAX_ACTIONS) series.addPoint(new Point(e.getKey(), e.getValue()));
			else sumOfOthers += e.getValue();
		}
		if (sumOfOthers > 0) series.addPoint(new Point((orderedList.size() - MAX_ACTIONS) + " other actions", sumOfOthers));
		actionsRatioChart.addSeries(series);
	}

	private void update() {
		setOptionsEnabled(false);
		ClientServices.get().metrics().getMetrics(new AsyncCallback<OnTrackServerMetrics>() {

			@Override
			public void onSuccess(final OnTrackServerMetrics statistic) {
				updateView(statistic);
				setOptionsEnabled(true);
				final List<OnTrackServerMetrics> list = metricsList.getStatisticsList();
				final int size = list.size();
				if (size > 1 && hasSameAttributes(list.get(size - 1), statistic) && hasSameAttributes(list.get(size - 2), statistic)) {
					list.remove(size - 1);
				}
				list.add(statistic);
				storeStatistics();
			}

			private boolean hasSameAttributes(final OnTrackServerMetrics o1, final OnTrackServerMetrics o2) {
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
				ClientServices.get().alerting().showError(caught.getLocalizedMessage());
			}
		});
	}

	private void initCharts() {
		createClientsChart();
		createUsageChart();
		createActionsRatioChart();

		final Iterator<OnTrackServerMetrics> it = metricsList.getStatisticsList().iterator();
		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (!it.hasNext()) {
					update();

					autoUpdateTimer.cancel();
					autoUpdateTimer.scheduleRepeating((int) (autoUpdateInterval * SECOND));
					autoUpdateIntervalTextBox.setText("" + autoUpdateInterval);

					return false;
				}

				updateView(it.next());
				return true;
			}
		});
	}

	private void createClientsChart() {
		clientsChart = new Chart().setChartTitleText("Clients").setLegend(new Legend().setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP).setFloating(true))
				.setSeriesPlotOptions(new SeriesPlotOptions().setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
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
				})).setToolTip(new ToolTip().setShared(true).setCrosshairs(true).setFormatter(new ToolTipFormatter() {
					@Override
					public String format(final ToolTipData toolTipData) {
						final long timestamp = toolTipData.getXAsLong();
						final OnTrackServerMetrics statistic = metricsCache.get(timestamp);
						final Set<String> onlineUsers = statistic.getOnlineUsers();
						String toolTip = formatTime(timestamp) + "<br/><b style=\"color: " + ACTIVE_CONNECTIONS_COLOR + ";\">Active Connections:</b> " + statistic.getActiveConnectionsCount()
								+ "<br/><b style=\"color: " + ONLINE_USERS_COLOR + ";\">Online Users:</b> " + onlineUsers.size();

						for (final String userIdAsString : onlineUsers) {
							toolTip += "<br/>" + usersCache.get(userIdAsString).getEmail();
						}

						return toolTip;
					}

				}));

		clientsChart.getXAxis().setAxisTitle(null).setType(Type.DATE_TIME);

		clientsChart.getYAxis().setAxisTitle(null).setMin(0).setAllowDecimals(false);

		onlineUsersSeries = clientsChart.createSeries().setName("Online Users")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ONLINE_USERS_COLOR).setLineWidth(1));

		activeConnectionsSeries = clientsChart.createSeries().setName("Active Connections")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ACTIVE_CONNECTIONS_COLOR).setLineWidth(1));

		clientsChart.addSeries(activeConnectionsSeries);
		clientsChart.addSeries(onlineUsersSeries);

		onlineUsersPanel.setWidget(clientsChart);
	}

	private void createUsageChart() {
		usageChart = new Chart().setChartTitleText("Server Usage").setLegend(new Legend().setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP).setFloating(true))
				.setSeriesPlotOptions(new SeriesPlotOptions().setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
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
				})).setToolTip(new ToolTip().setShared(true).setCrosshairs(true).setFormatter(new ToolTipFormatter() {
					@Override
					public String format(final ToolTipData toolTipData) {
						final long timestamp = toolTipData.getXAsLong();
						final OnTrackServerMetrics statistic = metricsCache.get(timestamp);
						final String toolTip = formatTime(timestamp) + "<br/><b style=\"color: " + ACTIONS_PER_HOUR_COLOR + ";\">Actions / Hour:</b> " + statistic.getActionsPerHour()
								+ "<br/><b style=\"color: " + USERS_COUNT_COLOR + ";\">Users Count:</b> " + statistic.getUsersCount() + "<br/><b style=\"color: " + PROJECTS_COUNT_COLOR
								+ ";\">Projects Count:</b> " + statistic.getProjectsCount();
						return toolTip;
					}

				}));

		usageChart.getXAxis().setAxisTitle(null).setType(Type.DATE_TIME);

		usageChart.getYAxis().setAxisTitle(null).setMin(0).setAllowDecimals(false);

		actionsPerHourSeries = usageChart.createSeries().setName("Actions / Hour")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ACTIONS_PER_HOUR_COLOR).setLineWidth(1));

		usersCountSeries = usageChart.createSeries().setName("Total Users").setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(USERS_COUNT_COLOR).setLineWidth(1));

		projectsCountSeries = usageChart.createSeries().setName("Total Users")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(PROJECTS_COUNT_COLOR).setLineWidth(1));

		usageChart.addSeries(actionsPerHourSeries);
		usageChart.addSeries(usersCountSeries);
		usageChart.addSeries(projectsCountSeries);

		usagePanel.setWidget(usageChart);
	}

	private void createActionsRatioChart() {
		actionsRatioChart = new Chart().setType(Series.Type.PIE).setChartTitleText("Actions")
				.setPiePlotOptions(new PiePlotOptions().setPieDataLabels(new PieDataLabels().setConnectorColor("#000000").setEnabled(true).setColor("#000000").setFormatter(new DataLabelsFormatter() {
					@Override
					public String format(final DataLabelsData dataLabelsData) {
						return "<b>" + dataLabelsData.getPointName() + "</b>: " + dataLabelsData.getYAsLong() + " actions";
					}
				}))).setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
					@Override
					public String format(final ToolTipData data) {
						return "<b>" + data.getPointName() + "</b>: " + ClientDecimalFormat.roundFloat((float) data.getPercentage(), 1) + "%";
					}
				}));
		actionsPanel.setWidget(actionsRatioChart);
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

	private Point getOnlineUsersPoint(final OnTrackServerMetrics statistic) {
		return createPoint(statistic, statistic.getOnlineUsers().size());
	}

	private Point getActiveConnectionsPoint(final OnTrackServerMetrics statistic) {
		return createPoint(statistic, statistic.getActiveConnectionsCount());
	}

	private Point getActionsPerHourPoint(final OnTrackServerMetrics statistic) {
		return createPoint(statistic, statistic.getActionsPerHour());
	}

	private Point getUsersCountPoint(final OnTrackServerMetrics statistic) {
		return createPoint(statistic, statistic.getUsersCount());
	}

	private Point getProjectsCountPoint(final OnTrackServerMetrics statistic) {
		return createPoint(statistic, statistic.getProjectsCount());
	}

	private Point createPoint(final OnTrackServerMetrics statistic, final Number number) {
		return new Point(statistic.getTimestamp().getTime(), number);
	}

	private String formatTime(final long time) {
		return dateTimeFormat.format(new Date(time));
	}

	private void storeStatistics() {
		ClientServices.get().storage().appendOnTrackServerMetrics(metricsList);
	}

}
