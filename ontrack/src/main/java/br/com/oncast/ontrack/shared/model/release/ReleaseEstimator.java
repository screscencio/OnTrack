package br.com.oncast.ontrack.shared.model.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.ScopeComparator;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ReleaseEstimator {

	private static final int DURATION_OF_START_DAY = 1;
	private static final short NUMBER_OF_REQUIRED_SCOPES = 30;
	private static final short DEFAULT_VELOCITY = 1;
	private static final short DEFAULT_EFFORT = 1;
	private static final short DEFAULT_DAYS_SPENT = 1;
	private final Release rootRelease;

	public ReleaseEstimator(final Release rootRelease) {
		this.rootRelease = rootRelease;
	}

	public WorkingDay getEstimatedStartDayFor(final Release release) {
		final WorkingDay startDay = release.getStartDay();
		return startDay == null ? WorkingDayFactory.create() : startDay;
	}

	public WorkingDay getEstimatedEndDayFor(final Release release) {
		final WorkingDay startDay = getEstimatedStartDayFor(release);
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return startDay;

		final float estimatedVelocity = getEstimatedVelocityOnDay(startDay);

		final int estimatedReleaseDurationInDays = roundUp(effortSum / estimatedVelocity);

		return startDay.add(estimatedReleaseDurationInDays - DURATION_OF_START_DAY);
	}

	public float getEstimatedVelocityOnDay(final WorkingDay day) {
		final List<Scope> sampleScopes = rootRelease.getAllScopesIncludingDescendantReleases();

		final List<Scope> consideredSampleScopes = getDoneScopesSortedByLatestEndDateUntilDay(sampleScopes, day);
		if (consideredSampleScopes.isEmpty()) return DEFAULT_VELOCITY;

		final WorkingDay earliestStartDay = getEarliestStartDay(consideredSampleScopes);
		final WorkingDay latestEndDay = consideredSampleScopes.get(0).getProgress().getEndDay();
		final int daysSpent = earliestStartDay.countTo(latestEndDay);

		final float effortSum = getEffortSumOf(consideredSampleScopes);
		final int nConsideredScopes = consideredSampleScopes.size();

		return calculateVelocity(nConsideredScopes, effortSum, daysSpent);
	}

	private List<Scope> getDoneScopesSortedByLatestEndDateUntilDay(final List<Scope> scopes, final WorkingDay day) {
		ScopeComparator.sortByLatestEndDate(scopes);
		final ArrayList<Scope> consideredScopes = new ArrayList<Scope>();

		for (final Scope scope : scopes) {
			final WorkingDay endDate = scope.getProgress().getEndDay();

			if (endDate == null) break;
			if (!endDate.isBefore(day)) continue;

			consideredScopes.add(scope);
			if (consideredScopes.size() == NUMBER_OF_REQUIRED_SCOPES) break;
		}

		return consideredScopes;
	}

	private WorkingDay getEarliestStartDay(final List<Scope> consideredSampleScopes) {
		WorkingDay earliestStartDay = WorkingDayFactory.create();

		for (final Scope scope : consideredSampleScopes) {
			final WorkingDay scopeStartDay = scope.getProgress().getStartDay();
			if (scopeStartDay.isBefore(earliestStartDay)) earliestStartDay = scopeStartDay;
		}
		return earliestStartDay;
	}

	private float getEffortSumOf(final List<Scope> consideredSampleScopes) {
		float effortSum = 0;
		for (final Scope scope : consideredSampleScopes) {
			effortSum += scope.getEffort().getInfered();
		}
		return effortSum;
	}

	private float calculateVelocity(final int nConsideredScopes, final float consideredEffortSum, final int consideredDaysSpent) {
		final int nLackingScopes = NUMBER_OF_REQUIRED_SCOPES - nConsideredScopes;

		final float effortSum = consideredEffortSum + nLackingScopes * DEFAULT_EFFORT;
		final int daysSpent = consideredDaysSpent + nLackingScopes * DEFAULT_DAYS_SPENT;

		final float velocity = effortSum / daysSpent;
		return velocity;
	}

	private int roundUp(final float number) {
		return (int) (number + 0.999999);
	}
}
