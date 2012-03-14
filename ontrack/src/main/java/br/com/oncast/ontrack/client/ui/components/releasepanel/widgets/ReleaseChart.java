package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.List;
import java.util.Map;

import org.moxieapps.gwt.highcharts.client.Axis.Type;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.PlotLine;
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

import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseChart extends Composite implements HasCloseHandlers<ReleaseChart>, PopupAware {

	private static final String IDEAL_EFFORT_COLOR = "#r4r44r";

	private static final String VALUE_COLOR = "#00AA00";

	private static final String EFFORT_COLOR = "#AA0000";

	private static final String SERIES_IDEAL_BURN_UP_LINE = "Ideal Effort";

	private static final String SERIES_ACCOMPLISHED_EFFORT = "Effort";

	private static final String SERIES_ACCOMPLISHED_VALUE = "Value";

	private static final int MAX_NUMBER_OF_TICKS = 22;

	private static ChartPanelUiBinder uiBinder = GWT.create(ChartPanelUiBinder.class);

	interface ChartPanelUiBinder extends UiBinder<Widget, ReleaseChart> {}

	protected Chart chart;

	@UiField
	protected FocusPanel clickableChartPanel;

	@UiField
	protected Image closeIcon;

	private final ReleaseChartDataProvider dataProvider;

	public ReleaseChart(final ReleaseChartDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		initWidget(uiBinder.createAndBindUi(this));
		this.setVisible(false);

		chart = createBasicChart();

		clickableChartPanel.add(chart);
		configureXAxis(dataProvider.getReleaseDays());
	}

	@UiHandler("clickableChartPanel")
	public void onAttach(final AttachEvent event) {
		if (event.isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					setVisible(true);
					chart.setSize(clickableChartPanel.getElement().getClientWidth(), clickableChartPanel.getElement().getClientHeight(), false);
				}
			});
		}
	}

	@UiHandler("clickableChartPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (!(e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE)) return;

		hide();
		e.preventDefault();
		e.stopPropagation();
	}

	@UiHandler("closeIcon")
	protected void onClick(final ClickEvent event) {
		hide();
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ReleaseChart> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		updateData();
		clickableChartPanel.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		this.setVisible(false);
		chart.removeAllSeries();
		CloseEvent.fire(this, this);
	}

	public void updateData() {
		dataProvider.evaluateData();

		configureXAxis(dataProvider.getReleaseDays());
		chart.addSeries(createBurnUpLine(dataProvider.getReleaseDays(), dataProvider.getAccomplishedEffortPointsByDate()), false, false);
		chart.addSeries(createValueLine(dataProvider.getReleaseDays(), dataProvider.getAccomplishedValuePointsByDate()), false, false);
		chart.addSeries(createIdealLine(dataProvider.getReleaseDays(), dataProvider.getEstimatedEndDay(), dataProvider.getEffortSum()), false, false);
		chart.redraw();
	}

	private Chart createBasicChart() {
		final Chart newChart = new Chart()
				.setAnimation(false)
				.setType(Series.Type.LINE)
				.setChartTitleText("")
				.setLegend(new Legend()
						.setEnabled(true)
						.setAlign(Legend.Align.LEFT)
						.setVerticalAlign(Legend.VerticalAlign.TOP)
						.setY(-8)
						.setFloating(true)
						.setBorderWidth(0)
				)
				.setMarginTop(25)
				.setMarginLeft(30)
				.setMarginRight(30)
				.setBorderRadius(0)
				.setCredits(new Credits().setEnabled(false))
				.setSeriesPlotOptions(new SeriesPlotOptions()
						.setCursor(PlotOptions.Cursor.POINTER)
						.setMarker(new Marker().setLineWidth(1))
				)
				.setLinePlotOptions(new LinePlotOptions()
						.setDataLabels(new DataLabels()
								.setEnabled(true)
								.setAlign(Align.RIGHT)
								.setFormatter(new ShowOnlyLastPointFormatter())
						)
				)
				.setToolTip(new ToolTip()
						.setCrosshairs(true)
						.setFormatter(new ToolTipFormatter() {
							@Override
							public String format(final ToolTipData toolTipData) {
								final String seriesName = toolTipData.getSeriesName();
								final String suffix = seriesName.equals(SERIES_ACCOMPLISHED_VALUE) ? "value points" : (seriesName
										.equals(SERIES_ACCOMPLISHED_EFFORT) ? "effort points" : "ideal effort");
								return toolTipData.getXAsString() + "<br>"
										+ ClientDecimalFormat.roundFloat((float) toolTipData.getYAsDouble(), 1) + " "
										+ suffix;
							}
						})
				);

		newChart.getXAxis(0)
				.setType(Type.LINEAR)
				.setTickWidth(0)
				.setLabels(new XAxisLabels()
						.setAlign(Align.CENTER)
						.setRotation(-45)
						.setX(-16)
						.setY(27)
				);

		newChart.getYAxis(0)
				.setAxisTitleText("")
				.setShowFirstLabel(false)
				.setAllowDecimals(false)
				.setShowLastLabel(true)
				.setOpposite(true)
				.setMin(0)
				.setLabels(new YAxisLabels()
						.setAlign(Labels.Align.RIGHT)
						.setX(27)
						.setY(13)
						.setStyle(new Style()
								.setColor(EFFORT_COLOR)
								.setFontWeight("bold")
						)
						.setFormatter(new AxisLabelsFormatter() {
							@Override
							public String format(final AxisLabelsData axisLabelsData) {
								return ClientDecimalFormat.roundFloat((float) axisLabelsData.getValueAsDouble(), 1);
							}
						})
				);

		newChart.getYAxis(1)
				.setAxisTitleText("")
				.setShowFirstLabel(false)
				.setShowLastLabel(true)
				.setGridLineWidth(0)
				.setMin(0)
				.setLabels(new YAxisLabels()
						.setAlign(Labels.Align.LEFT)
						.setX(-28)
						.setY(16)
						.setStyle(new Style()
								.setColor(VALUE_COLOR)
								.setFontWeight("bold")
						)
						.setFormatter(new AxisLabelsFormatter() {
							@Override
							public String format(final AxisLabelsData axisLabelsData) {
								return ClientDecimalFormat.roundFloat((float) axisLabelsData.getValueAsDouble(), 1);
							}
						})
				);

		return newChart;
	}

	private void configureXAxis(final List<WorkingDay> workingDays) {
		final int numberOfDays = workingDays.size();

		final String[] array = new String[numberOfDays];
		for (int i = 0; i < numberOfDays; i++)
			array[i] = workingDays.get(i).getDayMonthShortYearString();

		chart.getXAxis(0)
				.setTickInterval(Math.ceil(((float) numberOfDays) / MAX_NUMBER_OF_TICKS))
				.setCategories(array);
	}

	private Series createBurnUpLine(final List<WorkingDay> releaseDays, final Map<WorkingDay, Float> values) {
		final Series newSerie = chart.createSeries()
				.setName(SERIES_ACCOMPLISHED_EFFORT)
				.setYAxis(0)
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(2)
						.setColor(EFFORT_COLOR)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(3)));

		return populateSeries(values, releaseDays, newSerie);
	}

	private Series createValueLine(final List<WorkingDay> releaseDays, final Map<WorkingDay, Float> values) {
		final Series newSerie = chart.createSeries()
				.setName(SERIES_ACCOMPLISHED_VALUE)
				.setYAxis(1)
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(2)
						.setColor(VALUE_COLOR)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(2)));

		return populateSeries(values, releaseDays, newSerie);
	}

	private Series createIdealLine(final List<WorkingDay> releaseDays, final WorkingDay estimatedEndDay, final float maxValue) {
		final Series idealLine = chart.createSeries()
				.setName(SERIES_IDEAL_BURN_UP_LINE)
				.setYAxis(0)
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(1)
						.setDashStyle(PlotLine.DashStyle.SHORT_DASH)
						.setColor(IDEAL_EFFORT_COLOR)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(1)))

				.addPoint(0, 0)
				.addPoint(createLastPoint(releaseDays.indexOf(estimatedEndDay), maxValue, SERIES_IDEAL_BURN_UP_LINE));

		return idealLine;
	}

	private Series populateSeries(final Map<WorkingDay, Float> values, final List<WorkingDay> releaseDays, final Series serie) {
		if (values.isEmpty()) return serie;

		serie.addPoint(0, 0);

		WorkingDay lastDayAccounted = null;
		for (final WorkingDay workingDay : values.keySet()) {
			lastDayAccounted = workingDay;
			serie.addPoint(releaseDays.indexOf(workingDay), values.get(workingDay));
		}
		if (lastDayAccounted != null) serie.addPoint(createLastPoint(releaseDays.indexOf(lastDayAccounted), values.get(lastDayAccounted), serie.getOptions()
				.get("name").toString()));

		return serie;
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
			return !isLastPoint(dataLabelsData) ? null : String.valueOf(ClientDecimalFormat.roundFloat((float) dataLabelsData.getYAsDouble(), 1));
		}
	}
}
