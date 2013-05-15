package br.com.oncast.ontrack.shared.model.scope;

import java.util.Date;

import br.com.oncast.ontrack.client.utils.date.DateUnit;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class ScopeEstimator {

	private static final long DEFAULT_DURATION = 1 * DateUnit.DAY;

	private final ReleaseEstimator releaseEstimator;

	public ScopeEstimator(final ReleaseEstimator releaseEstimator) {
		this.releaseEstimator = releaseEstimator;
	}

	public long getDuration(final Scope scope) {
		if (!scope.getEffort().hasInfered()) return DEFAULT_DURATION;
		return (long) (scope.getEffort().getInfered() * DateUnit.DAY / getEstimatedExecutionSpeed(scope));
	}

	public long getRemainingTime(final Scope scope) {
		if (!scope.hasDueDate()) throw new IllegalStateException("It's not possible to calculate remaining time when the scope does not have due date set");
		final Date dueDate = scope.getDueDate();
		final Date now = new Date();
		return getWorkingDaysBetween(now, dueDate) * DateUnit.DAY + getTimeBetween(now, dueDate);
	}

	public float getEstimatedExecutionSpeed(final Scope scope) {
		return scope.getRelease() == null ? releaseEstimator.getCurrentSpeed() : releaseEstimator.getEstimatedSpeed(scope.getRelease());

	}

	private long getTimeBetween(final Date from, final Date to) {
		return getTimeOnly(to) - getTimeOnly(from);
	}

	private int getWorkingDaysBetween(final Date from, final Date to) {
		final int daysCount = WorkingDayFactory.create(from).countTo(WorkingDayFactory.create(to));
		return daysCount > 0 ? daysCount - 1 : daysCount + 1;
	}

	@SuppressWarnings("deprecation")
	private long getTimeOnly(final Date date) {
		final Date d = CalendarUtil.copyDate(date);
		d.setYear(0);
		d.setMonth(0);
		d.setDate(0);
		return d.getTime();
	}

}
