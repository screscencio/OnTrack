package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.List;

import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.PlotLine;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.labels.DataLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker.Symbol;

import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartPanel extends Composite {

	private static ChartPanelUiBinder uiBinder = GWT.create(ChartPanelUiBinder.class);

	interface ChartPanelUiBinder extends UiBinder<Widget, ChartPanel> {}

	@UiField
	protected Chart chart;

	@UiField
	protected FocusPanel clickableChartPanel;

	private final WidgetVisibilityAssurer visibilityAssurer;

	private Number maxValue;

	private List<Float> yAxisLineValues;

	private List<String> xAxisLineValues;

	public ChartPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		visibilityAssurer = new WidgetVisibilityAssurer(clickableChartPanel);

		configureBasicsChart();

		chart.setSize(350, 210);
	}

	public ChartPanel setMaxValue(final Number maxValue) {
		this.maxValue = maxValue;
		return this;
	}

	public ChartPanel setYAxisLineValues(final List<Float> yAxisLineValues) {
		this.yAxisLineValues = yAxisLineValues;
		return this;
	}

	public ChartPanel setXAxisLineValues(final List<String> xAxisLineValues) {
		this.xAxisLineValues = xAxisLineValues;
		return this;
	}

	public void show(final Widget relativeWidget) {
		configureYAxis();
		configureXAxis();

		createIdealLine();
		createBurnUpLine();

		this.setVisible(true);
		clickableChartPanel.setFocus(true);
		visibilityAssurer.assureVisibilityAround(relativeWidget);
	}

	public void hide() {
		if (!this.isVisible()) return;

		this.setVisible(false);
		chart.removeAllSeries();
	}

	private void createBurnUpLine() {
		final Series newSerie = chart.createSeries()
				.setName("")
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(2)
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(3)));

		newSerie.addPoint(0, 0);
		for (int i = 0; i < yAxisLineValues.size(); i++) {
			newSerie.addPoint(i, yAxisLineValues.get(i));
		}
		chart.addSeries(newSerie);
	}

	private void configureYAxis() {
		chart.getYAxis().setMax(27.0f);
	}

	private void configureXAxis() {
		chart.getXAxis().setCategories(xAxisLineValues.toArray(new String[] {}));
	}

	private void createIdealLine() {
		final Series idealLine = chart.createSeries()
				.setName("IdealLine")
				.setPlotOptions(new LinePlotOptions()
						.setLineWidth(1)
						.setDashStyle(PlotLine.DashStyle.SHORT_DASH)
						.setColor("#r4r44r")
						.setMarker(new Marker()
								.setSymbol(Symbol.CIRCLE)
								.setRadius(1)))
				.addPoint(0, 0)
				.addPoint(xAxisLineValues.size() - 1, maxValue);

		chart.addSeries(idealLine);
	}

	private void configureBasicsChart() {
		chart.setType(Series.Type.LINE)
				.setChartTitleText("")
				.setToolTip(new ToolTip().setEnabled(false))
				.setLegend(new Legend().setEnabled(false))
				.setMarginTop(20)
				.setMarginLeft(30)
				.setMarginRight(15)
				.setMarginBottom(25)
				.setCredits(new Credits().setEnabled(false))
				.setLinePlotOptions(new LinePlotOptions()
						.setEnableMouseTracking(false)
						.setDataLabels(new DataLabels().setEnabled(true))
						.setColor("#535153"));

		chart.getXAxis().setOffset(0).setTickmarkPlacement(null).setTickWidth(2);

		chart.getYAxis().setAxisTitle(null).setMin(0);
		chart.getYAxis().setShowFirstLabel(false);
	}

	@UiHandler("clickableChartPanel")
	protected void onBlur(final BlurEvent event) {
		event.preventDefault();
		event.stopPropagation();

		hide();
	}

	@UiHandler("clickableChartPanel")
	protected void onClick(final ClickEvent e) {
		e.preventDefault();
		e.stopPropagation();
	}

	@UiHandler("clickableChartPanel")
	protected void onKeyDown(final KeyDownEvent e) {
		if (!(e.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE)) return;

		hide();
		e.preventDefault();
		e.stopPropagation();
	}
}
