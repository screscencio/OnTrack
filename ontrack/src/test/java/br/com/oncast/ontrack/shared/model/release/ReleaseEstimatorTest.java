package br.com.oncast.ontrack.shared.model.release;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;

public class ReleaseEstimatorTest {

	private static final float WEIGHT = 0.6F;

	private static final float DEFAULT_VELOCITY = 1;

	private Release sampleReleases;
	private ReleaseEstimator estimator;
	private Float velocity;
	private Release actual;

	@Before
	public void setup() {
		sampleReleases = ReleaseTestUtils.getEmptyRelease();
		estimator = new ReleaseEstimator(sampleReleases);
		velocity = 3.5F;
		actual = createRelease(null, false);
	}

	@Test
	public void estimatedVelocityShouldBeOneWhenThereIsNoPreviousDoneRelease() throws Exception {
		sampleReleases.addChild(actual);
		assertEquals(DEFAULT_VELOCITY, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void estimatedVelocityIsTheVelocityOfThePreviousDoneReleaseWhenThereIsOnlyOnePreviousDoneRelease() throws Exception {
		sampleReleases.addChild(createRelease(velocity));
		sampleReleases.addChild(actual);
		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void notLeafReleasesDoesNotCount() throws Exception {
		final Release notLeafRelease = createRelease(5.7F);
		notLeafRelease.addChild(createRelease(velocity));
		sampleReleases.addChild(notLeafRelease);
		sampleReleases.addChild(actual);
		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void notDoneReleasesDoesNotCount() throws Exception {
		sampleReleases.addChild(createRelease(5.7F, false));
		sampleReleases.addChild(createRelease(velocity));
		sampleReleases.addChild(createRelease(8.2F, false));
		sampleReleases.addChild(actual);

		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void releasesAfterTheGivenReleaseDoesNotCount() throws Exception {
		sampleReleases.addChild(createRelease(velocity));
		sampleReleases.addChild(actual);
		sampleReleases.addChild(createRelease(5.7F));
		sampleReleases.addChild(createRelease(8.2F));

		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void theEstimativeShouldBeTheProjectionOfPreviousDoneReleasesWithWeightOf06() throws Exception {
		final Float[] velocities = { 3.5F, 5.7F, 8.2F };

		for (final Float vel : velocities)
			sampleReleases.addChild(createRelease(vel));
		sampleReleases.addChild(actual);

		final Float velocity = calculateProjectionVelocity(velocities);
		assertEquals(velocity, estimator.getInferedEstimatedVelocityOnDay(actual), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	private Float calculateProjectionVelocity(final Float[] velocities) {
		Float velocity = velocities[0];
		for (int i = 1; i < velocities.length; i++) {
			velocity = velocity * (1F - WEIGHT) + velocities[i] * WEIGHT;
		}
		return velocity;
	}

	private Release createRelease(final Float actualVelocity) {
		return createRelease(actualVelocity, true);
	}

	private Release createRelease(final Float actualVelocity, final boolean isDone) {
		final Release release = ReleaseTestUtils.getEmptyRelease();
		final Release spy = Mockito.spy(release);
		when(spy.getActualVelocity()).thenReturn(actualVelocity);
		when(spy.isDone()).thenReturn(isDone);
		return spy;
	}

}
