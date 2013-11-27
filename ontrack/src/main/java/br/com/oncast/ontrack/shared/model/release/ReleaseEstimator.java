package br.com.oncast.ontrack.shared.model.release;

import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import java.util.ArrayList;
import java.util.List;

public class ReleaseEstimator {

	public static final float MIN_SPEED = 0.1F;
	private static final float NEW_SPEED_WEIGHT = 0.6F;
	private static final int DURATION_OF_START_DAY = 1;
	private static final short DEFAULT_SPEED = 1;

	private final Release rootRelease;

	public ReleaseEstimator(final Release rootRelease) {
		this.rootRelease = rootRelease;
	}

	public WorkingDay getEstimatedStartDayFor(final Release release) {
		final WorkingDay startDay = release.getStartDay();
		return startDay == null ? WorkingDayFactory.create() : startDay;
	}

	public WorkingDay getEstimatedEndDayUsingInferedEstimatedSpeed(final Release release) {
		final WorkingDay startDay = getEstimatedStartDayFor(release);
		return getEstimatedEndDay(startDay, release.getEffortSum(), getInferedEstimatedSpeed(release));

	}

	public WorkingDay getEstimatedEndDayFor(final Release release) {
		if (release.hasDeclaredEndDay()) return release.getEndDay();

		final WorkingDay startDay = getEstimatedStartDayFor(release);
		final float estimatedSpeed = getEstimatedSpeed(release);

		return getEstimatedEndDay(startDay, release.getEffortSum(), estimatedSpeed);
	}

	private WorkingDay getEstimatedEndDay(final WorkingDay startDay, final float effortSum, final float estimatedSpeed) {
		if (effortSum == 0) return startDay;

		return startDay.add(ceilling(effortSum / estimatedSpeed) - DURATION_OF_START_DAY);
	}

	public float getEstimatedSpeed(final Release release) {
		return release.hasDeclaredEstimatedSpeed() ? release.getEstimatedSpeed() : getInferedEstimatedSpeed(release);
	}

	public float getInferedEstimatedSpeed(final Release release) {
		return Math.max(MIN_SPEED, getRawInferedEstimatedSpeed(release));
	}

	private float getRawInferedEstimatedSpeed(final Release release) {
		final List<Release> consideredReleases = getConsideredReleases(release);

		if (consideredReleases.isEmpty()) return DEFAULT_SPEED;

		final Release first = consideredReleases.remove(0);
		float speed = first.getActualSpeed();
		for (final Release r : consideredReleases) {
			final Float actual = r.getActualSpeed();
			if (actual != null) speed = (1F - NEW_SPEED_WEIGHT) * speed + NEW_SPEED_WEIGHT * actual;
		}

		return speed;
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

	public float getCurrentSpeed() {
		return getInferedEstimatedSpeed(null);
	}

}
