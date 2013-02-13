/*
 * Copyright 2011 Moxie Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moxieapps.gwt.highcharts.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.moxieapps.gwt.highcharts.client.events.AxisSetExtremesEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartClickEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartClickEventHandler;
import org.moxieapps.gwt.highcharts.client.events.ChartLoadEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartLoadEventHandler;
import org.moxieapps.gwt.highcharts.client.events.ChartRedrawEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartRedrawEventHandler;
import org.moxieapps.gwt.highcharts.client.events.ChartSelectionEvent;
import org.moxieapps.gwt.highcharts.client.events.ChartSelectionEventHandler;
import org.moxieapps.gwt.highcharts.client.events.PointClickEvent;
import org.moxieapps.gwt.highcharts.client.events.PointLegendItemClickEvent;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOutEvent;
import org.moxieapps.gwt.highcharts.client.events.PointMouseOverEvent;
import org.moxieapps.gwt.highcharts.client.events.PointRemoveEvent;
import org.moxieapps.gwt.highcharts.client.events.PointSelectEvent;
import org.moxieapps.gwt.highcharts.client.events.PointUnselectEvent;
import org.moxieapps.gwt.highcharts.client.events.PointUpdateEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesCheckboxClickEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesClickEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesHideEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesLegendItemClickEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesMouseOutEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesMouseOverEvent;
import org.moxieapps.gwt.highcharts.client.events.SeriesShowEvent;
import org.moxieapps.gwt.highcharts.client.labels.AxisLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.DataLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.LegendLabelsData;
import org.moxieapps.gwt.highcharts.client.labels.StackLabelsData;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaRangePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaSplinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.AreaSplineRangePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.BarPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.ColumnRangePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.OHLCPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.PiePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.ScatterPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.SeriesPlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.SplinePlotOptions;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * The common base class for both {@link Chart} types as well as {@link StockChart} types.
 * You should not use this class directly, but instead create an instance of one of those
 * sub types.
 * 
 * @author squinn@moxiegroup.com (Shawn Quinn)
 * @see Chart
 * @see StockChart
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class BaseChart<T> extends Widget {

	/**
	 * This class can not be created directly, but instead create an instance of one of the
	 * sub types such as {@link Chart} or {@link StockChart}.
	 */
	protected BaseChart() {
		final DivElement divElement = Document.get().createDivElement();
		divElement.getStyle().setOverflow(com.google.gwt.dom.client.Style.Overflow.HIDDEN);
		divElement.setId(Document.get().createUniqueId());
		this.setElement(divElement);
	}

	/**
	 * Convenience method for setting the 'alignTicks' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/alignTicks", false);
	 * </code>
	 * </pre>
	 * 
	 * When using multiple axis, the ticks of two or more opposite axes will automatically be aligned
	 * by adding ticks to the axis or axes with the least ticks. This can be prevented by setting
	 * alignTicks to false. If the grid lines look messy, it's a good idea to hide them for the
	 * secondary axis by setting gridLineWidth to 0. Defaults to true.
	 * 
	 * @param alignTicks The value to set as the 'alignTicks' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAlignTicks(final boolean alignTicks) {
		return this.setOption("/chart/alignTicks", alignTicks);
	}

	/**
	 * Convenience method for setting the 'animation' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/animation", true);
	 * </code>
	 * </pre>
	 * 
	 * Sets the overall animation for all chart updating. Animation can be disabled throughout
	 * the chart by setting it to false here. It can be overridden for each individual API
	 * method as a function parameter. The only animation not affected by this option is the
	 * initial series animation, see {@link org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions#setAnimation(boolean)} method for
	 * control over series animations that are added after the chart has been rendered.
	 * <p/>
	 * Note that more control over the animations is available by calling the {@link #setAnimation(Animation)} method instead.
	 * <p/>
	 * 
	 * @param animation The value to set as the 'animation' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAnimation(final boolean animation) {
		return this.setOption("/chart/animation", animation);
	}

	/**
	 * Convenience method for setting the 'animation' options of the chart. Equivalent to code like:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/animation/duration", 500);
	 *     chart.setOption("/chart/animation/easing", "linear");
	 * </code>
	 * </pre>
	 * 
	 * Sets the overall animation for all chart updating. Animation can be disabled throughout
	 * the chart by setting it to false here. It can be overridden for each individual API
	 * method as a function parameter. The only animation not affected by this option is the
	 * initial series animation, see {@link org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions#setAnimation(Animation)} method for
	 * control over series animations that are added after the chart has been rendered.
	 * <p/>
	 * Note that this is intended for users that want to have finite control over the way animations behave. To simply enable/disable animations, you can use
	 * the {@link #setAnimation(boolean)} method instead.
	 * <p/>
	 * 
	 * @param animation The custom animation to set as the default animation type for the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAnimation(final Animation animation) {
		return this.setOption("/chart/animation", animation.getOptions());
	}

	/**
	 * Convenience method for setting the 'backgroundColor' option of the chart to an RGB hex value. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/backgroundColor", "#CCCCCC");
	 * </code>
	 * </pre>
	 * 
	 * The RGB background color for the outer chart area. Defaults to "#FFFFFF".
	 * <p/>
	 * Note that this method is intended for setting the color to a simple RBG hex value. If you instead want to set a color to include an alpha channel or a
	 * gradient, use the {@link #setBackgroundColor(Color)} version instead.
	 * 
	 * @param backgroundColor The value to set as the 'backgroundColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBackgroundColor(final String backgroundColor) {
		return this.setOption("/chart/backgroundColor", backgroundColor);
	}

	/**
	 * Convenience method for setting the 'backgroundColor' option of the chart, allowing for
	 * colors with opacity or gradients. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/backgroundColor", new Color()
	 *        .setLinearGradient(0.0, 0.0, 1.0, 1.0)
	 *        .addStop(new Color(255, 255, 255))
	 *        .addStop(new Color(200, 200, 255))
	 *     );
	 * </code>
	 * </pre>
	 * 
	 * The background color or gradient for the outer chart area. Defaults to "#FFFFFF".
	 * <p/>
	 * Note that this method is intended for setting the color to a gradient or color that includes an alpha channel. If you instead just want to set the color
	 * to a normal RGB hex value you can use the {@link #setBackgroundColor(String)} version instead.
	 * 
	 * @param backgroundColor The color gradient or color with an alpha channel to set as the 'backgroundColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBackgroundColor(final Color backgroundColor) {
		return this.setOption("/chart/backgroundColor", backgroundColor != null ? backgroundColor.getOptionValue() : null);
	}

	/**
	 * Convenience method for setting the 'borderColor' option of the chart to an RGB hex value. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/borderColor", "#CCCCCC");
	 * </code>
	 * </pre>
	 * 
	 * The RGB color of the outer chart border. The border is painted using vector graphic techniques to allow
	 * rounded corners. Defaults to "#4572A7".
	 * <p/>
	 * Note that this method is intended for setting the color to a simple RBG hex value. If you instead want to set a color to include an alpha channel or a
	 * gradient, use the {@link #setBorderColor(Color)} version instead.
	 * 
	 * @param borderColor The value to set as the 'borderColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBorderColor(final String borderColor) {
		return this.setOption("/chart/borderColor", borderColor);
	}

	/**
	 * Convenience method for setting the 'borderColor' option of the chart, allowing for
	 * colors with opacity or gradients. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/borderColor", new Color()
	 *        .setLinearGradient(0.0, 0.0, 1.0, 1.0)
	 *        .addStop(new Color(255, 255, 255))
	 *        .addStop(new Color(200, 200, 255))
	 *     );
	 * </code>
	 * </pre>
	 * 
	 * The color of the outer chart border. The border is painted using vector graphic techniques to allow
	 * rounded corners. Defaults to "#4572A7".
	 * <p/>
	 * Note that this method is intended for setting the color to a gradient or color that includes an alpha channel. If you instead just want to set the color
	 * to a normal RGB hex value you can use the {@link #setBorderColor(String)} version instead.
	 * 
	 * @param borderColor The color gradient or color with an alpha channel to set as the 'borderColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBorderColor(final Color borderColor) {
		return this.setOption("/chart/borderColor", borderColor != null ? borderColor.getOptionValue() : null);
	}

	/**
	 * Convenience method for setting the 'borderRadius' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/borderRadius", 10);
	 * </code>
	 * </pre>
	 * 
	 * The corner radius of the outer chart border. Defaults to 5.
	 * 
	 * @param borderRadius The corner radius of the outer chart border.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBorderRadius(final Number borderRadius) {
		return this.setOption("/chart/borderRadius", borderRadius);
	}

	/**
	 * Convenience method for setting the 'borderWidth' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/borderWidth", 10);
	 * </code>
	 * </pre>
	 * 
	 * The pixel width of the outer chart border. The border is painted using vector graphic
	 * techniques to allow rounded corners. Defaults to 0.
	 * 
	 * @param borderWidth The corner radius of the outer chart border.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBorderWidth(final Number borderWidth) {
		return this.setOption("/chart/borderWidth", borderWidth);
	}

	/**
	 * Convenience method for setting the 'subtitle/text' chart option. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/subtitle/text", "Source: Wikipedia.com");
	 * </code>
	 * </pre>
	 * 
	 * The actual text of the chart subtitle. It can contain basic HTML text markup like
	 * &lt;b&gt;, &lt;i&gt; and spans with style. Defaults to null.
	 * <p/>
	 * For more control over the title, utilize the {@link #setChartSubtitle(ChartSubtitle)} method instead.
	 * <p/>
	 * To hide the chart subtitle completely, simply set the text to null.
	 * <p/>
	 * Note that this method is intended for handling configuring the chart's sub title area before the chart has been rendered, and has no affect if you invoke
	 * the method after the chart has been drawn to the DOM. If you do want to change the sub title (or title) of the chart dynamically after it has already
	 * been rendered, you can use the {@link #setTitle(ChartTitle, ChartSubtitle)} method instead.
	 * 
	 * @param subtitle Sets the subtitle of chart, or null to hide the subtitle.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setChartSubtitleText(final String subtitle) {
		return this.setOption("/subtitle/text", subtitle);
	}

	/**
	 * Convenience method for setting the 'subtitle' chart options. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/subtitle/text", "Source: Wikipedia.com");
	 *     chart.setOption("/subtitle/align", ChartTitle.Align.Left);
	 *     etc...
	 * </code>
	 * </pre>
	 * <p/>
	 * When you call this method it will overwrite any existing settings that have already been applied to the subtitle (e.g. if you had previously called the
	 * {@link #setChartSubtitleText(String)} method that change will get overwritten by this call.)
	 * <p/>
	 * If you just want to change the text of the subtitle, you can simply use the {@link #setChartSubtitleText(String)} method instead.
	 * <p/>
	 * Note that this method is intended for handling configuring the chart's sub title area before the chart has been rendered, and has no affect if you invoke
	 * the method after the chart has been drawn to the DOM. If you do want to change the sub title (or title) of the chart dynamically after it has already
	 * been rendered, you can use the {@link #setTitle(ChartTitle, ChartSubtitle)} method instead.
	 * 
	 * @param subtitle Sets the chart subtitle options, or null to hide the subtitle.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setChartSubtitle(final ChartSubtitle subtitle) {
		return this.setOption("/subtitle", subtitle != null ? subtitle.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'title/text' chart option. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/title/text", "Sales by Year");
	 * </code>
	 * </pre>
	 * 
	 * The actual text of the chart title. It can contain basic HTML text markup like
	 * &lt;b&gt;, &lt;i&gt; and spans with style. Defaults to "Chart title".
	 * <p/>
	 * For more control over the title, utilize the {@link #setChartTitle(ChartTitle)} method instead.
	 * <p/>
	 * To hide the chart title completely, simply set the text to null.
	 * <p/>
	 * Note that this method is intended for handling configuring the chart's title area before the chart has been rendered, and has no affect if you invoke the
	 * method after the chart has been drawn to the DOM. If you do want to change the title (or sub title) of the chart dynamically after it has already been
	 * rendered, you can use the {@link #setTitle(ChartTitle, ChartSubtitle)} method instead.
	 * 
	 * @param title Sets the title of chart, or null to hide the title.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setChartTitleText(final String title) {
		return this.setOption("/title/text", title);
	}

	/**
	 * Convenience method for setting the 'title' chart options. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/title/text", "Sales by Year");
	 *     chart.setOption("/title/align", ChartTitle.Align.Left);
	 *     etc...
	 * </code>
	 * </pre>
	 * <p/>
	 * If you call this method it will overwrite any existing settings that have already been applied to the title (e.g. if you had previously called the
	 * {@link #setChartTitleText(String)} method that change will get overwritten by this call.)
	 * <p/>
	 * If you just want to change the text of the title, you can simply use the {@link #setChartTitleText(String)} method instead.
	 * <p/>
	 * Note that this method is intended for handling configuring the chart's title area before the chart has been rendered, and has no affect if you invoke the
	 * method after the chart has been drawn to the DOM. If you do want to change the title (or sub title) of the chart dynamically after it has already been
	 * rendered, you can use the {@link #setTitle(ChartTitle, ChartSubtitle)} method instead.
	 * 
	 * @param title Sets the chart title options, or null to hide the title.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setChartTitle(final ChartTitle title) {
		return this.setOption("/title", title != null ? title.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'className' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/className", "RedChart");
	 * </code>
	 * </pre>
	 * 
	 * A CSS class name to apply to the charts container div, allowing unique CSS styling
	 * for each chart. Defaults to "".
	 * 
	 * @param className A CSS class name to apply to the charts container div.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setClassName(final String className) {
		return this.setOption("/chart/className", className);
	}

	/**
	 * Sets the default colors for the chart's series. When all colors are used, new colors are pulled from the start again.
	 * Note that the colors for each series can be overridden via the {@link org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions#setColor(String)}
	 * mechanism.
	 * <p/>
	 * Default colors are: "#4572A7", "#AA4643", "#89A54E", "#80699B", "#3D96AE", "#DB843D", "#92A8CD", "#A47D7C", "#B5CA92"
	 * 
	 * @param colors An array of colors to use as the defaults for each series.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setColors(final String... colors) {
		return this.setOption("/colors", colors);
	}

	/**
	 * Convenience method for setting the 'credits' options of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/credits/text", "Presented by Snoopy");
	 *     chart.setOption("/credits/href", "http://www.peanuts.com/");
	 *     etc..
	 * </code>
	 * </pre>
	 * 
	 * Highchart by default puts a credits label in the lower right corner of the chart. This
	 * can be changed using these options.
	 * 
	 * @param credits The options to apply to the credits area of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setCredits(final Credits credits) {
		return this.setOption("/credits", credits != null ? credits.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'exporting' options of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/exporting/enabled", true);
	 *     chart.setOption("/exporting/width", 600);
	 *     etc..
	 * </code>
	 * </pre>
	 * 
	 * Note that the "exporting" module must be included in the page in order for the exporting
	 * options to apply. E.g.:
	 * <p/>
	 * &lt;script type="text/javascript" src="js/modules/exporting.js"&gt;&lt;/script&gt;
	 * 
	 * @param exporting The options to apply to the exporting area of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @see #setNavigation(Navigation)
	 * @since 1.1.0
	 */
	public T setExporting(final Exporting exporting) {
		return this.setOption("/exporting", exporting != null ? exporting.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'height' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/height", 300);
	 * </code>
	 * </pre>
	 * 
	 * An explicit height for the chart. By default the height is calculated from the offset
	 * height of the containing element. Defaults to null.
	 * 
	 * @param height An explicit height for the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setHeight(final Number height) {
		return this.setOption("/chart/height", height);
	}

	/**
	 * Convenience method for setting the 'ignoreHiddenSeries' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/ignoreHiddenSeries", false);
	 * </code>
	 * </pre>
	 * 
	 * If true, the axes will scale to the remaining visible series once one series is hidden.
	 * If false, hiding and showing a series will not affect the axes or the other series.
	 * For stacks, once one series within the stack is hidden, the rest of the stack will close
	 * in around it even if the axis is not affected. Defaults to true.
	 * 
	 * @param ignoreHiddenSeries If true, the axes will scale to the remaining visible series
	 *            once one series is hidden. If false, hiding and showing a series
	 *            will not affect the axes or the other series.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setIgnoreHiddenSeries(final boolean ignoreHiddenSeries) {
		return this.setOption("/chart/ignoreHiddenSeries", ignoreHiddenSeries);
	}

	/**
	 * Convenience method for setting the 'inverted' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/inverted", true);
	 * </code>
	 * </pre>
	 * 
	 * Whether to invert the axes so that the x axis is horizontal and y axis is vertical. When true, the
	 * x axis is reversed by default. If a bar plot is present in the chart, it will be inverted automatically. Defaults to false.
	 * 
	 * @param inverted Whether to invert the axes so that the x axis is horizontal and y axis is vertical.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setInverted(final boolean inverted) {
		return this.setOption("/chart/inverted", inverted);
	}

	/**
	 * Sets HTML labels that can be positioned anywhere in the chart area. Each label can contain
	 * HTML as well as CSS style properties that can be used to position the label anywhere in the
	 * chart area. See the {@link #setLabelsStyle(Style)} method to control the default style for
	 * each label.
	 * <p/>
	 * 
	 * @param labelItems An array of LabelItem instances that contain the HTML of the label as well
	 *            as a "style" object that can be used to position each label.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setLabelItems(final LabelItem... labelItems) {
		return this.setOption("/labels/items", labelItems);
	}

	/**
	 * Sets the default CSS style options that will be shared by all label items that are added to the
	 * chart via the {@link #setLabelItems(LabelItem...)} method. The default label style
	 * is simply:
	 * <ul>
	 * <li><b>color</b>: "#3E576F"</li>
	 * </ul>
	 * 
	 * @param style Shared CSS styles for all label items.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setLabelsStyle(final Style style) {
		return this.setOption("/labels/style", style != null ? style.getOptions() : null);
	}

	// We need to maintain a local reference to legend to deal with the formatter function
	private Legend legend;

	/**
	 * Convenience method for setting the 'legend' options of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/legend/borderColor", "#CCCCCC");
	 *     chart.setOption("/legend/layout", Legend.Layout.HORIZONTAL);
	 *     etc..
	 * </code>
	 * </pre>
	 * 
	 * The legend is a box containing a symbol and name for each series item or point item in the chart.
	 * 
	 * @param legend The options to apply to the legend of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setLegend(final Legend legend) {
		this.legend = legend;
		return this.setOption("/legend", legend != null ? legend.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'loading' options of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/loading/showDuration", 150);
	 *     chart.setOption("/loading/hideDuration", 60);
	 *     etc..
	 * </code>
	 * </pre>
	 * 
	 * The loading options control the appearance of the loading screen that covers the plot area
	 * on chart operations. This screen only appears after an explicit call to {@link Chart#showLoading(String)}. It is a utility for developers to communicate
	 * to the end
	 * user that something is going on, for example while retrieving new data via a GWT remoting
	 * request. The "Loading..." text itself is not part of this configuration object, but part of
	 * the {@link Lang} object.
	 * 
	 * @param loading The options to apply to the loading area of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setLoading(final Loading loading) {
		return this.setOption("/loading", loading != null ? loading.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'margin' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/margin", 0, 10, 0, 15);
	 * </code>
	 * </pre>
	 * 
	 * The margin between the outer edge of the chart and the plot area. Use the options {@link #setMarginTop(Number)}, {@link #setMarginRight(Number)},
	 * {@link #setMarginBottom(Number)}, and {@link #setMarginLeft(Number)} for shorthand setting of one option.
	 * <p/>
	 * Since version 2.1, the margin is 0 by default. The actual space is dynamically calculated from the offset of axis labels, axis title, title, subtitle and
	 * legend in addition to the {@link #setSpacingTop(Number)}, {@link #setSpacingRight(Number)}, {@link #setSpacingBottom(Number)}, and
	 * {@link #setSpacingLeft(Number)} options.
	 * 
	 * @param marginTop The margin between the top outer edge of the chart and the plot area.
	 * @param marginRight The margin between the right outer edge of the chart and the plot area.
	 * @param marginBottom The margin between the bottom outer edge of the chart and the plot area.
	 * @param marginLeft The margin between the left outer edge of the chart and the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setMargin(final Number marginTop, final Number marginRight, final Number marginBottom, final Number marginLeft) {
		final JSONArray margins = new JSONArray();
		margins.set(0, new JSONNumber(marginTop.doubleValue()));
		margins.set(1, new JSONNumber(marginRight.doubleValue()));
		margins.set(2, new JSONNumber(marginBottom.doubleValue()));
		margins.set(3, new JSONNumber(marginLeft.doubleValue()));
		return this.setOption("/chart/margin", margins);
	}

	/**
	 * Convenience method for setting the 'marginTop' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/marginTop", 100);
	 * </code>
	 * </pre>
	 * 
	 * The margin between the top outer edge of the chart and the plot area. Use this to set a fixed
	 * pixel value for the margin as opposed to the default dynamic margin.
	 * See also {@link #setSpacingTop(Number)}. Defaults to null.
	 * 
	 * @param marginTop The margin between the top outer edge of the chart and the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setMarginTop(final Number marginTop) {
		return this.setOption("/chart/marginTop", marginTop);
	}

	/**
	 * Convenience method for setting the 'marginRight' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/marginRight", 100);
	 * </code>
	 * </pre>
	 * 
	 * The margin between the right outer edge of the chart and the plot area. Use this to set
	 * a fixed pixel value for the margin as opposed to the default dynamic margin.
	 * See also {@link #setSpacingRight(Number)}. Defaults to null.
	 * 
	 * @param marginRight The margin between the right outer edge of the chart and the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setMarginRight(final Number marginRight) {
		return this.setOption("/chart/marginRight", marginRight);
	}

	/**
	 * Convenience method for setting the 'marginBottom' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/marginBottom", 100);
	 * </code>
	 * </pre>
	 * 
	 * The margin between the bottom outer edge of the chart and the plot area. Use this to set
	 * a fixed pixel value for the margin as opposed to the default dynamic margin.
	 * See also {@link #setSpacingBottom(Number)}. Defaults to null.
	 * 
	 * @param marginBottom The margin between the bottom outer edge of the chart and the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setMarginBottom(final Number marginBottom) {
		return this.setOption("/chart/marginBottom", marginBottom);
	}

	/**
	 * Convenience method for setting the 'marginBottom' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/marginLeft", 100);
	 * </code>
	 * </pre>
	 * 
	 * The margin between the left outer edge of the chart and the plot area. Use this to set a
	 * fixed pixel value for the margin as opposed to the default dynamic margin.
	 * See also {@link #setSpacingLeft(Number)}. Defaults to null.
	 * 
	 * @param marginLeft The margin between the left outer edge of the chart and the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setMarginLeft(final Number marginLeft) {
		return this.setOption("/chart/marginLeft", marginLeft);
	}

	/**
	 * Convenience method for setting the 'navigation' options of the chart, which represents
	 * a collection of options for buttons and menus appearing in the exporting module.
	 * <p/>
	 * Note that the "exporting" module must be included in the page in order for the exporting navigation options to apply. E.g.:
	 * <p/>
	 * &lt;script type="text/javascript" src="js/modules/navigation.js"&gt;&lt;/script&gt;
	 * 
	 * @param navigation The options to apply to the exporting navigation area of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @see #setExporting(Exporting)
	 * @since 1.1.0
	 */
	public T setNavigation(final Navigation navigation) {
		return this.setOption("/navigation", navigation != null ? navigation.getOptions() : null);
	}

	/**
	 * Convenience method for setting the 'plotBackgroundColor' option of the chart to an RGB hex value. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotBackgroundColor", "#CCCCCC");
	 * </code>
	 * </pre>
	 * 
	 * The RGB background color for the plot area. Defaults to null.
	 * <p/>
	 * Note that this method is intended for setting the color to a simple RBG hex value. If you instead want to set a color to include an alpha channel or a
	 * gradient, use the {@link #setPlotBackgroundColor(Color)} version instead.
	 * 
	 * @param plotBackgroundColor The value to set as the 'plotBackgroundColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotBackgroundColor(final String plotBackgroundColor) {
		return this.setOption("/chart/plotBackgroundColor", plotBackgroundColor);
	}

	/**
	 * Convenience method for setting the 'plotBackgroundColor' option of the chart, allowing for
	 * colors with opacity or gradients. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotBackgroundColor", new Color()
	 *        .setLinearGradient(0.0, 0.0, 1.0, 1.0)
	 *        .addStop(new Color(255, 255, 255))
	 *        .addStop(new Color(200, 200, 255))
	 *     );
	 * </code>
	 * </pre>
	 * 
	 * The background color or gradient for the plot area. Defaults to null.
	 * <p/>
	 * Note that this method is intended for setting the color to a gradient or color that includes an alpha channel. If you instead just want to set the color
	 * to a normal RGB hex value you can use the {@link #setPlotBackgroundColor(String)} version instead.
	 * 
	 * @param plotBackgroundColor The color gradient or color with an alpha channel to set as the 'plotBackgroundColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotBackgroundColor(final Color plotBackgroundColor) {
		return this.setOption("/chart/plotBackgroundColor", plotBackgroundColor != null ? plotBackgroundColor.getOptionValue() : null);
	}

	/**
	 * Convenience method for setting the 'plotBorderColor' option of the chart to an RGB hex value. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotBorderColor", "#CCCCCC");
	 * </code>
	 * </pre>
	 * 
	 * The RGB color of the inner chart or plot area border. Defaults to "#C0C0C0".
	 * <p/>
	 * Note that this method is intended for setting the color to a simple RBG hex value. If you instead want to set a color to include an alpha channel or a
	 * gradient, use the {@link #setPlotBorderColor(Color)} version instead.
	 * 
	 * @param plotBorderColor The value to set as the 'plotBorderColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotBorderColor(final String plotBorderColor) {
		return this.setOption("/chart/plotBorderColor", plotBorderColor);
	}

	/**
	 * Convenience method for setting the 'plotBorderColor' option of the chart, allowing for
	 * colors with opacity or gradients. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotBorderColor", new Color()
	 *        .setLinearGradient(0.0, 0.0, 1.0, 1.0)
	 *        .addStop(new Color(255, 255, 255))
	 *        .addStop(new Color(200, 200, 255))
	 *     );
	 * </code>
	 * </pre>
	 * 
	 * The color (or gradient) of the inner chart or plot area border. Defaults to "#C0C0C0".
	 * <p/>
	 * Note that this method is intended for setting the color to a gradient or color that includes an alpha channel. If you instead just want to set the color
	 * to a normal RGB hex value you can use the {@link #setPlotBorderColor(String)} version instead.
	 * 
	 * @param plotBorderColor The color gradient or color with an alpha channel to set as the 'plotBorderColor' option on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotBorderColor(final Color plotBorderColor) {
		return this.setOption("/chart/plotBorderColor", plotBorderColor != null ? plotBorderColor.getOptionValue() : null);
	}

	/**
	 * Convenience method for setting the 'plotBorderWidth' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotBorderWidth", 10);
	 * </code>
	 * </pre>
	 * 
	 * The pixel width of the plot area border. Defaults to 0.
	 * 
	 * @param plotBorderWidth The pixel width of the plot area border.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotBorderWidth(final Number plotBorderWidth) {
		return this.setOption("/chart/plotBorderWidth", plotBorderWidth);
	}

	/**
	 * Convenience method for setting the 'plotShadow' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/plotShadow", false);
	 * </code>
	 * </pre>
	 * 
	 * Whether to apply a drop shadow to the plot area. Requires that plotBackgroundColor be set. Defaults to false.
	 * 
	 * @param plotShadow Whether to apply a drop shadow to the plot area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPlotShadow(final boolean plotShadow) {
		return this.setOption("/chart/plotShadow", plotShadow);
	}

	/**
	 * Convenience method for setting the 'reflow' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/reflow", false);
	 * </code>
	 * </pre>
	 * 
	 * Whether to reflow the chart to fit the width of the container div on resizing
	 * the window. Defaults to true.
	 * 
	 * @param reflow If true, reflow the chart to fit the width of the container div on resizing
	 *            the window.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setReflow(final boolean reflow) {
		return this.setOption("/chart/reflow", reflow);
	}

	/**
	 * Convenience method for setting the 'shadow' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/shadow", true);
	 * </code>
	 * </pre>
	 * 
	 * Whether to apply a drop shadow to the outer chart area. Requires that backgroundColor be set. Defaults to false.
	 * 
	 * @param shadow If true, apply a drop shadow to the outer chart area.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setShadow(final boolean shadow) {
		return this.setOption("/chart/shadow", shadow);
	}

	/**
	 * Convenience method for setting the 'showAxes' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/showAxes", true);
	 * </code>
	 * </pre>
	 * 
	 * Whether to show the axes initially. This only applies to empty charts where series are added
	 * dynamically, as axes are automatically added to cartesian series. Defaults to false.
	 * 
	 * @param showAxes If true, show the axes initially (only applies to empty charts when the series
	 *            are added dynamically).
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setShowAxes(final boolean showAxes) {
		return this.setOption("/chart/showAxes", showAxes);
	}

	/**
	 * Convenience method for setting the 'spacingTop' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/spacingTop", 100);
	 * </code>
	 * </pre>
	 * 
	 * The space between the top edge of the chart and the content (plot area, axis title and labels,
	 * title, subtitle or legend in top position). Defaults to 10.
	 * 
	 * @param spacingTop The space between the top edge of the chart and the content.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSpacingTop(final Number spacingTop) {
		return this.setOption("/chart/spacingTop", spacingTop);
	}

	/**
	 * Convenience method for setting the 'spacingRight' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/spacingRight", 100);
	 * </code>
	 * </pre>
	 * 
	 * The space between the right edge of the chart and the content (plot area, axis title and labels,
	 * title, subtitle or legend in top position). Defaults to 10.
	 * 
	 * @param spacingRight The space between the right edge of the chart and the content.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSpacingRight(final Number spacingRight) {
		return this.setOption("/chart/spacingRight", spacingRight);
	}

	/**
	 * Convenience method for setting the 'spacingBottom' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/spacingBottom", 100);
	 * </code>
	 * </pre>
	 * 
	 * The space between the bottom edge of the chart and the content (plot area, axis title and labels,
	 * title, subtitle or legend in top position). Defaults to 15.
	 * 
	 * @param spacingBottom The space between the bottom edge of the chart and the content.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSpacingBottom(final Number spacingBottom) {
		return this.setOption("/chart/spacingBottom", spacingBottom);
	}

	/**
	 * Convenience method for setting the 'spacingLeft' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/spacingLeft", 100);
	 * </code>
	 * </pre>
	 * 
	 * The space between the left edge of the chart and the content (plot area, axis title and labels,
	 * title, subtitle or legend in top position). Defaults to 10.
	 * 
	 * @param spacingLeft The space between the left edge of the chart and the content.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSpacingLeft(final Number spacingLeft) {
		return this.setOption("/chart/spacingLeft", spacingLeft);
	}

	/**
	 * Convenience method for setting the 'style' options of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/style/fontWeight", "bold");
	 *     chart.setOption("/chart/style/fontFamily", "serif");
	 *     etc.
	 * </code>
	 * </pre>
	 * 
	 * Additional CSS styles to apply inline to the container div. Note that since the default
	 * font styles are applied in the renderer, it is ignorant of the individual chart options
	 * and must be set globally. Defaults to:
	 * <ul>
	 * <li>fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif'</li>
	 * <li>fontSize: '12px'</li>
	 * </ul>
	 * 
	 * @param style An object containing the style properties to set on the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setStyle(final Style style) {
		return this.setOption("/chart/style", style != null ? style.getOptions() : null);
	}

	/**
	 * Sets the default symbols for the series point markers. When all symbols are used, new symbols
	 * are pulled from the start again. Note that the symbols for each series can be overridden via the
	 * {@link org.moxieapps.gwt.highcharts.client.plotOptions.Marker#setSymbol(org.moxieapps.gwt.highcharts.client.plotOptions.Marker.Symbol)} method
	 * (which can be set on the series via its plot options, see {@link Series#setPlotOptions(org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions)}).
	 * <p/>
	 * Default symbols are: Circle, Diamond, Square, Triangle, and Triangle-Down
	 * 
	 * @param symbols An array of symbols to use as the default symbols for the series point markers.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSymbols(final Marker.Symbol... symbols) {
		JSONArray symbolsArray = null;
		if (symbols != null) {
			symbolsArray = new JSONArray();
			for (int i = 0; i < symbols.length; i++) {
				final Marker.Symbol symbol = symbols[i];
				symbolsArray.set(i, new JSONString(symbol.toString()));
			}
		}
		return this.setOption("/symbols", symbolsArray);
	}

	/**
	 * Set a new title or subtitle for the chart. This method can be called after the chart is
	 * rendered in order to change the title of the chart dynamically.
	 * <p/>
	 * Note: this method is primarily intended to allow the chart's title or sub-title area to be modified dynamically, after the chart has already been
	 * rendered to the DOM. If you instead simply want to set the title or sub-title of the chart via normal configuration (before the chart has been rendered),
	 * then you can utilize the {@link #setChartTitleText(String)}, {@link #setChartTitle(ChartTitle)}, {@link #setChartSubtitleText(String)} and
	 * {@link #setChartSubtitle(ChartSubtitle)} methods.
	 * 
	 * @param chartTitle The new options for the main title area of the chart (including the text).
	 * @param chartSubtitle The new options for the main subtitle area of the chart (including the text).
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.1.0
	 */
	public T setTitle(final ChartTitle chartTitle, final ChartSubtitle chartSubtitle) {
		if (isRendered()) {
			nativeSetTitle(chart,
					chartTitle != null ? chartTitle.getOptions().getJavaScriptObject() : null,
					chartSubtitle != null ? chartSubtitle.getOptions().getJavaScriptObject() : null);
		}
		else {
			this.setChartTitle(chartTitle);
			this.setChartSubtitle(chartSubtitle);
		}
		return returnThis();
	}

	// We need to maintain a local reference to tooltip to deal with the formatter function
	private ToolTip toolTip;

	/**
	 * Convenience method for setting the 'tooltip' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/tooltip/borderColor", "#CCCCCC");
	 *     chart.setOption("/tooltip/shadow", true);
	 *     etc..
	 * </code>
	 * </pre>
	 * 
	 * The tooltip appears when the user hovers over a series or point.
	 * 
	 * @param toolTip The options to apply to the tooltip of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setToolTip(final ToolTip toolTip) {
		this.toolTip = toolTip;
		return this.setOption("/tooltip", toolTip != null ? toolTip.getOptions() : null);
	}

	/**
	 * Sets the defaults series type for the chart (which controls the way the series will be rendered), using
	 * an enumeration type in order to ensure a correct value is passed. This is equivalent to
	 * setting the option manually with code like:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/type", "line");
	 * </code>
	 * </pre>
	 * 
	 * Note that if you don't set this explicitly the default series type is {@link Series.Type#LINE}.
	 * 
	 * @param type One of the supported series types.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setType(final Series.Type type) {
		return this.setOption("/chart/type", type.toString());
	}

	/**
	 * Convenience method for setting the 'width' option of the chart. Equivalent to:
	 * 
	 * <pre>
	 * <code>
	 *     chart.setOption("/chart/width", 800);
	 * </code>
	 * </pre>
	 * 
	 * An explicit width for the chart. By default the width is calculated from the offset
	 * width of the containing element. Defaults to null.
	 * 
	 * @param width An explicit width for the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setWidth(final Number width) {
		return this.setOption("/chart/width", width);
	}

	/**
	 * Update the size of the chart to match the size of the container that this widget was inserted
	 * inside of.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSizeToMatchContainer() {
		return this.setSize(this.getOffsetWidth(), this.getOffsetHeight());
	}

	/**
	 * A helper method which can be used to conveniently set the width of the DOM element that contains
	 * the chart to "100%". Note that for more precise control over the size of the chart see the {@link #setSize(int, int)} and
	 * {@link #setSizeToMatchContainer()} methods.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setWidth100() {
		this.getElement().getStyle().setWidth(100, com.google.gwt.dom.client.Style.Unit.PCT);
		return returnThis();
	}

	/**
	 * A helper method which can be used to conveniently set the height of the DOM element that contains
	 * the chart to "100%". Note that for more precise control over the size of the chart see the {@link #setSize(int, int)} and
	 * {@link #setSizeToMatchContainer()} methods.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setHeight100() {
		this.getElement().getStyle().setHeight(100, com.google.gwt.dom.client.Style.Unit.PCT);
		return returnThis();
	}

	/**
	 * Updates the size of the chart to match the given width and height, using the default animation options.
	 * Note that if you simply want to set the size of the chart to match the container in which it was inserted,
	 * you can simply call the {@link #setSizeToMatchContainer()} method instead.
	 * 
	 * @param width The new pixel width of the chart.
	 * @param height The new pixel height of the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSize(final int width, final int height) {
		return this.setSize(width, height, true);
	}

	/**
	 * Updates the size of the chart to match the given width and height, explicitly setting whether or
	 * not the resize should be animated.
	 * 
	 * @param width The new pixel width of the chart.
	 * @param height The new pixel height of the chart.
	 * @param animated When true, the resize will be animated with default animation options. The animation
	 *            can also be configured by call the {@link #setSize(int, int, Animation)} method instead.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSize(final int width, final int height, final boolean animated) {
		return this.setSize(width, height, animated ? new Animation() : null);
	}

	/**
	 * Updates the size of the chart to match the given width and height, allowing for custom control
	 * over how the animation will be resized.
	 * 
	 * @param width The new pixel width of the chart.
	 * @param height The new pixel height of the chart.
	 * @param animation The custom animation to use as the resize animation for the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSize(final int width, final int height, final Animation animation) {

		// Avoid monkeying with anything if we got bad values
		if (width <= 0 || height <= 0) { return returnThis(); }

		if (isRendered()) {
			// If we're already on the screen, then do the resize natively
			if (animation == null || animation.getOptions() == null) {
				nativeSetSize(chart, width, height, animation != null);
			}
			else {
				nativeSetSize(chart, width, height, animation.getOptions().getJavaScriptObject());
			}
		}
		else {
			// In we're invoked before the chart is rendered, keep the width and height around in our configuration
			setOption("/chart/width", width);
			setOption("/chart/height", height);
		}

		return returnThis();

	}

	// Delegate responsibility for managing configuration options to an anonymous helper class
	private final Configurable configurable = new Configurable() {};

	/**
	 * General purpose method to set an arbitrary option on the chart at any level,
	 * using "/" characters to designate which level of option you'd like to set.
	 * E.g., the following code:
	 * 
	 * <pre>
	 * <code>
	 * Chart chart = new Chart();
	 * chart.setOption("/chart/type", "spline");
	 * chart.setOption("/chart/marginRight", 10);
	 * chart.setOption("/title/text", "Nice Chart");
	 * </code>
	 * </pre>
	 * 
	 * Would result in initializing HighCharts like the following:
	 * 
	 * <pre>
	 * <code>
	 * new HighCharts.Chart({
	 *     chart: {
	 *         type: "spline",
	 *         marginRight: 10
	 *     },
	 *     title: {
	 *         text: "Nice Chart"
	 *     }
	 * });
	 * </code>
	 * </pre>
	 * 
	 * Note that the beginning "/" is optional, so <code>chart.setOption("/thing", "piglet")</code> is
	 * equivalent to <code>chart.setOption("thing", "piglet")</code>.
	 * <p/>
	 * For details on available options see the <a href="http://www.highcharts.com/ref/">Highcharts reference</a>.
	 * <p/>
	 * Note that, when possible, you'll ideally want to use one of the available type specific setter methods instead of this general method. E.g. instead of
	 * doing this:
	 * 
	 * <pre>
	 * <code>
	 * chart.setOption("/chart/type", "spline");
	 * </code>
	 * </pre>
	 * 
	 * Do this instead:
	 * 
	 * <pre>
	 * <code>
	 * chart.setType(Series.Type.SPLINE);
	 * </code>
	 * </pre>
	 * 
	 * @param path The path to the option to set (e.g. "/title/text");
	 * @param value The value to set for the option (can be a String, Number, Boolean, or JSONObject)
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setOption(final String path, final Object value) {
		configurable.setOption(path, value);
		return returnThis();
	}

	/**
	 * Retrieve all of the options that have been configured for this chart instance
	 * as a JSONObject.
	 * 
	 * @return A JSONObject representing all of the configuration options
	 *         that have been set on the instance (will be null if no options have been set)
	 * @since 1.1.3
	 */
	public JSONObject getOptions() {
		return configurable.getOptions();
	}

	// Helper method to avoid having to do the cast and warning handling in multiple places
	private T returnThis() {
		@SuppressWarnings({ "unchecked" }) final T instance = (T) this;
		return instance;
	}

	// We need to maintain a local reference to the plot options in order to handle the potential callback formatter functions
	private AreaPlotOptions areaPlotOptions;
	private AreaRangePlotOptions areaRangePlotOptions;
	private AreaSplinePlotOptions areaSplinePlotOptions;
	private AreaSplineRangePlotOptions areaSplineRangePlotOptions;
	private BarPlotOptions barPlotOptions;
	private ColumnPlotOptions columnPlotOptions;
	private ColumnRangePlotOptions columnRangePlotOptions;
	private LinePlotOptions linePlotOptions;
	private PiePlotOptions piePlotOptions;
	private SeriesPlotOptions seriesPlotOptions;
	private ScatterPlotOptions scatterPlotOptions;
	private SplinePlotOptions splinePlotOptions;

	// The OHLC plot options are managed by the StockChart sub class, but the reference is managed here for rendering
	protected OHLCPlotOptions ohlcPlotOptions;

	/**
	 * Updates the options that all area type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param areaPlotOptions The options to set on the chart as the default settings for all area type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAreaPlotOptions(final AreaPlotOptions areaPlotOptions) {
		this.areaPlotOptions = areaPlotOptions;
		return areaPlotOptions != null && areaPlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/area", areaPlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all area range type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * <p/>
	 * Note that the highcharts-more.js script must be included in your GWT module to use AreaRange charts.
	 * 
	 * @param areaRangePlotOptions The options to set on the chart as the default settings for all area range type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAreaRangePlotOptions(final AreaRangePlotOptions areaRangePlotOptions) {
		this.areaRangePlotOptions = areaRangePlotOptions;
		return areaRangePlotOptions != null && areaRangePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/arearange", areaRangePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all area spline type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param areaSplinePlotOptions The options to set on the chart as the default settings for all area spline type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAreaSplinePlotOptions(final AreaSplinePlotOptions areaSplinePlotOptions) {
		this.areaSplinePlotOptions = areaSplinePlotOptions;
		return areaSplinePlotOptions != null && areaSplinePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/areaspline", areaSplinePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all area spline range type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * <p/>
	 * Note that the highcharts-more.js script must be included in your GWT module to use AreaSplineRange charts.
	 * 
	 * @param areaSplineRangePlotOptions The options to set on the chart as the default settings for all area spline range type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setAreaSplineRangePlotOptions(final AreaSplineRangePlotOptions areaSplineRangePlotOptions) {
		this.areaSplineRangePlotOptions = areaSplineRangePlotOptions;
		return areaSplineRangePlotOptions != null && areaSplineRangePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/areasplinerange", areaSplineRangePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all bar type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param barPlotOptions The options to set on the chart as the default settings for all bar type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setBarPlotOptions(final BarPlotOptions barPlotOptions) {
		this.barPlotOptions = barPlotOptions;
		return barPlotOptions != null && barPlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/bar", barPlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all column type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param columnPlotOptions The options to set on the chart as the default settings for all column type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setColumnPlotOptions(final ColumnPlotOptions columnPlotOptions) {
		this.columnPlotOptions = columnPlotOptions;
		return columnPlotOptions != null && columnPlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/column", columnPlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all column range type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param columnRangePlotOptions The options to set on the chart as the default settings for all column type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setColumnRangePlotOptions(final ColumnRangePlotOptions columnRangePlotOptions) {
		this.columnRangePlotOptions = columnRangePlotOptions;
		return columnRangePlotOptions != null && columnRangePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/columnrange", columnRangePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all line type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param linePlotOptions The options to set on the chart as the default settings for all line type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setLinePlotOptions(final LinePlotOptions linePlotOptions) {
		this.linePlotOptions = linePlotOptions;
		return linePlotOptions != null && linePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/line", linePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all pie type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param piePlotOptions The options to set on the chart as the default settings for all pie type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setPiePlotOptions(final PiePlotOptions piePlotOptions) {
		this.piePlotOptions = piePlotOptions;
		return piePlotOptions != null && piePlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/pie", piePlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the general options that all series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that the general {@link SeriesPlotOptions} type only represents core options that area available for all series types. To control options that are
	 * more specific to each series type, use one of the more specific methods instead (e.g.
	 * {@link #setLinePlotOptions(org.moxieapps.gwt.highcharts.client.plotOptions.LinePlotOptions)},
	 * {@link #setAreaPlotOptions(org.moxieapps.gwt.highcharts.client.plotOptions.AreaPlotOptions)}, etc.)
	 * <p/>
	 * Also note that changing the general plot options on a chart that has already been rendered will only affect series that are subsequently added to the
	 * chart, and therefore will not impact any of the series that are already rendered in the chart.
	 * 
	 * @param seriesPlotOptions The options to set on the chart as the default settings for all series that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSeriesPlotOptions(final SeriesPlotOptions seriesPlotOptions) {
		this.seriesPlotOptions = seriesPlotOptions;
		return seriesPlotOptions != null && seriesPlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/series", seriesPlotOptions.getOptions()) :
				returnThis();
	}

	/**
	 * Updates the options that all scatter type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param scatterPlotOptions The options to set on the chart as the default settings for all scatter type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setScatterPlotOptions(final ScatterPlotOptions scatterPlotOptions) {
		this.scatterPlotOptions = scatterPlotOptions;
		return scatterPlotOptions != null && scatterPlotOptions.getOptions() != null ?
				this.setOption("/plotOptions/scatter", scatterPlotOptions.getOptions()) :
				returnThis();
	}

	private ChartClickEventHandler chartClickEventHandler;

	/**
	 * Set a callback handler that will be invoked whenever the user clicks on the plot background.
	 * Information where the user clicked can then be found through the {@link org.moxieapps.gwt.highcharts.client.events.ChartClickEvent} instance
	 * that is passed to the handler's
	 * {@link org.moxieapps.gwt.highcharts.client.events.ChartClickEventHandler#onClick(org.moxieapps.gwt.highcharts.client.events.ChartClickEvent)} method.
	 * 
	 * @param chartClickEventHandler The handler that should be invoked whenever a click event occurs.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.1.0
	 */
	public T setClickEventHandler(final ChartClickEventHandler chartClickEventHandler) {
		this.chartClickEventHandler = chartClickEventHandler;
		return returnThis();
	}

	private ChartLoadEventHandler chartLoadEventHandler;

	/**
	 * Set a callback handler that will be invoked whenever the chart's loading event is fired.
	 * 
	 * @param chartLoadEventHandler The handler that should be invoked whenever a load event occurs.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.1.0
	 */
	public T setLoadEventHandler(final ChartLoadEventHandler chartLoadEventHandler) {
		this.chartLoadEventHandler = chartLoadEventHandler;
		return returnThis();
	}

	private ChartRedrawEventHandler chartRedrawEventHandler;

	/**
	 * Set a callback handler that will be invoked whenever the chart's redraw event is fired.
	 * 
	 * @param chartRedrawEventHandler The handler that should be invoked whenever a redraw event occurs.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.1.0
	 */
	public T setRedrawEventHandler(final ChartRedrawEventHandler chartRedrawEventHandler) {
		this.chartRedrawEventHandler = chartRedrawEventHandler;
		return returnThis();
	}

	private ChartSelectionEventHandler chartSelectionEventHandler;

	/**
	 * Set a callback handler that will be invoked whenever the chart's selection event is fired.
	 * 
	 * @param chartSelectionEventHandler The handler that should be invoked whenever a selection event occurs.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.1.0
	 */
	public T setSelectionEventHandler(final ChartSelectionEventHandler chartSelectionEventHandler) {
		this.chartSelectionEventHandler = chartSelectionEventHandler;
		return returnThis();
	}

	/**
	 * Updates the options that all spline type series within the chart will use by default. The settings can then
	 * be overridden for each individual series via the {@link Series#setPlotOptions(PlotOptions)} method.
	 * <p/>
	 * Note that changing the plot options on a chart that has already been rendered will only affect series that are subsequently added to the chart (and will
	 * not impact any of the series that are already rendered in the chart.)
	 * 
	 * @param splinePlotOptions The options to set on the chart as the default settings for all spline type series
	 *            that are part of this chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T setSplinePlotOptions(final SplinePlotOptions splinePlotOptions) {
		this.splinePlotOptions = splinePlotOptions;
		return this.setOption("/plotOptions/spline", splinePlotOptions.getOptions());
	}

	// Whether or not to retain the data points associated with each series in local GWT arrays (as
	// well as within the core Highcharts DOM elements)
	private boolean persistent = false;

	/**
	 * By default the data associated with the series of the chart is only maintained within a GWT managed Java
	 * array until the chart is rendered, at which point the data points of the series are maintained
	 * exclusively within the DOM via the Highcharts API. This is primarily intended for memory
	 * savings, to avoid having to keep the all of the data point information in two places. However,
	 * if you need to support dynamically moving a chart from one panel to another within the application
	 * you can set the persistent option to 'true', which will then retain the GWT managed Java array
	 * as well so that when the chart is redrawn in the new panel all of the data will still be available.
	 * 
	 * @return 'true' if the data associated with the points in the series in the chart will be retained locally
	 *         within a GWT managed Java array (as well as within the DOM via the core Highcharts JS library).
	 *         Defaults to 'false' for memory savings.
	 * @since 1.2.0
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * By default the data associated with the series of the chart is only maintained within a GWT managed Java
	 * array until the chart is rendered, at which point the data points of the series are maintained
	 * exclusively within the DOM via the Highcharts API. This is primarily intended for memory
	 * savings, to avoid having to keep the all of the data point information in two places. However,
	 * if you need to support dynamically moving a chart from one panel to another within the application
	 * you can set the persistent option to 'true', which will then retain the GWT managed Java array
	 * as well so that when the chart is redrawn in the new panel all of the data will still be available.
	 * 
	 * @param persistent 'true' to retain the data associated with the points in the series in the chart locally
	 *            within a GWT managed Java array (as well as within the DOM via the core Highcharts JS library).
	 *            Defaults to 'false' for memory savings.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 * @since 1.2.0
	 */
	public T setPersistent(final boolean persistent) {
		this.persistent = persistent;
		return returnThis();
	}

	// Purposefully not using the generic "List" interface here in order to optimize GWT performance.
	private final ArrayList<Series> seriesList = new ArrayList<Series>();

	/**
	 * Create a new data series that can be configured, and then added to this chart instance via the {@link #addSeries(Series)} method.
	 * 
	 * @return The series that was created (which will need to be added to the chart after it is configured
	 *         via {@link #addSeries(Series)}.
	 */
	public Series createSeries() {
		return new Series(this);
	}

	/**
	 * Add the given data series to the chart, using the default options. Note, see the {@link #addSeries(Series, boolean, boolean)} and
	 * {@link #addSeries(Series, boolean, Animation)} for more
	 * control over how the series will be drawn and animated when added to a chart that has already been rendered.
	 * 
	 * @param series The data series to add to the chart, including its general configuration and plot options.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T addSeries(final Series series) {
		return this.addSeries(series, true, true);
	}

	/**
	 * Add a data series to this chart, controlling the redraw and animation options.
	 * 
	 * @param series The data series to add to the chart, including its general configuration and plot options.
	 * @param redraw 'true' to force the chart to redraw immediately, or 'false' to wait until the {@link #redraw()} method is invoked.
	 * @param animation When 'true', the series updating will be animated with default animation options.
	 *            Note, use the {@link #addSeries(Series, boolean, Animation)} method for more control over the animation options.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T addSeries(final Series series, final boolean redraw, final boolean animation) {
		return addSeries(series, redraw, animation ? new Animation() : null);
	}

	/**
	 * Add a data series to this chart, controlling the redraw and animation options.
	 * 
	 * @param series The data series to add to the chart, including its general configuration and plot options.
	 * @param redraw 'true' to force the chart to redraw immediately, or 'false' to wait until the {@link #redraw()} method is invoked.
	 * @param animation Custom animation to use when the series is being updated.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T addSeries(final Series series, final boolean redraw, final Animation animation) {

		// Whether or not we've been rendered yet or not, maintain a reference to all of the series that we're managing
		seriesList.add(series);

		if (isRendered()) {
			final JavaScriptObject seriesOptions = convertSeriesToJSON(series).getJavaScriptObject();
			if (animation == null || animation.getOptions() == null) {
				final boolean animationFlag = animation != null;
				nativeAddSeries(chart, seriesOptions, redraw, animationFlag);
			}
			else {
				final JavaScriptObject animationOptions = animation.getOptions().getJavaScriptObject();
				nativeAddSeries(chart, seriesOptions, redraw, animationOptions);
			}
			series.setRendered(true);

			// Once we're rendered, we're maintaining the point state in the DOM, so we can dump our internal list to save memory
			series.clearInternalPointsList();
		}

		return returnThis();
	}

	/**
	 * Retrieve the series instance within the chart for the given id, or null if no series exists in the chart
	 * with the given id. Note that series ids are set automatically by the framework, so all series instances
	 * are guaranteed to have an id associated with them. <br/>
	 * <br/>
	 * Some event methods (such as {@link org.moxieapps.gwt.highcharts.client.events.SeriesClickEvent#getSeriesId()})
	 * return the unique id of a series, which can then be used in conjunction with this method to access
	 * the series instance associated with the event.
	 * 
	 * @param seriesId The unique id of the series to try and find.
	 * @return The series associated with the given id, or null if no series exist with the given id
	 * @since 1.1.0
	 */
	public Series getSeries(final String seriesId) {
		for (final Series series : seriesList) {
			if (series.getId().equals(seriesId)) { return series; }
		}
		return null;
	}

	/**
	 * Return an array of all the {@link Series} instances that have been added to this chart (either before or
	 * after the chart was rendered), or an empty array if none have been added yet. Note that we're returning
	 * an array instead of a java.util.List as the generic interface types require a bit of a performance hit
	 * in GWT to use.
	 * 
	 * @return An array of the Series instances currently within the chart, or an empty array if none have yet
	 *         been added.
	 */
	public Series[] getSeries() {
		return seriesList.toArray(new Series[seriesList.size()]);
	}

	/**
	 * Remove the given series from the chart, automatically redrawing the chart after the series
	 * has been removed. See the {@link #removeSeries(Series, boolean)} method if you're performing
	 * multiple options on the chart at once and need tighter control over when the chart is redrawn.
	 * 
	 * @param series The series instance to remove from the chart.
	 * @return 'true' if the series was a part of this chart and successfully removed, or 'false' if the
	 *         given series was not a part of this chart and therefore couldn't be removed.
	 */
	public boolean removeSeries(final Series series) {
		return removeSeries(series, true);
	}

	/**
	 * Remove the given series from the chart, explicitly controlling whether or not the chart will
	 * be redrawn after the series is removed.
	 * 
	 * @param series The series instance to remove from the chart.
	 * @param redraw Whether to redraw the chart after the series is removed. When you're performing multiple
	 *            options on the chart, it is highly recommended that the redraw option be set to false, and instead
	 *            explicitly call the {@link Chart#redraw()} method after you're done updating the chart.
	 * @return 'true' if the series was a part of this chart and successfully removed, or 'false' if the
	 *         given series was not a part of this chart and therefore couldn't be removed.
	 */
	public boolean removeSeries(final Series series, final boolean redraw) {
		if (!seriesList.remove(series)) { return false; }
		if (isRendered()) {
			final JavaScriptObject nativeSeries = nativeGet(chart, series.getId());
			if (nativeSeries != null) {
				nativeRemoveSeries(chart, nativeSeries, redraw);
			}
		}
		return true;
	}

	/**
	 * A convenience method to run through and remove all of the series that are currently part of the chart
	 * instance, only performing a redraw after they're all done. See the {@link Series#remove()} or {@link #removeSeries(Series)} method if you instead want to
	 * remove a single series from the chart.
	 * Also see the {@link #removeAllSeries(boolean)} method if you need more control over when the chart
	 * is redrawn.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T removeAllSeries() {
		removeAllSeries(true);
		return returnThis();
	}

	/**
	 * A convenience method to run through and remove all of the series that are currently part of the chart
	 * instance, with explicit control over the animation options. See the {@link Series#remove()} or {@link #removeSeries(Series)} method if you instead want
	 * to remove a single series from the chart.
	 * 
	 * @param redraw Whether to redraw the chart after the series is removed. When you're performing multiple
	 *            options on the chart, it is highly recommended that the redraw option be set to false, and instead
	 *            explicitly call the {@link Chart#redraw()} method after you're done updating the chart.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T removeAllSeries(final boolean redraw) {
		// Clone the collection so that we can remove from the main list while iterating over the clone
		final ArrayList<Series> clonedList = new ArrayList<Series>(seriesList);
		for (final Iterator<Series> iterator = clonedList.iterator(); iterator.hasNext();) {
			final Series series = iterator.next();
			// Only redraw after the vary last series is removed (unless not requested to at all)
			if (iterator.hasNext()) {
				removeSeries(series, false);
			}
			else {
				removeSeries(series, redraw);
			}
		}
		return returnThis();
	}

	/**
	 * Returns an array of all currently selected series in the chart. Series can be selected either programmatically by the {@link Series#select(boolean)}
	 * method or by checking the checkbox next to the legend item if the {@link SeriesPlotOptions#setShowCheckbox(boolean)} option is true.
	 * 
	 * @return An array of the selected Series items, or an empty array if either no series are selected or the
	 *         chart has not yet been rendered.
	 * @since 1.1.0
	 */
	public Series[] getSelectedSeries() {
		final ArrayList<Series> selectedSeries = new ArrayList<Series>();
		if (isRendered()) {
			final JsArrayString selectedSeriesIds = nativeGetSelectedSeriesIds(chart);
			for (int i = 0; i < selectedSeriesIds.length(); i++) {
				final String selectedSeriesId = selectedSeriesIds.get(i);
				for (final Series series : seriesList) {
					if (series.getId().equals(selectedSeriesId)) {
						selectedSeries.add(series);
					}
				}
			}
		}
		return selectedSeries.toArray(new Series[selectedSeries.size()]);
	}

	/**
	 * Returns an array of all currently selected points in the chart. Points can be selected either
	 * programmatically via the {@link Point#select(boolean, boolean)} method or by clicking on them.
	 * 
	 * @return An array of the selected Point items, or an empty array if either no points are selected or the
	 *         chart has not yet been rendered.
	 * @since 1.1.0
	 */
	public Point[] getSelectedPoints() {
		final ArrayList<Point> selectedPoints = new ArrayList<Point>();
		if (isRendered()) {
			final JsArray<JavaScriptObject> nativeSelectedPoints = nativeGetSelectedPoints(chart);
			for (int i = 0; i < nativeSelectedPoints.length(); i++) {
				selectedPoints.add(new Point(nativeSelectedPoints.get(i)));
			}
		}
		return selectedPoints.toArray(new Point[selectedPoints.size()]);
	}

	/**
	 * Exporting module required. Get an SVG string representing the chart.
	 * <p/>
	 * This method has no effect if the chart has not yet been rendered.
	 * 
	 * @return An SVG representation of the chart, or null if the chart has not yet been rendered
	 */
	public String getSVG() {
		if (isRendered()) {
			return nativeGetSVG(chart);
		}
		else {
			return null;
		}
	}

	/**
	 * Hide the loading screen. Options for the loading screen are defined via {@link Chart#setLoading(Loading)}.
	 * Should be used in conjunction with the {@link #showLoading(String)} method.
	 * <p/>
	 * This method has no effect if the chart has not yet been rendered.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T hideLoading() {
		if (isRendered()) {
			nativeHideLoading(chart);
		}
		return returnThis();
	}

	/**
	 * Dim the chart's plot area and show a loading label text. Options for the loading screen are defined via {@link Chart#setLoading(Loading)}. A custom text
	 * can be given as a parameter, otherwise the default
	 * message specified via the {@link Lang} options will be used.
	 * Should be used in conjunction with the {@link #hideLoading()} method.
	 * <p/>
	 * This method has no effect if the chart has not yet been rendered.
	 * 
	 * @param message A custom message to appear as the loading text.
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T showLoading(final String message) {
		if (isRendered()) {
			nativeShowLoading(chart, message);
		}
		return returnThis();
	}

	/**
	 * Exporting module required. Clears away other elements in the page and prints the chart as it is
	 * displayed. By default, when the exporting module is enabled, a button at the upper left calls this method.
	 * <p/>
	 * This method has no effect if the chart has not yet been rendered.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T print() {
		if (isRendered()) {
			nativePrint(chart);
		}
		return returnThis();
	}

	// Purposefully using a specific type instead of the generic List interface for enhanced GWT performance
	private final ArrayList<XAxis> xAxes = new ArrayList<XAxis>();
	private final ArrayList<YAxis> yAxes = new ArrayList<YAxis>();

	/**
	 * Retrieve a reference to the XAxis for this chart (guaranteed non-null), so that it can then be
	 * configured or operated on. Note that you should use this method when you only have one x-axis
	 * to operate on. If you have multiple x-axis, then utilize the {@link #getXAxis(int)} method instead.
	 * 
	 * @return A reference to the XAxis for this chart (never null);
	 */
	public XAxis getXAxis() {
		return getXAxis(0);
	}

	/**
	 * Retrieve a reference to a specific XAxis for this chart (guaranteed non-null), so that it can then be
	 * configured or operated on. Note that you should use this method when you only have multiple x-axis
	 * to operate on. If you have only a single y-axis, then utilize the {@link #getXAxis()} method instead.
	 * 
	 * @param axisIndex The numeric index (zero based) of the XAxis to return.
	 * @return A reference to the XAxis at the given index for this chart (never null).
	 */
	public XAxis getXAxis(final int axisIndex) {
		if (axisIndex < xAxes.size()) { return xAxes.get(axisIndex); }
		for (int i = xAxes.size(); i <= axisIndex; i++) {
			xAxes.add(new XAxis(this));
		}
		return xAxes.get(axisIndex);
	}

	/**
	 * Retrieve a reference to the YAxis for this chart (guaranteed non-null), so that it can then be
	 * configured or operated on. Note that you should use this method when you only have one y-axis
	 * to operate on. If you have multiple y-axis, then utilize the {@link #getYAxis(int)} method instead.
	 * 
	 * @return A reference to the YAxis for this chart (never null);
	 */
	public YAxis getYAxis() {
		return getYAxis(0);
	}

	/**
	 * Retrieve a reference to a specific YAxis for this chart (guaranteed non-null), so that it can then be
	 * configured or operated on. Note that you should use this method when you only have multiple y-axis
	 * to operate on. If you have only a single y-axis, then utilize the {@link #getYAxis()} method instead.
	 * 
	 * @param axisIndex The numeric index (zero based) of the YAxis to return.
	 * @return A reference to the YAxis at the given index for this chart (never null).
	 */
	public YAxis getYAxis(final int axisIndex) {
		if (axisIndex < yAxes.size()) { return yAxes.get(axisIndex); }
		for (int i = yAxes.size(); i <= axisIndex; i++) {
			yAxes.add(new YAxis(this));
		}
		return yAxes.get(axisIndex);
	}

	/**
	 * Get a axis by id
	 * @param axisId the id of the Axis
	 * @return the Axis instance, may be XAxis or YAxis, null if not found
	 */
	private Axis<?> getAxis(final String axisId) {
		if (axisId == null) { return null; }
		for (final XAxis xAxis : xAxes) {
			if (axisId.equals(xAxis.getId())) { return xAxis; }
		}

		for (final YAxis yAxis : yAxes) {
			if (axisId.equals(yAxis.getId())) { return yAxis; }
		}

		return null;
	}

	/**
	 * Returns a pointer to the native Highcharts or Highstock chart instance that this GWT BaseChart
	 * instance is associated with. Note that this method will only return a non-null value if it
	 * is called after the chart has been rendered. For advanced use-cases only.
	 * 
	 * @return The native Highcharts or Highstock JS chart instance that this BaseChart is associated with, or
	 *         null if the chart has not yet been rendered.
	 * @since 1.4.0
	 */
	public JavaScriptObject getNativeChart() {
		return chart;
	}

	/**
	 * Redraw the chart after changes have been done to the data or axis extremes. All methods for
	 * updating axes, series or points have a parameter for redrawing the chart. This is true by default.
	 * But in many cases you want to do more than one operation on the chart before redrawing, for
	 * example add a number of points. In those cases it is a waste of resources to redraw the chart
	 * for each new point added. So you add the points and call <code>redraw()</code> after.
	 * <p/>
	 * The <code>redraw()</code> method only redraws those parts of the chart that are actually changed. If the data of a series is changed and it doesn't
	 * affect the axes, only the series itself is redrawn. If the new data requires the axis extremes to be altered, the axis and all other series depending on
	 * it are redrawn.
	 * 
	 * @return A reference to this {@link BaseChart} instance for convenient method chaining.
	 */
	public T redraw() {
		if (isRendered()) {
			nativeRedraw(chart);
		}
		return returnThis();
	}

	/**
	 * Returns true if the chart has already been rendered to the DOM, or false otherwise.
	 * 
	 * @return 'true' if the chart has been rendered to the DOM
	 */
	public boolean isRendered() {
		return chart != null;
	}

	private JavaScriptObject chart;

	@Override
	protected void onLoad() {

		// Build some arrays that we can pass into the JS function so that it knows how many custom callback functions it needs to wire
		// up on the client side for formatter functions and event handlers.
		final JSONArray xAxisLabelFormatters = new JSONArray();
		final JSONArray xAxisEventHandlers = new JSONArray();
		for (int i = 0, xAxesSize = xAxes.size(); i < xAxesSize; i++) {
			final XAxis xAxis = xAxes.get(i);
			xAxisLabelFormatters.set(i, JSONBoolean.getInstance(xAxis.getLabels() != null && xAxis.getLabels().getFormatter() != null));

			final JSONObject axisEventHandlers = new JSONObject();
			axisEventHandlers.put("setExtremes", JSONBoolean.getInstance(xAxis.getAxisSetExtremesEventHandler() != null));
			xAxisEventHandlers.set(i, axisEventHandlers);
		}
		final JSONArray yAxisLabelFormatters = new JSONArray();
		final JSONArray yAxisStackLabelFormatters = new JSONArray();
		final JSONArray yAxisEventHandlers = new JSONArray();
		for (int i = 0, yAxesSize = yAxes.size(); i < yAxesSize; i++) {
			final YAxis yAxis = yAxes.get(i);
			yAxisLabelFormatters.set(i, JSONBoolean.getInstance(yAxis.getLabels() != null && yAxis.getLabels().getFormatter() != null));
			yAxisStackLabelFormatters.set(i, JSONBoolean.getInstance(yAxis.getStackLabels() != null && yAxis.getStackLabels().getFormatter() != null));

			final JSONObject axisEventHandlers = new JSONObject();
			axisEventHandlers.put("setExtremes", JSONBoolean.getInstance(yAxis.getAxisSetExtremesEventHandler() != null));
			yAxisEventHandlers.set(i, axisEventHandlers);
		}

		// Build a similar object for dealing with all of the data label formatters that may be set on the plot options
		final JSONObject plotOptionsLabelFormatters = new JSONObject();
		plotOptionsLabelFormatters.put("area", hasDataLabelsFormatter(areaPlotOptions));
		plotOptionsLabelFormatters.put("arearange", hasDataLabelsFormatter(areaRangePlotOptions));
		plotOptionsLabelFormatters.put("areaspline", hasDataLabelsFormatter(areaSplinePlotOptions));
		plotOptionsLabelFormatters.put("areasplinerange", hasDataLabelsFormatter(areaSplineRangePlotOptions));
		plotOptionsLabelFormatters.put("bar", hasDataLabelsFormatter(barPlotOptions));
		plotOptionsLabelFormatters.put("column", hasDataLabelsFormatter(columnPlotOptions));
		plotOptionsLabelFormatters.put("columnrange", hasDataLabelsFormatter(columnRangePlotOptions));
		plotOptionsLabelFormatters.put("line", hasDataLabelsFormatter(linePlotOptions));
		plotOptionsLabelFormatters.put("pie", hasDataLabelsFormatter(piePlotOptions));
		plotOptionsLabelFormatters.put("ohlc", hasDataLabelsFormatter(ohlcPlotOptions));
		plotOptionsLabelFormatters.put("series", hasDataLabelsFormatter(seriesPlotOptions));
		plotOptionsLabelFormatters.put("scatter", hasDataLabelsFormatter(scatterPlotOptions));
		plotOptionsLabelFormatters.put("spline", hasDataLabelsFormatter(splinePlotOptions));

		// And one more for dealing with any data label formatters that have been applied directly to a series
		final JSONArray seriesLabelFormatters = new JSONArray();
		for (int i = 0, seriesListSize = seriesList.size(); i < seriesListSize; i++) {
			final Series series = seriesList.get(i);
			seriesLabelFormatters.set(i, hasDataLabelsFormatter(series));
		}

		// Another one for events fired on the chart
		final JSONObject chartEventHandlers = new JSONObject();
		chartEventHandlers.put("click", JSONBoolean.getInstance(chartClickEventHandler != null));
		chartEventHandlers.put("load", JSONBoolean.getInstance(chartLoadEventHandler != null));
		chartEventHandlers.put("redraw", JSONBoolean.getInstance(chartRedrawEventHandler != null));
		chartEventHandlers.put("selection", JSONBoolean.getInstance(chartSelectionEventHandler != null));

		// And two more for events that have been applied to the series (or the points within the series)
		final JSONObject seriesEventHandlers = new JSONObject();
		final JSONObject pointEventHandlers = new JSONObject();

		if (seriesPlotOptions != null) {

			// Series event
			seriesEventHandlers.put("click",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesClickEventHandler() != null)
					);
			seriesEventHandlers.put("checkboxClick",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesCheckboxClickEventHandler() != null)
					);
			seriesEventHandlers.put("hide",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesHideEventHandler() != null)
					);
			seriesEventHandlers.put("legendItemClick",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesLegendItemClickEventHandler() != null)
					);
			seriesEventHandlers.put("mouseOver",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesMouseOverEventHandler() != null)
					);
			seriesEventHandlers.put("mouseOut",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesMouseOutEventHandler() != null)
					);
			seriesEventHandlers.put("show",
					JSONBoolean.getInstance(seriesPlotOptions.getSeriesShowEventHandler() != null)
					);

			// Point events
			pointEventHandlers.put("click",
					JSONBoolean.getInstance(seriesPlotOptions.getPointClickEventHandler() != null)
					);
			pointEventHandlers.put("mouseOver",
					JSONBoolean.getInstance(seriesPlotOptions.getPointMouseOverEventHandler() != null)
					);
			pointEventHandlers.put("mouseOut",
					JSONBoolean.getInstance(seriesPlotOptions.getPointMouseOutEventHandler() != null)
					);
			pointEventHandlers.put("remove",
					JSONBoolean.getInstance(seriesPlotOptions.getPointRemoveEventHandler() != null)
					);
			pointEventHandlers.put("select",
					JSONBoolean.getInstance(seriesPlotOptions.getPointSelectEventHandler() != null)
					);
			pointEventHandlers.put("unselect",
					JSONBoolean.getInstance(seriesPlotOptions.getPointUnselectEventHandler() != null)
					);
			pointEventHandlers.put("update",
					JSONBoolean.getInstance(seriesPlotOptions.getPointUpdateEventHandler() != null)
					);
		}

		// Pie charts support one additional point event
		if (piePlotOptions != null) {
			pointEventHandlers.put("legendItemClick",
					JSONBoolean.getInstance(piePlotOptions.getPointLegendItemClickEventHandler() != null)
					);
		}

		chart = nativeRenderChart(
				getChartTypeName(),
				createNativeOptions(),
				toolTip != null && toolTip.getToolTipFormatter() != null,
				legend != null && legend.getLabelsFormatter() != null,
				chartEventHandlers.getJavaScriptObject(),
				seriesEventHandlers.getJavaScriptObject(),
				pointEventHandlers.getJavaScriptObject(),
				xAxisEventHandlers.getJavaScriptObject(),
				yAxisEventHandlers.getJavaScriptObject(),
				xAxisLabelFormatters.getJavaScriptObject(),
				yAxisLabelFormatters.getJavaScriptObject(),
				yAxisStackLabelFormatters.getJavaScriptObject(),
				plotOptionsLabelFormatters.getJavaScriptObject(),
				seriesLabelFormatters.getJavaScriptObject()
				);

		// Now that we're rendered we're going to switch to maintaining everything within the DOM, so we can dump
		// any series data that we were managing internally
		for (final Series series : seriesList) {
			series.clearInternalPointsList();
			series.setRendered(true);
		}

	}

	/**
	 * To be overridden in a sub class to return the JS type name of the chart instance that
	 * should be created when the chart is rendered. E.g. "Chart", "StockChart", etc.
	 * 
	 * @return The name of the JS class type that should be created when this chart is rendered.
	 */
	protected abstract String getChartTypeName();

	private JSONBoolean hasDataLabelsFormatter(final PlotOptions plotOptions) {
		return JSONBoolean.getInstance(plotOptions != null &&
				plotOptions.getDataLabels() != null &&
				plotOptions.getDataLabels().getFormatter() != null
				);
	}

	private JSONBoolean hasDataLabelsFormatter(final Series series) {
		return JSONBoolean.getInstance(series != null &&
				series.getPlotOptions() != null &&
				series.getPlotOptions().getDataLabels() != null &&
				series.getPlotOptions().getDataLabels().getFormatter() != null
				);
	}

	@Override
	protected void onUnload() {
		if (isRendered()) {
			nativeDestroy(chart);
			chart = null;

			// If they're going to move the widget to another parent, make sure that everything
			// starts off as if it's unrendered...
			for (final Series series : seriesList) {
				series.setRendered(false);
			}
		}
	}

	// IMPORTANT Added to be able to select tooltip programmaticaly
	public T refreshTooltip(final int seriesIndex, final int pointIndex) {
		if (isRendered()) {
			nativeRefreshTooltip(chart, seriesIndex, pointIndex);
		}
		return returnThis();
	}

	// IMPORTANT Added to be able to select tooltip programmaticaly
	private static native void nativeRefreshTooltip(JavaScriptObject chart, int seriesIndex, int pointIndex) /*-{
		chart.tooltip.refresh([ chart.series[seriesIndex].data[pointIndex] ]);
	}-*/;

	private JavaScriptObject createNativeOptions() {
		JSONObject options = getOptions();
		if (options == null) {
			options = new JSONObject();
		}

		// #1: We need to merge the options they provided with the additional detail we know internally,
		// and first we need to set which DOM element the chart should be rendered within
		JSONValue chartValue = options.get("chart");
		if (chartValue == null || chartValue.isObject() == null) {
			chartValue = new JSONObject();
			options.put("chart", chartValue);
		}
		final JSONObject chartObject = (JSONObject) options.get("chart");
		chartObject.put("renderTo", new JSONString(this.getElement().getId()));

		// #2: We need to setup whatever data series needs to be rendered initially in the chart
		if (seriesList.size() > 0) {
			final JSONValue seriesValue = options.get("series");
			if (seriesValue == null || seriesValue.isArray() == null) {
				options.put("series", new JSONArray());
			}
			final JSONArray seriesArray = (JSONArray) options.get("series");
			for (int i = 0, seriesListSize = seriesList.size(); i < seriesListSize; i++) {
				final Series series = seriesList.get(i);
				final JSONObject seriesOptions = convertSeriesToJSON(series);
				seriesArray.set(i, seriesOptions);
			}
		}

		// In the case that they're going to wait to access the X/Y axis after the chart is rendered,
		// we need to call the 'get' methods once (at least for the primary X/Y axis) so that the ids
		// are set on the Highcharts options correctly
		this.getXAxis();
		this.getYAxis();

		// #3: We need to add references to our axis so that we can later lookup the
		// axis by id (as well as pass along any configuration options that were applied to the axis)
		final JSONValue xAxisJSONValue = convertToJSONValue(xAxes.toArray(new Configurable[xAxes.size()]));
		if (xAxisJSONValue != null && xAxisJSONValue.isNull() == null) {
			options.put("xAxis", xAxisJSONValue);
		}
		final JSONValue yAxisJSONValue = convertToJSONValue(yAxes.toArray(new Configurable[yAxes.size()]));
		if (yAxisJSONValue != null && yAxisJSONValue.isNull() == null) {
			options.put("yAxis", yAxisJSONValue);
		}

		// For debugging the raw options that we're passing to the chart on startup, uncomment the following line
		// com.google.gwt.user.client.Window.alert(options.toString());

		return options.getJavaScriptObject();
	}

	private JSONObject convertSeriesToJSON(final Series series) {
		JSONObject seriesOptions = series.getOptions();
		if (seriesOptions == null) {
			seriesOptions = new JSONObject();
		}
		final JSONValue dataValue = seriesOptions.get("data");
		if (dataValue == null || dataValue.isArray() == null) {
			seriesOptions.put("data", new JSONArray());
		}
		copyPointsToJSONArray(series.getPoints(), (JSONArray) seriesOptions.get("data"));
		return seriesOptions;
	}

	private static JSONValue convertToJSONValue(final Configurable... configurables) {
		if (configurables.length > 1) {
			final JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < configurables.length; i++) {
				jsonArray.set(i, configurables[i].getOptions());
			}
			return jsonArray;
		}
		else if (configurables.length == 1) { return configurables[0].getOptions(); }
		return JSONNull.getInstance();
	}

	private void copyPointsToJSONArray(final Point[] points, final JSONArray jsonArray) {
		for (int i = 0, pointsLength = points.length; i < pointsLength; i++) {
			final Point point = points[i];
			jsonArray.set(i, convertPointToJSON(point));
		}
	}

	// Purposefully package scope so we can get to this method from the Series and Point classes as well
	JSONValue convertPointToJSON(final Point point) {
		JSONObject options = point.getOptions();
		if (options != null) {
			addPointScalarValues(point, options);
			return addPointId(point, options);
		}
		else if (point.hasNativeProperties()) {
			options = new JSONObject();
			addPointScalarValues(point, options);
			addPointId(point, options);
			return Point.addPointNativeProperties(point, options);
		}
		else if (isPersistent()) {
			options = new JSONObject();
			addPointScalarValues(point, options);
			return addPointId(point, options);
		}

		final JSONArray jsonArray = new JSONArray();
		switch (point.getType()) {
			case Y:
				return convertNumberToJSONValue(point.getY());
			case X_Y:
				jsonArray.set(0, convertNumberToJSONValue(point.getX()));
				jsonArray.set(1, convertNumberToJSONValue(point.getY()));
				return jsonArray;
			case X_LOW_HIGH:
				jsonArray.set(0, convertNumberToJSONValue(point.getX()));
				jsonArray.set(1, convertNumberToJSONValue(point.getLow()));
				jsonArray.set(2, convertNumberToJSONValue(point.getHigh()));
				return jsonArray;
			case X_OPEN_HIGH_LOW_CLOSE:
				jsonArray.set(0, convertNumberToJSONValue(point.getX()));
				jsonArray.set(1, convertNumberToJSONValue(point.getOpen()));
				jsonArray.set(2, convertNumberToJSONValue(point.getHigh()));
				jsonArray.set(3, convertNumberToJSONValue(point.getLow()));
				jsonArray.set(4, convertNumberToJSONValue(point.getClose()));
				return jsonArray;
			default:
				// Should not reach this statement based on the structure of the Point class constructors
				throw new IllegalArgumentException("Unable to convert point to JSON due to an invalid type");
		}
	}

	// Purposefully package scope
	JSONValue addPointId(final Point point, final JSONObject options) {
		if (isPersistent()) {
			String id = point.getId();
			if (id == null) {
				id = Document.get().createUniqueId();
				point.setId(id);
			}
			options.put("id", new JSONString(id));
		}
		return options;
	}

	// Purposefully package scope
	static JSONValue addPointScalarValues(final Point point, final JSONObject options) {
		final Point.Type type = point.getType();

		if (type != Point.Type.Y) {
			options.put("x", convertNumberToJSONValue(point.getX()));
		}

		if (type == Point.Type.Y || type == Point.Type.X_Y) {
			options.put("y", convertNumberToJSONValue(point.getY()));
		}

		if (type == Point.Type.X_LOW_HIGH || type == Point.Type.X_OPEN_HIGH_LOW_CLOSE) {
			options.put("high", convertNumberToJSONValue(point.getHigh()));
			options.put("low", convertNumberToJSONValue(point.getLow()));

			if (type == Point.Type.X_OPEN_HIGH_LOW_CLOSE) {
				options.put("open", convertNumberToJSONValue(point.getOpen()));
				options.put("close", convertNumberToJSONValue(point.getClose()));
			}
		}

		return options;
	}

	// Purposefully package scope
	static JSONValue convertNumberToJSONValue(final Number number) {
		if (number != null) {
			return new JSONNumber(number.doubleValue());
		}
		else {
			return JSONNull.getInstance();
		}
	}

	// Purposefully constrained to package scope
	JavaScriptObject get(final String id) {
		if (isRendered()) {
			return nativeGet(chart, id);
		}
		else {
			return null;
		}
	}

	private native JavaScriptObject nativeRenderChart(String chartTypeName,
			JavaScriptObject options,
			boolean toolTipFormatterFlag,
			boolean legendLabelsFormatterFlag,
			JavaScriptObject chartEventHandlerFlags,
			JavaScriptObject seriesEventHandlerFlags,
			JavaScriptObject pointEventHandlerFlags,
			JavaScriptObject xAxisEventHandlerFlags,
			JavaScriptObject yAxisEventHandlerFlags,
			JavaScriptObject xAxisLabelFormatterFlags,
			JavaScriptObject yAxisLabelFormatterFlags,
			JavaScriptObject yAxisStackLabelFormatterFlags,
			JavaScriptObject plotOptionsLabelsFormatterFlags,
			JavaScriptObject seriesLabelsFormatterFlags) /*-{

		var self = this;

		// Add in GWT interceptor callback functions for the various core chart event handlers
		for ( var type1 in chartEventHandlerFlags) {
			if (type1.indexOf("gwt") < 0 && chartEventHandlerFlags[type1]) {
				options.chart.events = options.chart.events || {};
				options.chart.events[type1] = function(e) {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::chartEventCallback(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(e, arguments.callee.type);
				};
				options.chart.events[type1].type = type1;
			}
		}

		// Add in GWT interceptor callback functions for the various series event handlers
		for ( var type2 in seriesEventHandlerFlags) {
			if (type2.indexOf("gwt") < 0 && seriesEventHandlerFlags[type2]) {
				options.plotOptions = options.plotOptions || {};
				options.plotOptions.series = options.plotOptions.series || {};
				options.plotOptions.series.events = options.plotOptions.series.events
						|| {};
				options.plotOptions.series.events[type2] = function(e) {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::seriesEventCallback(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(this, e, arguments.callee.type);
				};
				options.plotOptions.series.events[type2].type = type2;
			}
		}

		// Add in GWT interceptor callback functions for the various series point event handlers
		for ( var type3 in pointEventHandlerFlags) {
			if (type3.indexOf("gwt") < 0 && pointEventHandlerFlags[type3]) {
				options.plotOptions = options.plotOptions || {};
				options.plotOptions.series = options.plotOptions.series || {};
				options.plotOptions.series.point = options.plotOptions.series.point
						|| {};
				options.plotOptions.series.point.events = options.plotOptions.series.point.events
						|| {};
				options.plotOptions.series.point.events[type3] = function(e) {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::pointEventCallback(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(this, e, arguments.callee.type);
				};
				options.plotOptions.series.point.events[type3].type = type3;
			}
		}

		// Add in GWT interceptor callback functions for the various X axis event handlers
		for (i = 0; i < xAxisEventHandlerFlags.length; i++) {
			if (!xAxisEventHandlerFlags[i])
				continue;
			var xAxisA = xAxisEventHandlerFlags.length == 1 ? options.xAxis
					: options.xAxis[i];
			xAxisA.events = xAxisA.events || {};
			for ( var type4 in xAxisEventHandlerFlags[i]) {
				xAxisA.events[type4] = function(e) {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::axisEventCallback(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(this.options.id, e, arguments.callee.type);
				};
				xAxisA.events[type4].type = type4;
			}
		}

		// Add in GWT interceptor callback functions for the various Y axis event handlers
		for (i = 0; i < yAxisEventHandlerFlags.length; i++) {
			if (!yAxisEventHandlerFlags[i])
				continue;
			var yAxisA = yAxisEventHandlerFlags.length == 1 ? options.yAxis
					: options.yAxis[i];
			yAxisA.events = yAxisA.events || {};
			for ( var type5 in yAxisEventHandlerFlags[i]) {
				yAxisA.events[type5] = function(e) {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::axisEventCallback(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(this.options.id, e, arguments.callee.type);
				};
				yAxisA.events[type5].type = type5;
			}
		}

		// Add in GWT interceptor callback functions for the various formatters so that we can move from
		// the native JS world back to the Java world...
		if (toolTipFormatterFlag) {
			options.tooltip = options.tooltip || {};
			options.tooltip.formatter = function() {
				var result = self.@org.moxieapps.gwt.highcharts.client.BaseChart::toolTipFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(this);
				if (result == null) {
					return false;
				}
				return result;
			};
		}
		if (legendLabelsFormatterFlag) {
			options.legend = options.legend || {};
			options.legend.labelFormatter = function() {
				var result = self.@org.moxieapps.gwt.highcharts.client.BaseChart::legendLabelsFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(this);
				if (result == null) {
					return false;
				}
				return result;
			};
		}

		// X axis label formatters
		for (i = 0; i < xAxisLabelFormatterFlags.length; i++) {
			if (!xAxisLabelFormatterFlags[i])
				continue;
			var xAxisB = xAxisLabelFormatterFlags.length == 1 ? options.xAxis
					: options.xAxis[i];
			xAxisB.labels = xAxisB.labels || {};
			xAxisB.labels.formatter = function() {
				return self.@org.moxieapps.gwt.highcharts.client.BaseChart::xAxisLabelFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;I)(this, arguments.callee.index);
			};
			xAxisB.labels.formatter.index = i;
		}

		// Y axis label formatters
		for (i = 0; i < yAxisLabelFormatterFlags.length
				&& i < yAxisStackLabelFormatterFlags.length; i++) {
			var yAxisB = yAxisLabelFormatterFlags.length == 1 ? options.yAxis
					: options.yAxis[i];
			if (yAxisLabelFormatterFlags[i]) {
				yAxisB.labels = yAxisB.labels || {};
				yAxisB.labels.formatter = function() {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::yAxisLabelFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;I)(this, arguments.callee.index);
				};
				yAxisB.labels.formatter.index = i;
			}
			if (yAxisStackLabelFormatterFlags[i]) {
				yAxisB.stackLabels = yAxisB.stackLabels || {};
				yAxisB.stackLabels.formatter = function() {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::yAxisStackLabelFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;I)(this, arguments.callee.index);
				};
				yAxisB.stackLabels.formatter.index = i;
			}
		}

		// Plot options data label formatters
		for ( var type6 in plotOptionsLabelsFormatterFlags) {
			if (type6.indexOf("gwt") < 0
					&& plotOptionsLabelsFormatterFlags[type6]) {
				options.plotOptions = options.plotOptions || {};
				options.plotOptions[type6] = options.plotOptions[type6] || {};
				options.plotOptions[type6].dataLabels = options.plotOptions[type6].dataLabels
						|| {};
				options.plotOptions[type6].dataLabels.formatter = function() {
					return self.@org.moxieapps.gwt.highcharts.client.BaseChart::plotOptionsLabelsFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(this, arguments.callee.type);
				};
				options.plotOptions[type6].dataLabels.formatter.type = type6;
			}
		}

		// Series override label formatters
		for (i = 0; i < seriesLabelsFormatterFlags.length; i++) {
			if (!seriesLabelsFormatterFlags[i])
				continue;
			var series = options.series[i];
			series.dataLabels = series.dataLabels || {};
			series.dataLabels.formatter = function() {
				return self.@org.moxieapps.gwt.highcharts.client.BaseChart::seriesLabelsFormatterCallback(Lcom/google/gwt/core/client/JavaScriptObject;I)(this, arguments.callee.index);
			};
			series.dataLabels.formatter.index = i;
		}

		// Draw the chart!
		return new $wnd.Highcharts[chartTypeName](options);
	}-*/;

	private boolean chartEventCallback(final JavaScriptObject nativeEvent, final String eventType) {
		if ("click".equals(eventType) && chartClickEventHandler != null) {
			return chartClickEventHandler.onClick(new ChartClickEvent(nativeEvent));
		}
		else if ("load".equals(eventType) && chartLoadEventHandler != null) {
			return chartLoadEventHandler.onLoad(new ChartLoadEvent(nativeEvent));
		}
		else if ("redraw".equals(eventType) && chartRedrawEventHandler != null) {
			return chartRedrawEventHandler.onRedraw(new ChartRedrawEvent(nativeEvent));
		}
		else if ("selection".equals(eventType) && chartSelectionEventHandler != null) { return chartSelectionEventHandler.onSelection(new ChartSelectionEvent(
				nativeEvent)); }
		return true;
	}

	private boolean seriesEventCallback(final JavaScriptObject nativeSeries, final JavaScriptObject nativeEvent, final String eventType) {
		if ("click".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesClickEventHandler() != null) {
			return seriesPlotOptions.getSeriesClickEventHandler().onClick(new SeriesClickEvent(nativeEvent, nativeSeries));
		}
		else if ("checkboxClick".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesCheckboxClickEventHandler() != null) {
			return seriesPlotOptions.getSeriesCheckboxClickEventHandler().onClick(new SeriesCheckboxClickEvent(nativeEvent, nativeSeries));
		}
		else if ("hide".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesHideEventHandler() != null) {
			return seriesPlotOptions.getSeriesHideEventHandler().onHide(new SeriesHideEvent(nativeEvent, nativeSeries));
		}
		else if ("legendItemClick".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesLegendItemClickEventHandler() != null) {
			return seriesPlotOptions.getSeriesLegendItemClickEventHandler().onClick(new SeriesLegendItemClickEvent(nativeEvent, nativeSeries));
		}
		else if ("mouseOver".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesMouseOverEventHandler() != null) {
			return seriesPlotOptions.getSeriesMouseOverEventHandler().onMouseOver(new SeriesMouseOverEvent(nativeEvent, nativeSeries));
		}
		else if ("mouseOut".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesMouseOutEventHandler() != null) {
			return seriesPlotOptions.getSeriesMouseOutEventHandler().onMouseOut(new SeriesMouseOutEvent(nativeEvent, nativeSeries));
		}
		else if ("show".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getSeriesShowEventHandler() != null) { return seriesPlotOptions
				.getSeriesShowEventHandler().onShow(new SeriesShowEvent(nativeEvent, nativeSeries)); }
		return true;
	}

	private boolean pointEventCallback(final JavaScriptObject nativePoint, final JavaScriptObject nativeEvent, final String eventType) {
		if ("click".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointClickEventHandler() != null) {
			return seriesPlotOptions.getPointClickEventHandler().onClick(new PointClickEvent(nativeEvent, nativePoint));
		}
		else if ("mouseOver".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointMouseOverEventHandler() != null) {
			return seriesPlotOptions.getPointMouseOverEventHandler().onMouseOver(new PointMouseOverEvent(nativeEvent, nativePoint));
		}
		else if ("mouseOut".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointMouseOutEventHandler() != null) {
			return seriesPlotOptions.getPointMouseOutEventHandler().onMouseOut(new PointMouseOutEvent(nativeEvent, nativePoint));
		}
		else if ("remove".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointRemoveEventHandler() != null) {
			return seriesPlotOptions.getPointRemoveEventHandler().onRemove(new PointRemoveEvent(nativeEvent, nativePoint));
		}
		else if ("select".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointSelectEventHandler() != null) {
			return seriesPlotOptions.getPointSelectEventHandler().onSelect(new PointSelectEvent(nativeEvent, nativePoint));
		}
		else if ("unselect".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointUnselectEventHandler() != null) {
			return seriesPlotOptions.getPointUnselectEventHandler().onUnselect(new PointUnselectEvent(nativeEvent, nativePoint));
		}
		else if ("update".equals(eventType) && seriesPlotOptions != null && seriesPlotOptions.getPointUpdateEventHandler() != null) {
			return seriesPlotOptions.getPointUpdateEventHandler().onUpdate(new PointUpdateEvent(nativeEvent, nativePoint));
		}
		else if ("legendItemClick".equals(eventType) && piePlotOptions != null && piePlotOptions.getPointLegendItemClickEventHandler() != null) { return piePlotOptions
				.getPointLegendItemClickEventHandler().onClick(new PointLegendItemClickEvent(nativeEvent, nativePoint)); }
		return true;
	}

	private boolean axisEventCallback(final String axisId, final JavaScriptObject nativeEvent, final String eventType) {
		final Axis<?> axis = getAxis(axisId);
		// noinspection SimplifiableIfStatement
		if ("setExtremes".equals(eventType) && axis != null && axis.getAxisSetExtremesEventHandler() != null) { return axis.getAxisSetExtremesEventHandler()
				.onSetExtremes(new AxisSetExtremesEvent(nativeEvent, axis)); }
		return true;
	}

	private String toolTipFormatterCallback(final JavaScriptObject nativeData) {
		if (toolTip == null || toolTip.getToolTipFormatter() == null) { return null; }
		return toolTip.getToolTipFormatter().format(new ToolTipData(nativeData));
	}

	private String legendLabelsFormatterCallback(final JavaScriptObject nativeData) {
		if (legend == null || legend.getLabelsFormatter() == null) { return null; }
		return legend.getLabelsFormatter().format(new LegendLabelsData(nativeData));
	}

	private String xAxisLabelFormatterCallback(final JavaScriptObject nativeData, final int axisIndex) {
		if (xAxes == null || xAxes.size() <= axisIndex ||
				xAxes.get(axisIndex).getLabels() == null ||
				xAxes.get(axisIndex).getLabels().getFormatter() == null) { return null; }
		return xAxes.get(axisIndex).getLabels().getFormatter().format(new AxisLabelsData(nativeData));
	}

	private String yAxisLabelFormatterCallback(final JavaScriptObject nativeData, final int axisIndex) {
		if (yAxes == null || yAxes.size() <= axisIndex ||
				yAxes.get(axisIndex).getLabels() == null ||
				yAxes.get(axisIndex).getLabels().getFormatter() == null) { return null; }
		return yAxes.get(axisIndex).getLabels().getFormatter().format(new AxisLabelsData(nativeData));
	}

	private String yAxisStackLabelFormatterCallback(final JavaScriptObject nativeData, final int axisIndex) {
		if (yAxes == null || yAxes.size() <= axisIndex ||
				yAxes.get(axisIndex).getStackLabels() == null ||
				yAxes.get(axisIndex).getStackLabels().getFormatter() == null) { return null; }
		return yAxes.get(axisIndex).getStackLabels().getFormatter().format(new StackLabelsData(nativeData));
	}

	private String plotOptionsLabelsFormatterCallback(final JavaScriptObject nativeData, final String type) {
		PlotOptions plotOptions = null;
		if ("area".equals(type)) plotOptions = areaPlotOptions;
		if ("arearange".equals(type)) plotOptions = areaRangePlotOptions;
		if ("areaspline".equals(type)) plotOptions = areaSplinePlotOptions;
		if ("areasplinerange".equals(type)) plotOptions = areaSplineRangePlotOptions;
		if ("bar".equals(type)) plotOptions = barPlotOptions;
		if ("column".equals(type)) plotOptions = columnPlotOptions;
		if ("columnrange".equals(type)) plotOptions = columnRangePlotOptions;
		if ("line".equals(type)) plotOptions = linePlotOptions;
		if ("ohlc".equals(type)) plotOptions = ohlcPlotOptions;
		if ("pie".equals(type)) plotOptions = piePlotOptions;
		if ("scatter".equals(type)) plotOptions = scatterPlotOptions;
		if ("series".equals(type)) plotOptions = seriesPlotOptions;
		if ("spline".equals(type)) plotOptions = splinePlotOptions;

		if (plotOptions == null || plotOptions.getDataLabels() == null || plotOptions.getDataLabels().getFormatter() == null) { return null; }
		return plotOptions.getDataLabels().getFormatter().format(new DataLabelsData(nativeData));
	}

	private String seriesLabelsFormatterCallback(final JavaScriptObject nativeData, final int seriesIndex) {
		if (seriesList == null || seriesList.size() <= seriesIndex ||
				seriesList.get(seriesIndex).getPlotOptions() == null ||
				seriesList.get(seriesIndex).getPlotOptions().getDataLabels() == null ||
				seriesList.get(seriesIndex).getPlotOptions().getDataLabels().getFormatter() == null) { return null; }
		return seriesList.get(seriesIndex).getPlotOptions().getDataLabels().getFormatter().format(new DataLabelsData(nativeData));
	}

	private static native void nativeRedraw(JavaScriptObject chart) /*-{
		chart.redraw();
	}-*/;

	private static native void nativeDestroy(JavaScriptObject chart) /*-{
		chart.destroy();
	}-*/;

	private static native void nativeSetSize(JavaScriptObject chart, int width, int height, boolean animated) /*-{
		chart.setSize(width, height, animated);
	}-*/;

	private static native void nativeSetSize(JavaScriptObject chart, int width, int height, JavaScriptObject animation) /*-{
		chart.setSize(width, height, animation);
	}-*/;

	private static native JavaScriptObject nativeGet(JavaScriptObject chart, String id) /*-{
		return chart.get(id);
	}-*/;

	private static native void nativeAddSeries(JavaScriptObject chart, JavaScriptObject seriesOptions, boolean redraw, boolean animationFlag) /*-{
		chart.addSeries(seriesOptions, redraw, animationFlag);
	}-*/;

	private static native void nativeAddSeries(JavaScriptObject chart, JavaScriptObject seriesOptions, boolean redraw, JavaScriptObject animationOptions) /*-{
		chart.addSeries(seriesOptions, redraw, animationOptions);
	}-*/;

	private static native void nativeRemoveSeries(JavaScriptObject chart, JavaScriptObject nativeSeries, boolean redraw) /*-{
		nativeSeries.remove(redraw);
	}-*/;

	private static native void nativeHideLoading(JavaScriptObject chart) /*-{
		chart.hideLoading();
	}-*/;

	private static native void nativeShowLoading(JavaScriptObject chart, String message) /*-{
		chart.showLoading(message);
	}-*/;

	private static native void nativePrint(JavaScriptObject chart) /*-{
		chart.print();
	}-*/;

	private static native JsArrayString nativeGetSelectedSeriesIds(JavaScriptObject chart) /*-{
		var seriesIds = [];
		var selectedSeries = chart.getSelectedSeries();
		for ( var i = 0; i < selectedSeries.length; i++) {
			seriesIds.push(selectedSeries[i].options["id"]);
		}
		return seriesIds;
	}-*/;

	private static native JsArray<JavaScriptObject> nativeGetSelectedPoints(JavaScriptObject chart) /*-{
		return chart.getSelectedPoints();
	}-*/;

	private static native String nativeGetSVG(JavaScriptObject chart) /*-{
		return chart.getSVG();
	}-*/;

	private static native void nativeSetTitle(JavaScriptObject chart, JavaScriptObject title, JavaScriptObject subTitle) /*-{
		chart.setTitle(title, subTitle);
	}-*/;

}
