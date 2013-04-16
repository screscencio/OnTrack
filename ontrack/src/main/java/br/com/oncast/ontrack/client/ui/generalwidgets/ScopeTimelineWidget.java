package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.ModelState;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTimelineWidget extends Composite {

	private static final String PERIOD = "period";

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

		String openImpedimentMark();

		String solvedImpedimentMark();

		String timelineMark();
	}

	@UiField
	ScopeTimelineWidgetStyle style;

	@UiField
	HTMLPanel container;

	@UiField
	HorizontalPanel timelineBar;

	private long start;

	private long end;

	private long totalPeriod;

	private final Map<Long, FocusPanel> strokeMap = new HashMap<Long, FocusPanel>();

	public ScopeTimelineWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setScope(final Scope scope) {
		final Progress progress = scope.getProgress();

		start = progress.getCreationDate().getTime();
		end = (progress.isDone() ? progress.getEndDay().getJavaDate() : new Date()).getTime();
		totalPeriod = end - start;

		addProgressStates(progress);

		addImpediments(scope);
	}

	private void addImpediments(final Scope scope) {
		for (final Annotation impediment : ClientServices.get().details().getImpedimentsFor(scope.getId())) {
			if (impediment.getType() == AnnotationType.SOLVED_IMPEDIMENT) {
				addMark(style.solvedImpedimentMark(), impediment.getLastOcuurenceOf(AnnotationType.SOLVED_IMPEDIMENT), impediment.getMessage());
			}
			addMark(style.openImpedimentMark(), impediment.getLastOcuurenceOf(AnnotationType.OPEN_IMPEDIMENT), impediment.getMessage());
		}
	}

	private void addMark(final String styleName, final Date timestamp, final String label) {
		final Label mark = new Label();
		mark.addStyleName(styleName);
		mark.addStyleName(style.timelineMark());
		mark.addStyleName("icon-flag");
		mark.setTitle(HumanDateFormatter.getRelativeDate(timestamp) + " - " + label);
		container.add(mark);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				setPositionRelativeToPertinentStrokeArea(timestamp, mark);
				mark.getElement().getStyle().setMarginLeft(-mark.getOffsetWidth() / 2, Unit.PX);
			}
		});
	}

	public long getRelativePosition(final Date timestamp) {
		return (timestamp.getTime() - start) * 100 / (end - start);
	}

	private void addProgressStates(final Progress progress) {
		FocusPanel previousStroke = null;
		long previousTimestamp = start;
		for (final ModelState<ProgressState> state : progress) {
			final long currentTimestamp = state.getTimestamp().getTime();
			if (previousStroke != null) setProportion(previousStroke, previousTimestamp, currentTimestamp);

			addDot(state);
			previousStroke = addStroke(state);
			previousTimestamp = currentTimestamp;
			strokeMap.put(previousTimestamp, previousStroke);
		}

		if (progress.isDone()) {
			timelineBar.remove(previousStroke);
			strokeMap.remove(previousTimestamp);
		}
		else {
			setProportion(previousStroke, previousTimestamp, end);
			addDot("icon-play " + style.timelineUnfinishedDot(), progress.getState(), new Date(end));
		}
	}

	private void setProportion(final FocusPanel stroke, final long previousTimestamp, final long currentTimestamp) {
		final long currentPeriod = currentTimestamp - previousTimestamp;
		timelineBar.setCellWidth(stroke, "" + (currentPeriod * PROPORTION_SENSITIVITY / totalPeriod));
		stroke.setTitle(HumanDateFormatter.getDifferenceText(currentPeriod, 0));
		stroke.getElement().setPropertyInt(PERIOD, (int) currentPeriod);
	}

	private FocusPanel addStroke(final ModelState<ProgressState> state) {
		final FocusPanel stroke = new FocusPanel();
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

	private void setPositionRelativeToPertinentStrokeArea(final Date timestamp, final Label mark) {
		long closestPreviousTimestamp = 0;
		Widget portionStroke = null;
		for (final Entry<Long, FocusPanel> e : strokeMap.entrySet()) {
			final Long t = e.getKey();
			if (t > timestamp.getTime()) continue;
			if (t > closestPreviousTimestamp) {
				closestPreviousTimestamp = t;
				portionStroke = e.getValue();
			}
		}

		if (portionStroke == null) {
			mark.getElement().getStyle().setLeft(0, Unit.PX);
			return;
		}

		final float start = portionStroke.getAbsoluteLeft();
		float offset = portionStroke.getElement().getPropertyInt(PERIOD);
		if (offset == 0) offset = 1;
		float distance = timestamp.getTime() - closestPreviousTimestamp;
		if (distance > offset) distance = offset;
		final float pct = distance / offset;
		float left = portionStroke.getOffsetWidth() * pct + start;
		left = left - container.getElement().getAbsoluteLeft();

		mark.getElement().getStyle().setLeft(left, Unit.PX);
	}

}
