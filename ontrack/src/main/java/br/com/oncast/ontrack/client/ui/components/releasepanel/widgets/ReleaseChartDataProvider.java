package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ReleaseChartDataProvider {

	private final Release release;
	private final ReleaseEstimator releaseEstimator;
	private HashMap<String, Float> accomplishedEffortByDate;

	public ReleaseChartDataProvider(final Release release, final ReleaseEstimator estimator) {
		this.release = release;
		this.releaseEstimator = estimator;
		calculateAccomplishedEffortByDate();
	}

	public List<String> getReleaseDays() {
		return new ArrayList<String>(accomplishedEffortByDate.keySet());
	}

	public List<Float> getAccomplishedEffortsByDate() {
		final ArrayList<Float> efforts = new ArrayList<Float>();
		for (final String data : accomplishedEffortByDate.keySet()) {
			final Float effortInThisDate = accomplishedEffortByDate.get(data);
			if (effortInThisDate == null) continue;
			efforts.add(effortInThisDate);
			if (effortInThisDate >= getEffortSum()) break;
		}
		return efforts;
	}

	public float getEffortSum() {
		return release.getEffortSum();
	}

	private void calculateAccomplishedEffortByDate() {
		accomplishedEffortByDate = new LinkedHashMap<String, Float>();

		final List<WorkingDay> releaseDays = calculateReleaseDays();
		for (final WorkingDay releaseDay : releaseDays) {
			accomplishedEffortByDate.put(releaseDay.getDayAndMonthString(), getAccomplishedEffort(releaseDay));
		}
	}

	private Float getAccomplishedEffort(final WorkingDay day) {
		if (day.isAfter(WorkingDayFactory.create())) return null;

		float accomplishedEffortSum = 0;
		for (final Scope scope : release.getAllScopesIncludingChildrenReleases()) {
			if (scope.getProgress().isDone() && scope.getProgress().getEndDay().isBeforeOrSameDayOf(day)) {
				accomplishedEffortSum += scope.getEffort().getInfered();
			}
		}
		return accomplishedEffortSum;
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

	public String getEstimatedEndDay() {
		return releaseEstimator.getEstimatedEndDayFor(release).getDayAndMonthString();
	}

}
