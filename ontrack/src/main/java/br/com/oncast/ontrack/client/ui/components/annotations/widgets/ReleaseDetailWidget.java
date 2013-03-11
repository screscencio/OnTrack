package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import static br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat.roundFloat;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartDataProvider;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseDetailWidget extends Composite implements SubjectDetailWidget {

	private static ReleaseDetailWidgetUiBinder defaultUiBinder = GWT.create(ReleaseDetailWidgetUiBinder.class);

	@UiTemplate("ReleaseDetailWidgetForReport.ui.xml")
	interface ReleaseDetailWidgetForReportUiBinder extends UiBinder<Widget, ReleaseDetailWidget> {}

	private static ReleaseDetailWidgetForReportUiBinder reportUiBinder = GWT.create(ReleaseDetailWidgetForReportUiBinder.class);

	private static final ReleaseDetailWidgetMessages messages = GWT.create(ReleaseDetailWidgetMessages.class);

	interface ReleaseDetailWidgetUiBinder extends UiBinder<Widget, ReleaseDetailWidget> {}

	@UiField
	HasText parent;

	@UiField
	HasText effort;

	@UiField
	HasText value;

	@UiField
	HasText actualVelocity;

	@UiField
	HasText plannedVelocity;

	@UiField
	HasText period;

	@UiField
	HasText duration;

	@UiField
	HasText cycletime;

	@UiField
	HasText leadtime;

	private Release release;

	private ActionExecutionListener actionExecutionListener;

	private ReleaseChartDataProvider dataProvider;

	public static ReleaseDetailWidget forReport(final Release release) {
		return new ReleaseDetailWidget(reportUiBinder).setSubject(release);
	}

	private ReleaseDetailWidget(final UiBinder<Widget, ReleaseDetailWidget> uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ReleaseDetailWidget() {
		initWidget(defaultUiBinder.createAndBindUi(this));
	}

	public ReleaseDetailWidget setSubject(final Release release) {
		this.release = release;
		this.dataProvider = new ReleaseChartDataProvider(release, new ReleaseEstimator(getRootRelease()), ClientServiceProvider.getInstance()
				.getActionExecutionService());
		update();
		return this;
	}

	private Release getRootRelease() {
		Release root = release;
		while (!root.isRoot())
			root = root.getParent();
		return root;
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ReleaseRenameAction && action.getReferenceId().equals(release.getId())) update();
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		this.parent.setText(release.isRoot() || (release.getParent().isRoot()) ? messages.none() : release.getParent().getDescription());
		this.effort.setText(formatProgressText(release.getAccomplishedEffortSum(), release.getEffortSum(), " ep"));
		this.value.setText(formatProgressText(release.getAccomplishedValueSum(), release.getValueSum(), " vp"));
		this.actualVelocity.setText(dataProvider.hasStarted() ? (roundFloat(dataProvider.getActualVelocity(), 1) + " ep / " + messages.day()) : "---");
		this.plannedVelocity.setText(roundFloat(dataProvider.getEstimatedVelocity(), 1) + " ep / " + messages.day());
		final WorkingDay startDay = dataProvider.getEstimatedStartDay();
		final WorkingDay endDay = dataProvider.getEstimatedEndDay();
		this.period.setText(format(startDay) + " - " + format(endDay));
		this.duration.setText(HumanDateFormatter.getDifferenceText(endDay.getJavaDate().getTime() - startDay.getJavaDate().getTime(), 1));
		final Long cycleTimeAverage = getAverage(getCycletimeExtractor());
		this.cycletime.setText(formatInfo(cycleTimeAverage, getDeviant(getCycletimeExtractor(), cycleTimeAverage)));
		final Long leadtimeAverage = getAverage(getLeadtimeExtractor());
		this.leadtime.setText(formatInfo(leadtimeAverage, getDeviant(getLeadtimeExtractor(), leadtimeAverage)));
	}

	private Extractor getCycletimeExtractor() {
		return new Extractor() {
			@Override
			public Long getValue(final Progress progress) {
				return progress.getCycletime();
			}
		};
	}

	private Extractor getLeadtimeExtractor() {
		return new Extractor() {
			@Override
			public Long getValue(final Progress progress) {
				return progress.getLeadtime();
			}
		};
	}

	private Long getDeviant(final Extractor extractor, final Long average) {
		Long deviant = 0L;
		int scopeCount = 0;
		for (final Scope scope : this.release.getAllScopesIncludingDescendantReleases()) {
			if (!scope.getProgress().isDone()) continue;
			scopeCount++;
			final Long variance = extractor.getValue(scope.getProgress()) - average;
			deviant += variance * variance;
		}

		if (scopeCount <= 1) return null;
		return (long) Math.sqrt(deviant / (scopeCount - 1));
	}

	private Long getAverage(final Extractor extractor) {
		Long average = 0L;
		int scopeCount = 0;
		for (final Scope scope : this.release.getAllScopesIncludingDescendantReleases()) {
			if (!scope.getProgress().isDone()) continue;
			scopeCount++;
			average += extractor.getValue(scope.getProgress());
		}

		if (scopeCount == 0) return null;
		return average / scopeCount;
	}

	private interface Extractor {
		Long getValue(Progress progress);
	}

	private String formatInfo(final Long value, final Long deviation) {
		if (value == null) return "---";
		return format(value) + (" ± " + (deviation == null ? "0" : HumanDateFormatter.getDifferenceText(deviation, 0, true)));
	}

	private String format(final WorkingDay day) {
		return day.getDayMonthShortYearString();
	}

	private String formatProgressText(final float accomplished, final float total, final String unit) {
		if (total == 0) return format(total) + unit;
		final String percentage = accomplished == 0 ? "" : (" (" + format(accomplished * 100 / total) + "%)");
		return format(accomplished) + " / " + format(total) + unit + percentage;
	}

	private String format(final Long value) {
		return HumanDateFormatter.getDifferenceText(value, 0);
	}

	private String format(final float floatValue) {
		return roundFloat(floatValue, 0);
	}

}
