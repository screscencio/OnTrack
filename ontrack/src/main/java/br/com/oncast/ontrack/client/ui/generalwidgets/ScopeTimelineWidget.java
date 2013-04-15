package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.Date;

import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.ModelState;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTimelineWidget extends Composite {

	private static final int PROPORTION_SENSITIVITY = 10000;

	private static ScopeTimelineWidgetUiBinder uiBinder = GWT.create(ScopeTimelineWidgetUiBinder.class);

	interface ScopeTimelineWidgetUiBinder extends UiBinder<Widget, ScopeTimelineWidget> {}

	interface ScopeTimelineWidgetStyle extends CssResource {
		String timelineNotStarted();

		String timelineUnderwork();

		String timelineDone();

		String timelineDot();

		String timelineStroke();

		String timelineUnfinishedDot();
	}

	@UiField
	ScopeTimelineWidgetStyle style;

	@UiField
	HorizontalPanel timelineBar;

	public ScopeTimelineWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setScope(final Scope scope) {
		final Progress progress = scope.getProgress();

		Widget previousStroke = null;
		long previousTimestamp = progress.getCreationDate().getTime();

		final long end = (progress.isDone() ? progress.getEndDay().getJavaDate() : new Date()).getTime();
		final long totalPeriod = end - previousTimestamp;

		for (final ModelState<ProgressState> state : progress) {
			final long currentTimestamp = state.getTimestamp().getTime();
			if (previousStroke != null) setProportion(previousStroke, totalPeriod, previousTimestamp, currentTimestamp);

			addDot(state);
			previousStroke = addStroke(state);
			previousTimestamp = currentTimestamp;
		}

		if (progress.isDone()) timelineBar.remove(previousStroke);
		else {
			setProportion(previousStroke, totalPeriod, previousTimestamp, end);
			addDot("icon-play " + style.timelineUnfinishedDot(), progress.getState(), new Date(end));
		}
	}

	private void setProportion(final Widget stroke, final long totalPeriod, final long previousTimestamp, final long currentTimestamp) {
		final long currentPeriod = currentTimestamp - previousTimestamp;
		timelineBar.setCellWidth(stroke, "" + (currentPeriod * PROPORTION_SENSITIVITY / totalPeriod));
		stroke.setTitle(HumanDateFormatter.getDifferenceText(currentPeriod, 0));
	}

	private Widget addStroke(final ModelState<ProgressState> state) {
		final Widget stroke = new FocusPanel();
		stroke.addStyleName(style.timelineStroke());
		stroke.addStyleName(getColorStyle(state.getValue()));
		timelineBar.add(stroke);
		return stroke;
	}

	private void addDot(final ModelState<ProgressState> state) {
		addDot(style.timelineDot(), state.getValue(), state.getTimestamp());
	}

	private void addDot(final String styleName, final ProgressState state, final Date timestamp) {
		final Widget dot = new FocusPanel();
		dot.addStyleName(styleName);
		dot.addStyleName(getColorStyle(state));
		dot.setTitle(HumanDateFormatter.getRelativeDate(timestamp) + " - " + state.getLabel());
		timelineBar.add(dot);
	}

	private String getColorStyle(final ProgressState state) {
		if (state == ProgressState.NOT_STARTED) return style.timelineNotStarted();
		if (state == ProgressState.UNDER_WORK) return style.timelineUnderwork();
		return style.timelineDone();
	}

}
