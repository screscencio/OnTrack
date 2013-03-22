package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ReleaseChartDataProvider {

	private static final int MAX_NUMBER_OF_DAYS = 128;
	private final Release release;
	private final ReleaseEstimator releaseEstimator;
	private HashMap<WorkingDay, Float> accomplishedEffortByDate;
	private HashMap<WorkingDay, Float> accomplishedValueByDate;
	private List<WorkingDay> releaseDays;
	private final ActionExecutionService actionService;

	public ReleaseChartDataProvider(final Release release, final ReleaseEstimator estimator, final ActionExecutionService actionService) {
		this.release = release;
		this.releaseEstimator = estimator;
		this.actionService = actionService;
	}

	public float getEffortSum() {
		return release.getEffortSum();
	}

	public WorkingDay getInferedEstimatedEndDay() {
		return releaseEstimator.getEstimatedEndDayUsingInferedEstimatedVelocity(release);
	}

	public float getEstimatedVelocity() {
		return release.hasDeclaredEstimatedVelocity() ? release.getEstimatedVelocity() : releaseEstimator
				.getEstimatedVelocity(release);
	}

	public WorkingDay getEstimatedStartDay() {
		return releaseEstimator.getEstimatedStartDayFor(release);
	}

	public void declareStartDate(final Date date) {
		actionService.onUserActionExecutionRequest(new ReleaseDeclareStartDayAction(release.getId(), date));
	}

	public void declareEndDate(final Date date) {
		actionService.onUserActionExecutionRequest(new ReleaseDeclareEndDayAction(release.getId(), date));
	}

	public WorkingDay getEstimatedEndDay() {
		return releaseEstimator.getEstimatedEndDayFor(release);
	}

	public boolean hasDeclaredStartDay() {
		return release.hasDeclaredStartDay();
	}

	public boolean hasDeclaredEndDay() {
		return release.hasDeclaredEndDay();
	}

	public void declareEstimatedVelocity(final Float velocity) {
		actionService.onUserActionExecutionRequest(new ReleaseDeclareEstimatedVelocityAction(release.getId(), velocity));
	}

	public List<WorkingDay> getReleaseDays() {
		if (releaseDays == null) evaluateData();
		return new ArrayList<WorkingDay>(releaseDays);
	}

	public Map<WorkingDay, Float> getAccomplishedValuePointsByDate() {
		if (accomplishedValueByDate == null) evaluateData();
		return accomplishedValueByDate;
	}

	public Map<WorkingDay, Float> getAccomplishedEffortPointsByDate() {
		if (accomplishedEffortByDate == null) evaluateData();
		return accomplishedEffortByDate;
	}

	public boolean hasDeclaredEstimatedVelocity() {
		return release.hasDeclaredEstimatedVelocity();
	}

	public Float getActualVelocity() {
		return release.getActualVelocity();
	}

	public float getValueSum() {
		return release.getValueSum();
	}

	public void evaluateData() {
		accomplishedValueByDate = new LinkedHashMap<WorkingDay, Float>();
		accomplishedEffortByDate = new LinkedHashMap<WorkingDay, Float>();

		final float effortSum = getEffortSum();
		releaseDays = calculateReleaseDays();
		for (final WorkingDay releaseDay : releaseDays) {
			final Float accomplishedValue = getAccomplishedValueFor(releaseDay);
			final Float accomplishedEffort = getAccomplishedEffortFor(releaseDay);

			if (accomplishedValue != null) accomplishedValueByDate.put(releaseDay, accomplishedValue);
			if (accomplishedEffort != null) {
				accomplishedEffortByDate.put(releaseDay, accomplishedEffort);
				if (accomplishedEffort >= effortSum) break;
			}
		}
	}

	private List<WorkingDay> calculateReleaseDays() {
		final WorkingDay startDay = releaseEstimator.getEstimatedStartDayFor(release);
		final WorkingDay inferedEstimatedEndDay = releaseEstimator.getEstimatedEndDayUsingInferedEstimatedVelocity(release);
		final WorkingDay estimatedEndDay = releaseEstimator.getEstimatedEndDayFor(release);

		final WorkingDay lastReleaseDay = WorkingDay.getLatest(inferedEstimatedEndDay, estimatedEndDay, release.getInferedEndDay());

		final List<WorkingDay> releaseDays = new ArrayList<WorkingDay>();

		final int daysCount = startDay.countTo(lastReleaseDay);
		final int step = Math.max(1, daysCount / MAX_NUMBER_OF_DAYS);

		WorkingDay day = startDay.copy();
		for (int i = 0; i < daysCount; i += step) {
			releaseDays.add(day);
			day = day.copy().add(step);
		}

		addIfNotPresent(releaseDays, inferedEstimatedEndDay);
		addIfNotPresent(releaseDays, estimatedEndDay);
		addIfNotPresent(releaseDays, release.getInferedEndDay());

		return releaseDays;
	}

	private void addIfNotPresent(final List<WorkingDay> releaseDays, final WorkingDay day) {
		if (day == null) return;

		int index = 0;
		for (final WorkingDay d : releaseDays) {
			if (d.equals(day)) return;
			if (d.isAfter(day)) break;
			index++;
		}
		releaseDays.add(index, day.copy());
		System.out.println("added at " + index);
	}

	private Float getAccomplishedValueFor(final WorkingDay day) {
		return getAccomplishedPointsForTheDay(day, new PointsProvider() {
			@Override
			public float getPointsFrom(final Scope scope) {
				return scope.getValue().getInfered();
			}
		});
	}

	private Float getAccomplishedEffortFor(final WorkingDay day) {
		return getAccomplishedPointsForTheDay(day, new PointsProvider() {
			@Override
			public float getPointsFrom(final Scope scope) {
				return scope.getEffort().getInfered();
			}
		});
	}

	private Float getAccomplishedPointsForTheDay(final WorkingDay day, final PointsProvider provider) {
		if (day.isAfter(WorkingDayFactory.create())) return null;

		return getAccomplishedPointsSum(release.getAllScopesIncludingDescendantReleases(), day, provider);
	}

	private float getAccomplishedPoints(final Scope scope, final WorkingDay day, final PointsProvider provider) {
		if (scope.getProgress().isDone() && scope.getProgress().getEndDay().isBeforeOrSameDayOf(day)) return provider.getPointsFrom(scope);

		return getAccomplishedPointsSum(scope.getChildren(), day, provider);
	}

	private float getAccomplishedPointsSum(final List<Scope> scopes, final WorkingDay day, final PointsProvider provider) {
		float effort = 0;
		for (final Scope s : scopes) {
			effort += getAccomplishedPoints(s, day, provider);
		}
		return effort;
	}

	private interface PointsProvider {
		float getPointsFrom(final Scope scope);
	}

	public WorkingDay getActualEndDay() {
		return release.getInferedEndDay();
	}

	public boolean hasStarted() {
		final WorkingDay startDay = release.getStartDay();
		return startDay != null && !WorkingDayFactory.create().isBefore(startDay);
	}

}