package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
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

public class ReleaseChart extends Composite implements HasCloseHandlers<ReleaseChart>, PopupAware {

	private static final String IDEAL_EFFORT_COLOR = "#666666";

	private static final String VALUE_COLOR = "#00AA00";

	private static final String EFFORT_COLOR = "#AA0000";

	private static final String SERIES_IDEAL_BURN_UP_LINE = "Declared Ideal Line";

	private static final String SERIES_ACCOMPLISHED_EFFORT = "Effort";

	private static final String SERIES_ACCOMPLISHED_VALUE = "Value";

	private static final int MAX_NUMBER_OF_TICKS = 22;

	private static final String SERIES_INFERED_IDEAL_BURN_UP_LINE = "Infered Ideal Line";

	private static final String INFERED_IDEAL_EFFORT_COLOR = "#bbc2f2";

	private static ChartPanelUiBinder uiBinder = GWT.create(ChartPanelUiBinder.class);

	interface ChartPanelUiBinder extends UiBinder<Widget, ReleaseChart> {}

	protected Chart chart;

	@UiField
	protected FocusPanel clickableChartPanel;

	@UiField
	protected FocusPanel chartPanel;

	@UiField
	protected Image closeIcon;

	@UiField
	protected ReleaseChartEditableDateLabel startDate;

	@UiField
	protected ReleaseChartEditableDateLabel endDate;

	@UiField
	protected ReleaseChartEditableLabel velocity;

	@UiField
	protected Label currentVelocity;

	@UiField
	protected Label helpText;

	private final ReleaseChartDataProvider dataProvider;

	private ActionExecutionListener actionExecutionListener;

	public ReleaseChart(final ReleaseChartDataProvider dataProvider) {
		this.dataProvider = dataProvider;
		initWidget(uiBinder.createAndBindUi(this));

		chart = createBasicChart();
		chartPanel.add(chart);
	}

	@UiHandler("chartPanel")
	public void onAttach(final AttachEvent event) {
		if (event.isAttached()) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					chart.setSize(chartPanel.getElement().getClientWidth(), chartPanel.getElement().getClientHeight(), false);
				}
			});
		}
	}

	@UiHandler("startDate")
	protected void onStartDateChange(final ValueChangeEvent<Date> event) {
		dataProvider.declareStartDate(event.getValue());
	}

	@UiHandler("endDate")
	protected void onEndDateChange(final ValueChangeEvent<Date> event) {
		dataProvider.declareEndDate(event.getValue());
	}

	@UiHandler("velocity")
	protected void onVelocityChange(final ValueChangeEvent<Float> event) {
		dataProvider.declareEstimatedVelocity(event.getValue());
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
	public HandlerRegistration addCloseHandler(final CloseHandler<ReleaseChart> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		updateData();

		startDate.hidePicker();
		endDate.hidePicker();

		ClientServiceProvider.getInstance().getActionExecutionService().addActionExecutionListener(getActionExecutionListener());

		clickableChartPanel.setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		ClientServiceProvider.getInstance().getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
		CloseEvent.fire(this, this);
	}

	public void updateData() {
		dataProvider.evaluateData();

		updateChart();

		startDate.setValue(dataProvider.getEstimatedStartDay().getJavaDate(), false);
		startDate.setRemoveValueAvailable(dataProvider.hasDeclaredStartDay());

		endDate.setValue(dataProvider.getEstimatedEndDay().getJavaDate(), false);
		endDate.setRemoveValueAvailable(dataProvider.hasDeclaredEndDay());

		velocity.setValue(dataProvider.getEstimatedVelocity(), false);
		velocity.setRemoveValueAvailable(dataProvider.hasDeclaredEstimatedVelocity());

		this.currentVelocity.setText(round(dataProvider.getCurrentVelocity()));

		helpText.setVisible(hasDifferentEstimatives());
	}

	private boolean hasDifferentEstimatives() {
		return !dataProvider.getEstimatedEndDay().equals(dataProvider.getInferedEstimatedEndDay());
	}

	private void updateChart() {
		chart.removeAllSeries();

		final List<WorkingDay> releaseDays = dataProvider.getReleaseDays();
		configureXAxis(releaseDays);
		chart.addSeries(createBurnUpLine(releaseDays, dataProvider.getAccomplishedEffortPointsByDate()), false, false);
		final Series valueLine = createValueLine(releaseDays, dataProvider.getAccomplishedValuePointsByDate());
		chart.addSeries(valueLine, false, false);
		if (dataProvider.getValueSum() == 0f) valueLine.hide();
		chart.addSeries(
				createIdealLine(
						SERIES_IDEAL_BURN_UP_LINE,
						IDEAL_EFFORT_COLOR,
						DashStyle.SHORT_DASH,
						releaseDays,
						dataProvider.getEstimatedEndDay(),
						dataProvider.getEffortSum()),
				false,
				false);
		if (!dataProvider.getEstimatedEndDay().equals(dataProvider.getInferedEstimatedEndDay())) {
			final Series inferedIdealLine = createIdealLine(
					SERIES_INFERED_IDEAL_BURN_UP_LINE,
					INFERED_IDEAL_EFFORT_COLOR,
					DashStyle.LONG_DASH,
					releaseDays,
					dataProvider.getInferedEstimatedEndDay(),
					dataProvider.getEffortSum());
			chart.addSeries(
					inferedIdealLine,
					false,
					false);
			inferedIdealLine.hide();
		}

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
								final boolean isValuePoints = toolTipData.getSeriesName().equals(SERIES_ACCOMPLISHED_VALUE);

								final float currentPoints = (float) toolTipData.getYAsDouble();
								final int currentDayCount = toolTipData.getPoint().getX().intValue() + 1;

								final String toolTipText = toolTipData.getXAsString() + "<br>"
										+ round(currentPoints) + " "
										+ (isValuePoints ? "value points" : "effort points") + "<br>"
										+ "Velocity: "
										+ round(currentPoints / currentDayCount) + " "
										+ (isValuePoints ? "vp" : "ep") + "/day";
								return toolTipText;
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
								return round((float) axisLabelsData.getValueAsDouble());
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
								return round((float) axisLabelsData.getValueAsDouble());
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
						.setZIndex(2)
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
						.setZIndex(1)
						.setColor(VALUE_COLOR)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(2)));

		return populateSeries(values, releaseDays, newSerie);
	}

	private Series createIdealLine(final String seriesName,
			final String seriesColor, final DashStyle dashStyle, final List<WorkingDay> releaseDays, final WorkingDay estimatedEndDay, final float maxValue) {
		final Series idealLine = chart.createSeries()
				.setName(seriesName)
				.setYAxis(0)
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(1)
						.setEnableMouseTracking(true)
						.setHoverStateEnabled(false)
						.setShadow(false)
						.setZIndex(0)
						.setDashStyle(dashStyle)
						.setColor(seriesColor)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(1)))

				.addPoint(0, 0)
				.addPoint(createLastPoint(releaseDays.indexOf(estimatedEndDay), maxValue, seriesName));

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
			return !isLastPoint(dataLabelsData) ? null : String.valueOf(round((float) dataLabelsData.getYAsDouble()));
		}
	}

	private ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener == null ? actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (action instanceof ReleaseDeclareStartDayAction ||
						action instanceof ReleaseDeclareEndDayAction ||
						action instanceof ReleaseDeclareEstimatedVelocityAction) updateData();
			}
		} : actionExecutionListener;
	}

	private String round(final float currentPoints) {
		return ClientDecimalFormat.roundFloat(currentPoints, 1);
	}
}
