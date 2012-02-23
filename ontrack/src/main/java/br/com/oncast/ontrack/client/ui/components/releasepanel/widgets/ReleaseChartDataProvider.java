package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ReleaseChartDataProvider {

	private final Release release;
	private final ReleaseEstimator releaseEstimator;
	private HashMap<WorkingDay, Float> accomplishedEffortByDate;
	private HashMap<WorkingDay, Float> accomplishedValueByDate;
	private List<WorkingDay> releaseDays;

	public ReleaseChartDataProvider(final Release release, final ReleaseEstimator estimator) {
		this.release = release;
		this.releaseEstimator = estimator;
		// TODO+++ [Performance] Make it lazy loading
		evaluateData();
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

	private Float getAccomplishedValueFor(final WorkingDay day) {
		if (day.isAfter(WorkingDayFactory.create())) return null;

		float accomplishedValueSum = 0;
		for (final Scope scope : release.getAllScopesIncludingDescendantReleases()) {
			if (scope.getProgress().isDone() && scope.getProgress().getEndDay().isBeforeOrSameDayOf(day)) {
				accomplishedValueSum += scope.getValue().getInfered();
			}
		}
		return accomplishedValueSum;
	}

	private Float getAccomplishedEffortFor(final WorkingDay day) {
		if (day.isAfter(WorkingDayFactory.create())) return null;

		float accomplishedEffortSum = 0;
		for (final Scope scope : release.getAllScopesIncludingDescendantReleases()) {
			if (scope.getProgress().isDone() && scope.getProgress().getEndDay().isBeforeOrSameDayOf(day)) {
				accomplishedEffortSum += scope.getEffort().getInfered();
			}
		}
		return accomplishedEffortSum;
	}

	public List<WorkingDay> getReleaseDays() {
		return new ArrayList<WorkingDay>(releaseDays);
	}

	public Map<WorkingDay, Float> getAccomplishedValuePointsByDate() {
		return accomplishedValueByDate;
	}

	public Map<WorkingDay, Float> getAccomplishedEffortPointsByDate() {
		return accomplishedEffortByDate;
	}

	public float getEffortSum() {
		return release.getEffortSum();
	}

	private List<WorkingDay> calculateReleaseDays() {
		final List<WorkingDay> releaseDays = new ArrayList<WorkingDay>();
		final WorkingDay rollingDay = releaseEstimator.getEstimatedStartDayFor(release);
		final WorkingDay lastReleaseDay = getLatestDay(releaseEstimator.getEstimatedEndDayFor(release), release.getEndDay());

		do {
			releaseDays.add(rollingDay.copy());
			rollingDay.add(1);
		} while (rollingDay.isBeforeOrSameDayOf(lastReleaseDay));

		return releaseDays;
	}

	private WorkingDay getLatestDay(final WorkingDay estimatedEndDay, final WorkingDay releaseEndDay) {
		if (releaseEndDay == null) return estimatedEndDay;
		return estimatedEndDay.isAfter(releaseEndDay) ? estimatedEndDay : releaseEndDay;
	}

	public WorkingDay getEstimatedEndDay() {
		return releaseEstimator.getEstimatedEndDayFor(release);
	}
}
