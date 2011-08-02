package br.com.oncast.ontrack.shared.model.release;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ReleaseMock;
import br.com.oncast.ontrack.mocks.models.ScopeMock;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseProgressTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private Scope scopeHierarchy;
	private Release r1;

	@Before
	public void setUp() {
		r1 = ReleaseMock.getRelease().getChildReleases().get(0);
		scopeHierarchy = ScopeMock.getScopeWithEffort();

		r1.addScope(scopeHierarchy.getChild(0));
		r1.addScope(scopeHierarchy.getChild(1));
	}

	@Test
	public void progressShoulbBeZeroIfAllScopesAreNotStartedYet() {
		assertEquals(0f, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void progressShoulbBeZeroIfThereIsNoDoneScope() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		assertEquals(0f, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void shouldGetOnlyDoneScopesToCalculateProgressPercentage() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		assertEquals(66.6, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void shouldGetAllDoneScopesToCalculateProgressPercentage() {
		r1.addScope(scopeHierarchy.getChild(2));

		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(2).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		assertEquals(83.3, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void shouldUpdateProgressPercentageWhenAScopeIsRemovedFromRelease() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		assertEquals(66.6, getProgressPercentage(r1), 0.09);

		r1.removeScope(scopeHierarchy.getChild(1));

		assertEquals(0, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void shouldIncludeSubReleasesInProgressPercentageCalculation() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scopeHierarchy.getChild(2);
		scope2.getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		final Scope scope3 = scopeHierarchy.getChild(3);
		scope3.getProgress().setDescription("Not started");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(50, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void progressPercentageShouldBe100IfAllScopesAreDone() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scopeHierarchy.getChild(2);
		scope2.getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		final Scope scope3 = scopeHierarchy.getChild(3);
		scope3.getProgress().setDescription("Done");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(100, getProgressPercentage(r1), 0.09);
	}

	@Test
	public void progressPercentageShouldBeZeroEvenIfAllScopesAreUnderWork() {
		scopeHierarchy.getChild(0).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		scopeHierarchy.getChild(1).getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		final Release it1 = new Release("It1");
		r1.addRelease(it1);

		final Scope scope2 = scopeHierarchy.getChild(2);
		scope2.getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);
		final Scope scope3 = scopeHierarchy.getChild(3);
		scope3.getProgress().setDescription("Underwork");
		PROGRESS_INFERENCE_ENGINE.process(scopeHierarchy);

		it1.addScope(scope2);
		it1.addScope(scope3);

		assertEquals(0, getProgressPercentage(r1), 0.09);
	}

	private double getProgressPercentage(final Release release) {
		if (release.isDone()) return 100f;
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return 0f;

		final float concludedEffortSum = release.getComputedEffortSum();
		final float percentage = 100 * concludedEffortSum / effortSum;
		return percentage;
	}

	@Test
	public void progressPercentageShouldBe100IfAllItsChildrenAreDoneAndTheSumOfAllEstimatedEffortsIsZero() {
		r1.getScopeList().clear();

		final Scope rootScope = ScopeMock.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			child.getProgress().markAsCompleted();
			r1.addScope(child);
		}

		assertEquals(100, getProgressPercentage(r1), 0.09);
	}

}
