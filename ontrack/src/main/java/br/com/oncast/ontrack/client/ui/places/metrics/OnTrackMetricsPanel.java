package br.com.oncast.ontrack.client.ui.places.metrics;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.places.metrics.widgets.ProjectMetricsWidget;
import br.com.oncast.ontrack.client.utils.date.DateUnit;
import br.com.oncast.ontrack.client.utils.date.DateUtils;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetricsBag;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatisticsBag;
import br.com.oncast.ontrack.shared.services.metrics.ProjectMetrics;
import br.com.oncast.ontrack.shared.services.metrics.UserUsageData;

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
import org.moxieapps.gwt.highcharts.client.AxisTitle;
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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OnTrackMetricsPanel extends Composite {

	private static final int MAX_ACTIONS = 10;

	private static final String PROJECTS_COUNT_COLOR = "#CCCC44";

	private static final String USERS_COUNT_COLOR = "#CC44CC";

	private static final String ACTIONS_COUNT_COLOR = "#44CC44";

	private static final String ACTIVE_CONNECTIONS_COLOR = "#ff4444";

	private static final String ONLINE_USERS_COLOR = "#4444ff";

	private static final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy/MM/dd - HH:mm:ss");

	private static final int SECOND = 1000;

	private static final long ACTIVE_USER_DELTA = 10 * DateUnit.DAY;

	private static final long NEVER_USED_ACTIONS_COUNT = 50;

	private static final int NUMBER_OF_ACTIONS_SENCITIVITY = 100;

	private static OnTrackStatisticsPanelUiBinder uiBinder = GWT.create(OnTrackStatisticsPanelUiBinder.class);

	interface OnTrackStatisticsPanelUiBinder extends UiBinder<Widget, OnTrackMetricsPanel> {}

	@UiField
	TextBox autoUpdateIntervalTextBox;

	@UiField
	Button updateRealtimeMetricsButton;

	@UiField
	Button updateServerStatisticsButton;

	@UiField
	Button wipeLocalData;

	@UiField
	Anchor exportUsageDataCsv;

	@UiField
	Anchor exportInvitationDataCsv;

	@UiField
	FocusPanel onlineUsersPanel;

	@UiField
	FocusPanel serverUsagePanel;

	@UiField
	FocusPanel usersUsagePanel;

	@UiField
	FocusPanel actionsPanel;

	@UiField
	FlowPanel projectsPanel;

	@UiField
	SuggestBox actionCountSuggestBox;

	@UiField
	HTMLPanel actionCountResultsPanel;

	@UiField
	InlineLabel activeUsersCount;

	@UiField
	InlineLabel neverUsedUsersCount;

	@UiField
	InlineLabel stopedUsingUsersCount;

	private Series onlineUsersSeries;

	private Series activeConnectionsSeries;

	private Series actionsCountSeries;

	private Series usersCountSeries;

	private Series projectsCountSeries;

	private final OnTrackRealTimeServerMetricsBag metricsList;

	private final OnTrackServerStatisticsBag statisticsList;

	private final Map<Long, OnTrackRealTimeServerMetrics> realTimeMetricsCache;

	private final Map<Long, OnTrackServerStatistics> statisticsCache;

	private final Map<String, User> usersCache;

	private final Map<String, ProjectMetricsWidget> projects;

	private final Timer autoUpdateTimer = new Timer() {
		@Override
		public void run() {
			updateRealTimeMetrics();
		}
	};

	private float autoUpdateInterval = 30;

	private Chart serverUsageChart;

	private Chart usersUsageChart;

	private Chart currentlyChart;

	private Chart actionsRatioChart;

	private Date lastRealTimeMetricsUpdate;

	public OnTrackMetricsPanel() {
		projects = new HashMap<String, ProjectMetricsWidget>();
		realTimeMetricsCache = new HashMap<Long, OnTrackRealTimeServerMetrics>();
		statisticsCache = new HashMap<Long, OnTrackServerStatistics>();
		usersCache = new HashMap<String, User>();
		metricsList = ClientServices.get().storage().loadOnTrackRealTimeServerMetricsList();
		statisticsList = ClientServices.get().storage().loadOnTrackServerStatisticsList();
		initWidget(uiBinder.createAndBindUi(this));

		exportUsageDataCsv.setHref(GWT.getModuleBaseURL() + "metrics/usage/download");

		exportInvitationDataCsv.setHref(GWT.getModuleBaseURL() + "metrics/invitation/download");

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
		realTimeMetricsCache.clear();
		statisticsCache.clear();
		metricsList.getOnTrackRealTimeServerMetricsList().clear();
		statisticsList.getOnTrackServerStatisticsList().clear();
		projects.clear();
		projectsPanel.clear();
		storeStatistics();

		initCharts();
	}

	@UiHandler("updateRealtimeMetricsButton")
	void onUpdateRealtimeMetricsClick(final ClickEvent e) {
		updateRealTimeMetrics();
	}

	@UiHandler("updateServerStatisticsButton")
	void onUpdateServerStatisticsClick(final ClickEvent e) {
		updateServerStatistics();
	}

	@UiHandler("actionCountSuggestBox")
	void onActionSelected(final SelectionEvent<Suggestion> event) {
		actionCountResultsPanel.add(new Label(event.getSelectedItem().getReplacementString()));
		actionCountSuggestBox.setValue("", false);
	}

	private void updateView(final OnTrackRealTimeServerMetrics statistic) {
		onlineUsersSeries.addPoint(getOnlineUsersPoint(statistic));
		activeConnectionsSeries.addPoint(getActiveConnectionsPoint(statistic));
		actionsCountSeries.addPoint(getActionsCountPoint(statistic));

		lastRealTimeMetricsUpdate = statistic.getTimestamp();
		realTimeMetricsCache.put(lastRealTimeMetricsUpdate.getTime(), statistic);

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

	private void updateView(final OnTrackServerStatistics statistics) {
		usersCountSeries.addPoint(getUsersCountPoint(statistics));
		projectsCountSeries.addPoint(getProjectsCountPoint(statistics));

		updateActionsRatioChart(statistics);
		updateProjectsStatistics(statistics);
		statisticsCache.put(statistics.getTimestamp().getTime(), statistics);

		final Date now = new Date();
		final int totalUsersCount = statistics.getTotalUsersCount();
		int auCount = 0;
		int neverUsedCount = 0;
		int stopedUsing = 0;
		for (final UserUsageData data : statistics.getUsersUsageDataList()) {
			final Date lastActionTimestamp = data.getLastActionTimestamp();
			if (lastActionTimestamp != null && DateUtils.getDifferenceInMilliseconds(lastActionTimestamp, now) < ACTIVE_USER_DELTA) auCount++;
			else if (data.getSubmittedActionsCount() < NEVER_USED_ACTIONS_COUNT) neverUsedCount++;
			else stopedUsing++;

		}
		setUsersCountAndPercentage(activeUsersCount, auCount, totalUsersCount);
		setUsersCountAndPercentage(neverUsedUsersCount, neverUsedCount, totalUsersCount);
		setUsersCountAndPercentage(stopedUsingUsersCount, stopedUsing, totalUsersCount);

		updateUsersUsageChart(statistics);
	}

	private void updateUsersUsageChart(final OnTrackServerStatistics statistics) {
		usersUsageChart.removeAllSeries();
		final Series actionsCountSeries = usersUsageChart.createSeries().setName("Number of Actions (x" + NUMBER_OF_ACTIONS_SENCITIVITY + ")").setXAxis(0)
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(USERS_COUNT_COLOR).setLineWidth(1));

		final Series usedPeriod = usersUsageChart.createSeries().setName("Duration (weeks)").setXAxis(1)
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(PROJECTS_COUNT_COLOR).setLineWidth(1));

		final HashMap<Long, Integer> actionsCountMap = new HashMap<Long, Integer>();
		final HashMap<Integer, Integer> durationMap = new HashMap<Integer, Integer>();
		final Date now = new Date();
		for (final UserUsageData data : statistics.getUsersUsageDataList()) {
			final Date lastActionTimestamp = data.getLastActionTimestamp();
			if (lastActionTimestamp != null && DateUtils.getDifferenceInMilliseconds(lastActionTimestamp, now) < ACTIVE_USER_DELTA) continue;

			final long submittedActionsCount = data.getSubmittedActionsCount() / NUMBER_OF_ACTIONS_SENCITIVITY;
			increment(actionsCountMap, submittedActionsCount);

			final int durationInDays = lastActionTimestamp == null ? 0 : getDurationInWeeks(data);
			increment(durationMap, durationInDays);
		}

		for (final Long actionsCount : getSortedKeys(actionsCountMap)) {
			actionsCountSeries.addPoint(actionsCount, actionsCountMap.get(actionsCount));
		}

		for (final Integer duration : getSortedKeys(durationMap)) {
			usedPeriod.addPoint(duration, durationMap.get(duration));
		}

		usersUsageChart.addSeries(actionsCountSeries);
		usersUsageChart.addSeries(usedPeriod);
	}

	private <T extends Comparable<T>> List<T> getSortedKeys(final Map<T, ?> map) {
		final ArrayList<T> list = new ArrayList<T>(map.keySet());
		Collections.sort(list);
		return list;
	}

	private <T> void increment(final HashMap<T, Integer> map, final T key) {
		if (!map.containsKey(key)) map.put(key, 1);
		else map.put(key, map.get(key) + 1);
	}

	private int getDurationInWeeks(final UserUsageData data) {
		return (int) ((DateUtils.getDifferenceInMilliseconds(data.getInvitationTimestamp(), data.getLastActionTimestamp()) + 0.99) / DateUnit.WEEK);
	}

	private void setUsersCountAndPercentage(final HasText widget, final int count, final int total) {
		widget.setText("" + count + " (" + (count * 100 / total) + "%)");
	}

	private void updateProjectsStatistics(final OnTrackServerStatistics statistic) {
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

	private void updateActionsRatioChart(final OnTrackServerStatistics statistic) {
		actionsRatioChart.removeAllSeries();
		final MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) actionCountSuggestBox.getSuggestOracle();
		oracle.clear();
		final Series series = actionsRatioChart.createSeries();
		final Map<String, Integer> actionsCountMap = statistic.getActionsRatio();
		if (actionsCountMap == null) return;

		for (final Entry<String, Integer> e : actionsCountMap.entrySet()) {
			oracle.add(e.getKey() + ": " + e.getValue());
		}
		actionCountSuggestBox.refreshSuggestionList();
		actionCountResultsPanel.clear();
		actionCountSuggestBox.setValue("", false);

		final ArrayList<Entry<String, Integer>> orderedList = new ArrayList<Map.Entry<String, Integer>>(actionsCountMap.entrySet());
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

	private void updateRealTimeMetrics() {
		setOptionsEnabled(false);
		ClientServices.get().metrics().getRealTimeMetrics(lastRealTimeMetricsUpdate, new AsyncCallback<OnTrackRealTimeServerMetrics>() {

			@Override
			public void onSuccess(final OnTrackRealTimeServerMetrics statistic) {
				updateView(statistic);
				setOptionsEnabled(true);
				final List<OnTrackRealTimeServerMetrics> list = metricsList.getOnTrackRealTimeServerMetricsList();
				final int size = list.size();
				if (size > 1 && hasSameAttributes(list.get(size - 1), statistic) && hasSameAttributes(list.get(size - 2), statistic)) {
					list.remove(size - 1);
				}
				list.add(statistic);
				storeStatistics();
			}

			private boolean hasSameAttributes(final OnTrackRealTimeServerMetrics o1, final OnTrackRealTimeServerMetrics o2) {
				boolean equals = o1.getActiveConnectionsCount() == o2.getActiveConnectionsCount();
				equals &= o1.getOnlineUsers().size() == o2.getOnlineUsers().size();
				equals &= o1.getOnlineUsers().containsAll(o2.getOnlineUsers());
				equals &= o1.getActionsCount() == o2.getActionsCount();
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

	private void updateServerStatistics() {
		setOptionsEnabled(false);
		ClientServices.get().metrics().getServerStatistics(new AsyncCallback<OnTrackServerStatistics>() {

			@Override
			public void onSuccess(final OnTrackServerStatistics statistic) {
				updateView(statistic);
				setOptionsEnabled(true);

				final List<OnTrackServerStatistics> list = statisticsList.getOnTrackServerStatisticsList();
				final int size = list.size();
				if (size > 1 && hasSameAttributes(list.get(size - 1), statistic) && hasSameAttributes(list.get(size - 2), statistic)) {
					list.remove(size - 1);
				}
				list.add(statistic);
				storeStatistics();
			}

			private boolean hasSameAttributes(final OnTrackServerStatistics o1, final OnTrackServerStatistics o2) {
				boolean equals = o1.getActionsCount() == o2.getActionsCount();
				equals &= o1.getTotalUsersCount() == o2.getTotalUsersCount();
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
		createServerUsageChart();
		createUsersUsageChart();
		createActionsRatioChart();

		final Iterator<OnTrackRealTimeServerMetrics> it = metricsList.getOnTrackRealTimeServerMetricsList().iterator();
		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (!it.hasNext()) {
					updateRealTimeMetrics();

					autoUpdateTimer.cancel();
					autoUpdateTimer.scheduleRepeating((int) (autoUpdateInterval * SECOND));
					autoUpdateIntervalTextBox.setText("" + autoUpdateInterval);

					return false;
				}

				updateView(it.next());
				return true;
			}
		});

		final Iterator<OnTrackServerStatistics> it2 = statisticsList.getOnTrackServerStatisticsList().iterator();
		Scheduler.get().scheduleIncremental(new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (it2.hasNext()) updateView(it2.next());
				return it2.hasNext();
			}
		});
	}

	private void createClientsChart() {
		currentlyChart = new Chart().setChartTitleText("Currently").setLegend(new Legend().setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP).setFloating(true))
				.setSeriesPlotOptions(new SeriesPlotOptions().setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
					@Override
					public boolean onMouseOver(final PointMouseOverEvent pointMouseOverEvent) {
						final Point[] points = currentlyChart.getSeries(pointMouseOverEvent.getSeriesId()).getPoints();
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
						final OnTrackRealTimeServerMetrics statistic = realTimeMetricsCache.get(timestamp);
						final Set<String> onlineUsers = statistic.getOnlineUsers();
						String toolTip = formatTime(timestamp) + "<br/><b style=\"color: " + ACTIONS_COUNT_COLOR + ";\">Actions:</b> " + statistic.getActionsCount() + "<br/><b style=\"color: "
								+ ACTIVE_CONNECTIONS_COLOR + ";\">Active Connections:</b> " + statistic.getActiveConnectionsCount() + "<br/><b style=\"color: " + ONLINE_USERS_COLOR
								+ ";\">Online Users:</b> " + onlineUsers.size();

						for (final String userIdAsString : onlineUsers) {
							toolTip += "<br/>" + usersCache.get(userIdAsString).getEmail();
						}

						return toolTip;
					}

				}));

		currentlyChart.getXAxis().setAxisTitle(null).setType(Type.DATE_TIME);

		currentlyChart.getYAxis().setAxisTitle(null).setMin(0).setAllowDecimals(false);

		onlineUsersSeries = currentlyChart.createSeries().setName("Online Users")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ONLINE_USERS_COLOR).setLineWidth(1));

		activeConnectionsSeries = currentlyChart.createSeries().setName("Active Connections")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ACTIVE_CONNECTIONS_COLOR).setLineWidth(1));

		actionsCountSeries = currentlyChart.createSeries().setName("Actions")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(ACTIONS_COUNT_COLOR).setLineWidth(1));

		currentlyChart.addSeries(actionsCountSeries);
		currentlyChart.addSeries(activeConnectionsSeries);
		currentlyChart.addSeries(onlineUsersSeries);

		onlineUsersPanel.setWidget(currentlyChart);
	}

	private void createServerUsageChart() {
		serverUsageChart = new Chart().setChartTitleText("Total").setLegend(new Legend().setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP).setFloating(true))
				.setSeriesPlotOptions(new SeriesPlotOptions().setPointMouseOverEventHandler(new PointMouseOverEventHandler() {
					@Override
					public boolean onMouseOver(final PointMouseOverEvent pointMouseOverEvent) {
						final Point[] points = serverUsageChart.getSeries(pointMouseOverEvent.getSeriesId()).getPoints();
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
						final OnTrackServerStatistics statistic = statisticsCache.get(timestamp);
						final String toolTip = formatTime(timestamp) + "<br/><b style=\"color: " + USERS_COUNT_COLOR + ";\">Users Count:</b> " + statistic.getTotalUsersCount()
								+ "<br/><b style=\"color: " + PROJECTS_COUNT_COLOR + ";\">Projects Count:</b> " + statistic.getTotalProjectsCount();
						return toolTip;
					}

				}));

		serverUsageChart.getXAxis().setAxisTitle(null).setType(Type.DATE_TIME);

		serverUsageChart.getYAxis().setAxisTitle(null).setMin(0).setAllowDecimals(false);

		usersCountSeries = serverUsageChart.createSeries().setName("Total Users")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(USERS_COUNT_COLOR).setLineWidth(1));

		projectsCountSeries = serverUsageChart.createSeries().setName("Total Users")
				.setPlotOptions(new LinePlotOptions().setMarker(new Marker().setEnabled(false)).setColor(PROJECTS_COUNT_COLOR).setLineWidth(1));

		serverUsageChart.addSeries(usersCountSeries);
		serverUsageChart.addSeries(projectsCountSeries);

		serverUsagePanel.setWidget(serverUsageChart);
	}

	private void createUsersUsageChart() {
		usersUsageChart = new Chart().setChartTitleText("User usage before leaving").setLegend(new Legend().setAlign(Align.RIGHT).setVerticalAlign(VerticalAlign.TOP).setFloating(true))
				.setType(Series.Type.SPLINE).setToolTip(new ToolTip().setShared(false).setCrosshairs(false).setFormatter(new ToolTipFormatter() {
					@Override
					public String format(final ToolTipData toolTipData) {
						final long actionsCount = toolTipData.getXAsLong();
						return "<b>Number of Users:</b>" + toolTipData.getYAsLong() + "<br/><b>" + toolTipData.getSeriesName() + ":</b> " + actionsCount;
					}
				}));

		usersUsageChart.getXAxis(0).setAxisTitle(new AxisTitle().setText("Submitted actions before leaving (x" + NUMBER_OF_ACTIONS_SENCITIVITY + ")")).setMin(0).setAllowDecimals(false);

		usersUsageChart.getXAxis(1).setAxisTitle(new AxisTitle().setText("Time before leaving (weeks)")).setMin(0).setAllowDecimals(false);

		usersUsageChart.getYAxis().setAxisTitle(new AxisTitle().setText("Number of users")).setMin(0).setAllowDecimals(false);

		usersUsagePanel.setWidget(usersUsageChart);
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
		serverUsageChart.refreshTooltip(0, pointIndex);
		currentlyChart.refreshTooltip(0, pointIndex);
	}

	private void setOptionsEnabled(final boolean enabled) {
		updateRealtimeMetricsButton.setEnabled(enabled);
		updateServerStatisticsButton.setEnabled(enabled);
		wipeLocalData.setEnabled(enabled);
		autoUpdateIntervalTextBox.setEnabled(enabled);
	}

	private Point getOnlineUsersPoint(final OnTrackRealTimeServerMetrics statistic) {
		return createPoint(statistic.getTimestamp(), statistic.getOnlineUsers().size());
	}

	private Point getActiveConnectionsPoint(final OnTrackRealTimeServerMetrics statistic) {
		return createPoint(statistic.getTimestamp(), statistic.getActiveConnectionsCount());
	}

	private Point getActionsCountPoint(final OnTrackRealTimeServerMetrics statistic) {
		return createPoint(statistic.getTimestamp(), statistic.getActionsCount());
	}

	private Point getUsersCountPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic.getTimestamp(), statistic.getTotalUsersCount());
	}

	private Point getProjectsCountPoint(final OnTrackServerStatistics statistic) {
		return createPoint(statistic.getTimestamp(), statistic.getTotalProjectsCount());
	}

	private Point createPoint(final Date timestamp, final Number number) {
		return new Point(timestamp.getTime(), number);
	}

	private String formatTime(final long time) {
		return dateTimeFormat.format(new Date(time));
	}

	private void storeStatistics() {
		ClientServices.get().storage().storeOnTrackRealTimeServerMetricsList(metricsList);
		ClientServices.get().storage().storeOnTrackServerStatisticsList(statisticsList);
	}

}
