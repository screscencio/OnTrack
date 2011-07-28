package br.com.oncast.ontrack.shared.model.release;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseProgressTest {

	private Scope scope;
	private Release r1;

	@Before
	public void setUp() {
		r1 = ReleaseMock.getRelease().getChildReleases().get(0);
		scope = ScopeMock.getScopeWithEffort();

		r1.addScope(scope.getChild(0));
		r1.addScope(scope.getChild(1));
	}

	// public void shouldReturnReleaseProgressBasedOnScopesEffortAndProgress() {}

	@Test
	public void progressShoulbBeZeroIfAllScopesAreNotStartedYet() {
		assertEquals(0f, r1.getProgressPercentage());
	}

	@Test
	public void progressShoulbBeZeroIfThereIsNoDoneScope() {
		scope.getChild(0).getProgress().setDescription("Underwork");

		assertEquals(0f, r1.getProgressPercentage());
	}

	@Test
	public void shouldGetOnlyDoneScopesToCalculateProgressPercentage() {
		scope.getChild(0).getProgress().setDescription("Underwork");
		scope.getChild(1).getProgress().setDescription("Done");

		assertEquals(66.6, r1.getProgressPercentage(), 0.09);
	}

	@Test
	public void shouldGetAllDoneScopesToCalculateProgressPercentage() {
		r1.addScope(scope.getChild(2));

		scope.getChild(0).getProgress().setDescription("Underwork");
		scope.getChild(1).getProgress().setDescription("Done");
		scope.getChild(2).getProgress().setDescription("Done");

		assertEquals(83.3, r1.getProgressPercentage(), 0.09);
	}

	@Test
	public void shouldUpdateProgressPercentageWhenAScopeIsRemovedFromRelease() {
		scope.getChild(0).getProgress().setDescription("Underwork");
		scope.getChild(1).getProgress().setDescription("Done");

		assertEquals(66.6, r1.getProgressPercentage(), 0.09);

		r1.removeScope(scope.getChild(1));

		assertEquals(0, r1.getProgressPercentage(), 0.09);
	}

	@Test
	public void shouldIncludeSubReleasesInProgressPercentageCalculation() {
		scope.getChild(0).getProgress().setDescription("Underwork");
		scope.getChild(1).getProgress().setDescription("Done");

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scope.getChild(2);
		scope2.getProgress().setDescription("Done");
		final Scope scope3 = scope.getChild(3);
		scope3.getProgress().setDescription("Not started");

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(50, r1.getProgressPercentage(), 0.09);
	}

	@Test
	public void shouldBe100IfAllScopesAreDone() {
		scope.getChild(0).getProgress().setDescription("Done");
		scope.getChild(1).getProgress().setDescription("Done");

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scope.getChild(2);
		scope2.getProgress().setDescription("Done");
		final Scope scope3 = scope.getChild(3);
		scope3.getProgress().setDescription("Done");

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(100, r1.getProgressPercentage(), 0.09);
	}

	@Test
	public void shouldBeZeroEvenIfAllScopesAreUnderWork() {
		scope.getChild(0).getProgress().setDescription("Underwork");
		scope.getChild(1).getProgress().setDescription("Underwork");

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scope.getChild(2);
		scope2.getProgress().setDescription("Underwork");
		final Scope scope3 = scope.getChild(3);
		scope3.getProgress().setDescription("Underwork");

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(0, r1.getProgressPercentage(), 0.09);
	}

}
