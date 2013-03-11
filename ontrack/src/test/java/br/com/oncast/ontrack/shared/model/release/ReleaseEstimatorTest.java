package br.com.oncast.ontrack.shared.model.release;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.ibm.icu.util.Calendar;

public class ReleaseEstimatorTest {

	private static final int DEFAULT_EFFORT = 1;
	private static final int DEFAULT_DAYS_SPENT = 1;
	private final int REQUIRED_NUMBER_OF_DONE_SCOPES = 30;

	private Release sampleReleases;
	private ReleaseEstimator estimator;

	@Before
	public void setup() {
		sampleReleases = ReleaseTestUtils.getEmptyRelease();
		estimator = new ReleaseEstimator(sampleReleases);
	}

	@Test
	public void estimatedVelocityShouldBeOneWhenThereIsNoPreviousDoneRelease() throws Exception {
		assertEquals(1, estimator.getInferedEstimatedVelocityOnDay(WorkingDayFactory.create()), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void estimatedVelocityShouldConsiderLastThirtyDoneScopes() throws Exception {
		final int nScopes = 60;
		final int nDaysSpent = 30;
		final float effortSum = 465;

		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(nScopes);

		final float velocity = effortSum / nDaysSpent;
		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(WorkingDayFactory.create()), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void estimatedVelocityShouldBeTheSumOfEffortDividedByTheDaysSpentToFinishTheScopes() throws Exception {
		final int nScopes = 27;
		final int nDaysSpent = 40;

		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(nScopes);

		final WorkingDay date = WorkingDayFactory.create().add(-(nDaysSpent));

		sampleReleases.addScope(createDoneScopeWithEffortAndDaysSpent(20, date, 5));
		date.add(5);
		sampleReleases.addScope(createDoneScopeWithEffortAndDaysSpent(2, date, 2));
		date.add(1);
		sampleReleases.addScope(createDoneScopeWithEffortAndDaysSpent(18, date, 3));

		final float effortSum = 378 + 20 + 2 + 18;
		final float estimatedVelocity = effortSum / nDaysSpent;

		assertEquals(estimatedVelocity, estimator.getInferedEstimatedVelocityOnDay(WorkingDayFactory.create()), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void shouldFillWithDefaultVelocityWhenThereIsLessThanRequiredNumberOfDoneScopes() throws Exception {
		final int nScopes = 16;
		final int eachDoneScopeDaysSpent = 2;
		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(nScopes, eachDoneScopeDaysSpent);

		final int numberOfLackingScopes = REQUIRED_NUMBER_OF_DONE_SCOPES - nScopes;

		final float effortSum = 136 + numberOfLackingScopes * DEFAULT_EFFORT;
		final int daysSpent = 16 * eachDoneScopeDaysSpent + numberOfLackingScopes * DEFAULT_DAYS_SPENT;

		final float velocity = effortSum / daysSpent;

		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(WorkingDayFactory.create()), TestUtils.TOLERATED_FLOAT_DIFFERENCE);

	}

	@Test
	public void estimatedVelocityShouldConsiderOnlyScopesThatWasDoneBeforeTheDate() throws Exception {
		final int nScopes = 30;

		populateSampleScopesWithDaySpentAndArithmeticSequenceForEffortAndStartDayBeforeTheDay(nScopes, 1, WorkingDayFactory.create(2011, Calendar.JANUARY, 3));
		final WorkingDay anyDayBetween = WorkingDayFactory.create(2011, Calendar.MAY, 3);
		populateSampleScopesWithDaySpentAndArithmeticSequenceForEffortAndStartDayBeforeTheDay(10, 2, WorkingDayFactory.create(2011, Calendar.OCTOBER, 3));

		final float effortSum = 465;
		final int daysSpent = 30;

		final float velocity = effortSum / daysSpent;
		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(anyDayBetween), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void estimatedEndDateShouldBeSameDayOfReleaseStartDateWhenThereIsNoScopeOnRelease() throws Exception {
		final Release emptyRelease = ReleaseFactoryTestUtil.create("Release");

		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(13);

		assertEquals(WorkingDayFactory.create(), estimator.getEstimatedEndDayUsingInferedEstimatedVelocity(emptyRelease));
	}

	@Test
	public void estimatedEndDateShouldBeSameDayOfReleaseStartDateWhenTheEffortSumOfTheReleaseIsZero() throws Exception {
		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(16);

		final Release release = ReleaseFactoryTestUtil.create("Release");
		for (int i = 0; i < 10; i++)
			release.addScope(createDoneScopeWithEffort(0));

		assertEquals(release.getStartDay(), estimator.getEstimatedEndDayUsingInferedEstimatedVelocity(release));
	}

	@Test
	public void estimatedEndDateShouldBeTheDayWhenTheReleaseReachesEffortSumWithEstimatedVelocity() throws Exception {
		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(16);
		addDoneScopesWithEffortToRelease(sampleReleases, 2, 3, 5, 8, 13);

		final WorkingDay startDate = sampleReleases.getStartDay();

		final float estimatedVelocity = estimator.getInferedEstimatedVelocityOnDay(startDate);
		final float effortSum = sampleReleases.getEffortSum();

		// TODO Remove this calculation and put the result by hand. This is not explaining how to get to correct result.
		final WorkingDay estimatedEndDate = startDate.add(((int) (effortSum / estimatedVelocity + 0.999999) - 1)); // Subtract 1 because today is included.

		assertEquals(estimatedEndDate, estimator.getEstimatedEndDayUsingInferedEstimatedVelocity(sampleReleases));
	}

	private void addDoneScopesWithEffortToRelease(final Release release, final int... efforts) {
		for (final int effort : efforts) {
			release.addScope(createDoneScopeWithEffort(effort));
		}
	}

	private Scope createDoneScopeWithEffort(final int effort) {
		final Scope scope = ScopeTestUtils.createScope("Scope" + effort);
		ScopeTestUtils.setProgress(scope, ProgressState.DONE);
		ScopeTestUtils.setDelcaredEffort(scope, effort);

		return scope;
	}

	private Scope createDoneScopeWithEffortAndDaysSpent(final float effort, final WorkingDay date, final int daysSpent) throws Exception {
		final Scope scope = ScopeTestUtils.createScope("Scope" + effort);
		ScopeTestUtils.setDelcaredEffort(scope, effort);

		ScopeTestUtils.setStartDate(scope, date);

		ScopeTestUtils.setEndDate(scope, date.copy().add(daysSpent - 1));
		return scope;
	}

	private void populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(final int nScopes) throws Exception {
		populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(nScopes, 1);
	}

	private void populateSampleScopesWithOneDaySpentAndArithmeticSequenceForEffortAndStartDay(final int nScopes, final int daysSpent) throws Exception {
		populateSampleScopesWithDaySpentAndArithmeticSequenceForEffortAndStartDayBeforeTheDay(nScopes, daysSpent, WorkingDayFactory.create());
	}

	private void populateSampleScopesWithDaySpentAndArithmeticSequenceForEffortAndStartDayBeforeTheDay(final int nScopes, final int daysSpent,
			final WorkingDay day) throws Exception {
		for (int i = 1; i <= nScopes; i++) {
			sampleReleases.addScope(createDoneScopeWithEffortAndDaysSpent(i, day.copy().add(-i * daysSpent), daysSpent));
		}
	}

}
