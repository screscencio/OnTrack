package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.PlotLine.DashStyle;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.Style;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.DataLabels;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsFormatter;
import org.moxieapps.gwt.highcharts.client.labels.Labels;
import org.moxieapps.gwt.highcharts.client.labels.Labels.Align;
import org.moxieapps.gwt.highcharts.client.labels.XAxisLabels;
import org.moxieapps.gwt.highcharts.client.labels.YAxisLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker.Symbol;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseChart extends Composite {

	private static final ReleaseChartMessages messages = GWT.create(ReleaseChartMessages.class);

	private static final String IDEAL_EFFORT_COLOR = "#666666";

	private static final String VALUE_COLOR = "#00AA00";

	private static final String EFFORT_COLOR = "#AA0000";

	private static final String SERIES_IDEAL_BURN_UP_LINE = messages.declaredIdealLine();

	private static final String SERIES_ACCOMPLISHED_EFFORT = messages.effort();

	private static final String SERIES_ACCOMPLISHED_VALUE = messages.value();

	private static final int MAX_NUMBER_OF_TICKS = 22;

	private static final String SERIES_INFERED_IDEAL_BURN_UP_LINE = messages.inferedIdealLine();

	private static final String INFERED_IDEAL_EFFORT_COLOR = "#bbc2f2";

	private static ReleaseChartUiBinder uiBinder = GWT.create(ReleaseChartUiBinder.class);

	interface ReleaseChartUiBinder extends UiBinder<Widget, ReleaseChart> {}

	@UiField
	FocusPanel chartPanel;

	protected Chart chart;

	private ReleaseChartDataProvider dataProvider;

	private ActionExecutionListener actionExecutionListener;

	private final boolean completeMode;

	private ReleaseChartUpdateListener updateListener;

	private Release release;

	private Series accomplishedEffortSeries;

	private Series accomplishedValueSeries;

	private float previousEffort;

	private float previousValue;

	public ReleaseChart(final boolean completeMode) {
		this.completeMode = completeMode;
		initWidget(uiBinder.createAndBindUi(this));
		chart = createBasicChart();
		chartPanel.add(chart);
	}

	public ReleaseChart(final Release release, final boolean completeMode) {
		this.completeMode = completeMode;
		setRelease(release);

		initWidget(uiBinder.createAndBindUi(this));
		chart = createBasicChart();
		chartPanel.add(chart);
	}

	public void setRelease(final Release release) {
		setRelease(release, new ReleaseChartDataProvider(release, ClientServices.get().releaseEstimator().get(), ClientServices.get().actionExecution()));
	}

	public void setRelease(final Release release, final ReleaseChartDataProvider dataProvider) {
		this.release = release;
		this.dataProvider = dataProvider;
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
		updateSize();
	}

	@Override
	protected void onDetach() {
		if (actionExecutionListener != null) getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());

		super.onDetach();
	}

	public void updateData() {
		chart.removeAllSeries();

		dataProvider.evaluateData();
		updateAccomplishedAmounts();
		final List<WorkingDay> releaseDays = dataProvider.getReleaseDays();
		configureXAxis(releaseDays);
		accomplishedEffortSeries = createBurnUpLine(releaseDays, dataProvider.getAccomplishedEffortPointsByDate());
		chart.addSeries(accomplishedEffortSeries, false, false);
		accomplishedValueSeries = createValueLine(releaseDays, dataProvider.getAccomplishedValuePointsByDate());
		chart.addSeries(accomplishedValueSeries, false, false);
		if (dataProvider.getValueSum() == 0f) accomplishedValueSeries.hide();
		chart.addSeries(createIdealLine(SERIES_IDEAL_BURN_UP_LINE, IDEAL_EFFORT_COLOR, DashStyle.SHORT_DASH, releaseDays, dataProvider.getEstimatedEndDay(), dataProvider.getEffortSum()), false, false);
		if (completeMode && !dataProvider.getEstimatedEndDay().equals(dataProvider.getInferedEstimatedEndDay())) {
			final Series inferedIdealLine = createIdealLine(SERIES_INFERED_IDEAL_BURN_UP_LINE, INFERED_IDEAL_EFFORT_COLOR, DashStyle.LONG_DASH, releaseDays, dataProvider.getInferedEstimatedEndDay(),
					dataProvider.getEffortSum());
			chart.addSeries(inferedIdealLine, false, false);
			inferedIdealLine.hide();
		}

		chart.redraw();

		if (updateListener != null) updateListener.onUpdate(dataProvider);
	}

	private void updateAccomplishedAmounts() {
		previousEffort = release.getAccomplishedEffortSum();
		previousValue = release.getAccomplishedValueSum();
	}

	private Chart createBasicChart() {
		final Chart newChart = new Chart().setAnimation(false).setType(Series.Type.LINE).setChartTitleText("")
				.setLegend(new Legend().setEnabled(completeMode).setAlign(Legend.Align.LEFT).setVerticalAlign(Legend.VerticalAlign.TOP).setY(-8).setFloating(true).setBorderWidth(0))
				.setMarginTop(completeMode ? 25 : 5).setMarginLeft(completeMode ? 30 : null).setMarginRight(completeMode ? 30 : null).setBorderRadius(0).setCredits(new Credits().setEnabled(false))
				.setSeriesPlotOptions(new SeriesPlotOptions().setCursor(PlotOptions.Cursor.POINTER).setMarker(new Marker().setLineWidth(1)))
				.setLinePlotOptions(new LinePlotOptions().setDataLabels(new DataLabels().setEnabled(true).setAlign(Align.RIGHT).setFormatter(new ShowOnlyLastPointFormatter())))
				.setToolTip(new ToolTip().setCrosshairs(true).setFormatter(new ToolTipFormatter() {
					@Override
					public String format(final ToolTipData toolTipData) {
						final boolean isValuePoints = toolTipData.getSeriesName().equals(SERIES_ACCOMPLISHED_VALUE);

						final float currentPoints = (float) toolTipData.getYAsDouble();
						final int currentDayCount = toolTipData.getPoint().getX().intValue() + 1;

						final String toolTipText = toolTipData.getXAsString() + "<br>" + round(currentPoints) + " " + (isValuePoints ? messages.valuePoints() : messages.effortPoints()) + "<br>"
								+ messages.velocity() + round(currentPoints / currentDayCount) + " " + (isValuePoints ? "vp" : "ep") + "/" + messages.day();
						return toolTipText;
					}

				}));

		newChart.getXAxis(0).setType(Type.LINEAR).setTickWidth(0).setLabels(new XAxisLabels().setEnabled(completeMode).setAlign(Align.CENTER).setRotation(-45).setX(-16).setY(27));

		newChart.getYAxis(0)
				.setGridLineWidth(completeMode ? 1 : 0)
				.setAxisTitleText("")
				.setShowFirstLabel(false)
				.setAllowDecimals(false)
				.setShowLastLabel(true)
				.setOpposite(true)
				.setMin(0)
				.setLabels(
						new YAxisLabels().setAlign(Labels.Align.RIGHT).setEnabled(completeMode).setX(27).setY(13).setStyle(new Style().setColor(EFFORT_COLOR).setFontWeight("bold"))
								.setFormatter(new AxisLabelsFormatter() {
									@Override
									public String format(final AxisLabelsData axisLabelsData) {
										return round((float) axisLabelsData.getValueAsDouble());
									}
								}));

		newChart.getYAxis(1)
				.setAxisTitleText("")
				.setShowFirstLabel(false)
				.setShowLastLabel(true)
				.setGridLineWidth(0)
				.setMin(0)
				.setLabels(
						new YAxisLabels().setEnabled(completeMode).setAlign(Labels.Align.LEFT).setX(-28).setY(16).setStyle(new Style().setColor(VALUE_COLOR).setFontWeight("bold"))
								.setFormatter(new AxisLabelsFormatter() {
									@Override
									public String format(final AxisLabelsData axisLabelsData) {
										return round((float) axisLabelsData.getValueAsDouble());
									}
								}));

		return newChart;
	}

	private void configureXAxis(final List<WorkingDay> workingDays) {
		final int numberOfDays = workingDays.size();

		final String[] array = new String[numberOfDays];
		for (int i = 0; i < numberOfDays; i++)
			array[i] = workingDays.get(i).getDayMonthShortYearString();

		chart.getXAxis(0).setTickInterval(Math.ceil(((float) numberOfDays) / MAX_NUMBER_OF_TICKS)).setCategories(array);
	}

	private Series createBurnUpLine(final List<WorkingDay> releaseDays, final Map<WorkingDay, Float> values) {
		final Series newSerie = chart.createSeries().setName(SERIES_ACCOMPLISHED_EFFORT).setYAxis(0)
				.setPlotOptions(new LinePlotOptions().setLineWidth(2).setZIndex(2).setShadow(false).setColor(EFFORT_COLOR).setMarker(new Marker().setSymbol(Symbol.CIRCLE).setRadius(3)));

		return populateSeries(values, releaseDays, newSerie);
	}

	private Series createValueLine(final List<WorkingDay> releaseDays, final Map<WorkingDay, Float> values) {
		final Series newSerie = chart
				.createSeries()
				.setName(SERIES_ACCOMPLISHED_VALUE)
				.setYAxis(1)
				.setPlotOptions(
						new LinePlotOptions().setLineWidth(1).setShadow(false).setZIndex(1).setColor(VALUE_COLOR)
								.setMarker(new Marker().setSymbol(Symbol.DIAMOND).setLineColor(VALUE_COLOR).setRadius(2)));

		return populateSeries(values, releaseDays, newSerie);
	}

	private Series createIdealLine(final String seriesName, final String seriesColor, final DashStyle dashStyle, final List<WorkingDay> releaseDays, final WorkingDay estimatedEndDay,
			final float maxValue) {
		final Series idealLine = chart
				.createSeries()
				.setName(seriesName)
				.setYAxis(0)
				.setPlotOptions(
						new LinePlotOptions().setLineWidth(1).setEnableMouseTracking(true).setHoverStateEnabled(false).setShadow(false).setZIndex(0).setDashStyle(dashStyle).setColor(seriesColor)
								.setMarker(new Marker().setSymbol(Symbol.CIRCLE).setRadius(1)))

				.addPoint(0, 0).addPoint(createLastPoint(releaseDays.indexOf(estimatedEndDay), maxValue, seriesName));

		return idealLine;
	}

	private Series populateSeries(final Map<WorkingDay, Float> values, final List<WorkingDay> releaseDays, final Series serie) {
		if (values.isEmpty()) return serie;

		serie.setPoints(createPoints(values, releaseDays, serie));

		return serie;
	}

	private Point[] createPoints(final Map<WorkingDay, Float> values, final List<WorkingDay> releaseDays, final Series serie) {
		final List<Point> points = new ArrayList<Point>();

		points.add(new Point(0, 0));

		WorkingDay lastDayAccounted = null;
		for (final WorkingDay workingDay : values.keySet()) {
			lastDayAccounted = workingDay;
			points.add(new Point(releaseDays.indexOf(workingDay), values.get(workingDay)));
		}
		if (lastDayAccounted != null) {
			points.add(createLastPoint(releaseDays.indexOf(lastDayAccounted), values.get(lastDayAccounted), serie.getOptions().get("name").toString()));
		}
		return points.toArray(new Point[points.size()]);
	}

	private Point createLastPoint(final Number x, final Number y, final String name) {
		return new Point(x, y).setName(name);
	}

	private boolean isLastPoint(final DataLabelsData dataLabelsData) {
		return dataLabelsData.getPointName() != null;
	}

	private class ShowOnlyLastPointFormatter implements DataLabelsFormatter {
		@Override
		public String format(final DataLabelsData dataLabelsData) {
			return !isLastPoint(dataLabelsData) ? null : String.valueOf(round((float) dataLabelsData.getYAsDouble()));
		}
	}

	private String round(final float currentPoints) {
		return ClientDecimalFormat.roundFloat(currentPoints, 1);
	}

	private ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener == null ? actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
				final ModelAction action = execution.getModelAction();
				if (action instanceof ReleaseDeclareStartDayAction || action instanceof ReleaseDeclareEndDayAction || action instanceof ReleaseDeclareEstimatedVelocityAction) updateData();

				else updateAmountSeries();
			}

			private void updateAmountSeries() {
				final float accomplishedEffortSum = release.getAccomplishedEffortSum();
				final float accomplishedValueSum = release.getAccomplishedValueSum();

				if (previousEffort == accomplishedEffortSum && previousValue == accomplishedValueSum) return;

				dataProvider.evaluateData();
				if (previousEffort != accomplishedEffortSum) updateEffortSeries();
				if (previousValue != accomplishedValueSum) updateValueSeries();

				chart.redraw();
			}

			private void updateValueSeries() {
				accomplishedValueSeries.remove();
				chart.addSeries(accomplishedValueSeries = createValueLine(dataProvider.getReleaseDays(), dataProvider.getAccomplishedValuePointsByDate()), false, false);
				previousValue = release.getAccomplishedValueSum();
			}

			private void updateEffortSeries() {
				accomplishedEffortSeries.remove();
				chart.addSeries(accomplishedEffortSeries = createBurnUpLine(dataProvider.getReleaseDays(), dataProvider.getAccomplishedEffortPointsByDate()), false, false);
				previousEffort = release.getAccomplishedEffortSum();
			}

		} : actionExecutionListener;
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServices.get().actionExecution();
	}

	public ReleaseChart setUpdateListener(final ReleaseChartUpdateListener listener) {
		this.updateListener = listener;
		return this;
	}

	public interface ReleaseChartUpdateListener {

		void onUpdate(ReleaseChartDataProvider dataProvider);

	}

	public void declareStartDate(final Date date) {
		dataProvider.declareStartDate(date);
	}

	public void declareEndDate(final Date date) {
		dataProvider.declareEndDate(date);
	}

	public void declareEstimatedVelocity(final Float velocity) {
		dataProvider.declareEstimatedVelocity(velocity);
	}

	public void updateSize() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				chart.setSize(chartPanel.getElement().getClientWidth(), chartPanel.getElement().getClientHeight(), false);
			}
		});
	}

	public void highlight(final WorkingDay endDay) {
		int index = -1;
		for (final WorkingDay day : dataProvider.getReleaseDays()) {
			if (day.isBeforeOrSameDayOf(endDay)) index++;
			else break;
		}
		if (index == -1) return;
		chart.refreshTooltip(0, dataProvider.getReleaseDays().contains(endDay) ? index : index + 1);
	}

}
