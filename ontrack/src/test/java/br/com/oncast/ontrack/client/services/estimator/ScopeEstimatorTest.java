package br.com.oncast.ontrack.client.services.estimator;

import br.com.oncast.ontrack.client.utils.date.DateUnit;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.ScopeEstimator;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ScopeEstimatorTest {

	private static final long TOLERATED_DIFFERENCE = DateUnit.SECOND;

	ScopeEstimator estimator;

	@Mock
	ReleaseEstimator releaseEstimator;

	private Scope scope;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		estimator = spy(new ScopeEstimator(releaseEstimator));
		scope = ScopeTestUtils.createScope();
		when(releaseEstimator.getCurrentSpeed()).thenReturn(1.5F);
	}

	@Test
	public void whenTheScopeIsInAReleaseTheEstimatedVelocityForTheScopeIsTheEstimatedVelocityForTheRelease() throws Exception {
		final float vel = 3F;
		final Release release = ReleaseTestUtils.createRelease();
		release.addScope(scope);
		when(releaseEstimator.getEstimatedSpeed(release)).thenReturn(vel);

		assertEquals(vel, estimator.getEstimatedExecutionSpeed(scope), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void whenTheScopeDoesNotHaveAReleaseTheEstimatedExecutionVelocityIsTheCurrentVelocity() throws Exception {
		final float vel = 12F;
		when(releaseEstimator.getCurrentSpeed()).thenReturn(vel);
		assertEquals(vel, estimator.getEstimatedExecutionSpeed(scope), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void theDurationWhenThereIsNoDeclaredEffortIsOneDay() throws Exception {
		assertEquals(1 * DateUnit.DAY, estimator.getDuration(scope));
	}

	@Test
	public void whenTheEffortIsZeroTheDurationShouldBeZeroDays() throws Exception {
		ScopeTestUtils.declareEffort(scope, 0);
		assertEquals(0 * DateUnit.DAY, estimator.getDuration(scope));
	}

	@Test
	public void whenThereIsADeclaredEffortBiggerThanZeroTheDurationShouldBeTheEffortDividedByTheEstimatedExecutionSpeed() throws Exception {
		ScopeTestUtils.declareEffort(scope, 8);
		when(estimator.getEstimatedExecutionSpeed(scope)).thenReturn(4F);

		assertEquals(2 * DateUnit.DAY, estimator.getDuration(scope));
	}

	@Test(expected = IllegalStateException.class)
	public void whenThereIsNoDueDateTheScopeRemainingTimeCannotBeCalculated() throws Exception {
		estimator.getRemainingTime(scope);
	}

	@Test
	public void whenTheDueDateIsExactlyNowTheScopeRemainingTimeShouldBeZero() throws Exception {
		scope.setDueDate(new Date());
		assertRemainingTimeWithSecondsOfPrecision(0);
	}

	@Test
	public void theScopeRemainingTimeShouldBeTheTimeDifferenceBetweenDueDateAndNow() throws Exception {
		scope.setDueDate(addToNow(1, 2, 3));
		final long timeDifference = 1 * DateUnit.HOUR + 2 * DateUnit.MINUTE + 3 * DateUnit.SECOND;
		assertRemainingTimeWithSecondsOfPrecision(timeDifference);
	}

	@Test
	public void theScopeRemainingTimeShouldSkipNotWorkingDays() throws Exception {
		final Date dueDate = WorkingDayFactory.create().add(10).getJavaDate();
		scope.setDueDate(dueDate);
		final long timeDifference = 10 * DateUnit.DAY;
		assertRemainingTimeWithSecondsOfPrecision(timeDifference);
	}

	// FIXME this test fails in the weekends replace it with fixed date time
	@Test
	public void theScopeRemainingTimeShouldBeNegativeWhenNowIsBeforeTheDueDate() throws Exception {
		scope.setDueDate(addToNow(-2, -3, -4));
		final long timeDifference = -2 * DateUnit.HOUR + -3 * DateUnit.MINUTE + -4 * DateUnit.SECOND;
		assertRemainingTimeWithSecondsOfPrecision(timeDifference);
	}

	@Test
	public void theScopeRemainingTimeShouldSkipNotWorkingDaysEvenWhenDueDateHasAlreadyPassed() throws Exception {
		final Date dueDate = WorkingDayFactory.create().add(-10).getJavaDate();
		scope.setDueDate(dueDate);
		final long timeDifference = -10 * DateUnit.DAY;
		assertRemainingTimeWithSecondsOfPrecision(timeDifference);
	}

	private Date addToNow(final int hours, final int minutes, final int seconds) {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, minutes);
		cal.add(Calendar.SECOND, seconds);
		return cal.getTime();
	}

	private void assertRemainingTimeWithSecondsOfPrecision(final long timeDifference) {
		assertEquals(timeDifference, estimator.getRemainingTime(scope), TOLERATED_DIFFERENCE);
	}

}
