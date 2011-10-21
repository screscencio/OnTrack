package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressInferenceEngineTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			child.getProgress().setDescription("DONE");
			PROGRESS_INFERENCE_ENGINE.process(rootScope);
		}

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), 0.09);
	}

	@Test
	public void aScopeShouldBeDoneIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			child.getProgress().setDescription("DONE");
			PROGRESS_INFERENCE_ENGINE.process(rootScope);
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAtLeastOneOfItsChildrenIsNotDoneAndTheSumOfAllEstimatedEffortsIsZero() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		rootScope.getChild(0).getProgress().setDescription("DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope);
		rootScope.getChild(1).getProgress().setDescription("DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope);

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), 0.09);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringItsChildrenEffortsEstimatedAndTheirAccomplishedEffort() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getEffort().setDeclared(5);
		rootScope.getChild(0).getProgress().setDescription("DONE");
		EFFORT_INFERENCE_ENGINE.process(rootScope);
		PROGRESS_INFERENCE_ENGINE.process(rootScope);

		rootScope.getChild(1).getEffort().setDeclared(10);
		rootScope.getChild(1).getProgress().setDescription("DONE");
		EFFORT_INFERENCE_ENGINE.process(rootScope);
		PROGRESS_INFERENCE_ENGINE.process(rootScope);

		rootScope.getChild(2).getEffort().setDeclared(20);
		EFFORT_INFERENCE_ENGINE.process(rootScope);
		PROGRESS_INFERENCE_ENGINE.process(rootScope);

		assertEquals(42.8, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringAllItsChildrenHierarchy() {
		final Scope rootScope = ScopeTestUtils.getScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getChild(0).getChild(0).getEffort().setDeclared(5);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0));
		rootScope.getChild(0).getChild(0).getChild(0).getProgress().setDescription("DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0));

		rootScope.getChild(0).getChild(0).getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0));

		rootScope.getChild(0).getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0));
		rootScope.getChild(0).getChild(1).getProgress().setDescription("DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope.getChild(0));

		rootScope.getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope);
		rootScope.getChild(1).getProgress().setDescription("DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope);

		rootScope.getChild(2).getEffort().setDeclared(20);
		EFFORT_INFERENCE_ENGINE.process(rootScope);

		assertEquals(45.4, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

}
