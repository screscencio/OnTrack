package br.com.oncast.ontrack.shared.model.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

public class ReleaseEstimator {

	public static final float MIN_VELOCITY = 0.1F;
	private static final float NEW_VELOCITY_WEIGHT = 0.6F;
	private static final int DURATION_OF_START_DAY = 1;
	private static final short DEFAULT_VELOCITY = 1;

	private final Release rootRelease;

	public ReleaseEstimator(final Release rootRelease) {
		this.rootRelease = rootRelease;
	}

	public WorkingDay getEstimatedStartDayFor(final Release release) {
		final WorkingDay startDay = release.getStartDay();
		return startDay == null ? WorkingDayFactory.create() : startDay;
	}

	public WorkingDay getEstimatedEndDayUsingInferedEstimatedVelocity(final Release release) {
		final WorkingDay startDay = getEstimatedStartDayFor(release);
		return getEstimatedEndDay(startDay, release.getEffortSum(), getInferedEstimatedVelocityOnDay(release));

	}

	public WorkingDay getEstimatedEndDayFor(final Release release) {
		if (release.hasDeclaredEndDay()) return release.getEndDay();

		final WorkingDay startDay = getEstimatedStartDayFor(release);
		final float estimatedVelocity = release.hasDeclaredEstimatedVelocity() ? release.getEstimatedVelocity() : getInferedEstimatedVelocityOnDay(release);

		return getEstimatedEndDay(startDay, release.getEffortSum(), estimatedVelocity);
	}

	private WorkingDay getEstimatedEndDay(final WorkingDay startDay, final float effortSum, final float estimatedVelocity) {
		if (effortSum == 0) return startDay;

		return startDay.add(ceilling(effortSum / estimatedVelocity) - DURATION_OF_START_DAY);
	}

	public float getInferedEstimatedVelocityOnDay(final Release release) {
		return Math.max(MIN_VELOCITY, getRawInferedEstimatedVelocityOnDay(release));
	}

	private float getRawInferedEstimatedVelocityOnDay(final Release release) {
		final List<Release> consideredReleases = getConsideredReleases(release);

		if (consideredReleases.isEmpty()) return DEFAULT_VELOCITY;

		final Release first = consideredReleases.remove(0);
		float velocity = first.getActualVelocity();
		for (final Release r : consideredReleases) {
			final Float actual = r.getActualVelocity();
			if (actual != null) velocity = (1F - NEW_VELOCITY_WEIGHT) * velocity + NEW_VELOCITY_WEIGHT * actual;
		}

		return velocity;
	}

	private List<Release> getConsideredReleases(final Release release) {
		final List<Release> consideredReleases = new ArrayList<Release>();
		for (final Release r : rootRelease.getAllReleasesInTemporalOrder()) {
			if (r.equals(release)) break;
			if (r.isLeaf() && r.isDone()) consideredReleases.add(r);
		}
		return consideredReleases;
	}

	private int ceilling(final float number) {
		return (int) (number + 0.999999);
	}

}
