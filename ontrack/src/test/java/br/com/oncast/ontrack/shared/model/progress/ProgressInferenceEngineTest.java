package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ProgressInferenceEngineTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, "DONE");
			PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		}

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), 0.09);
	}

	@Test
	public void aScopeShouldBeDoneIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, "DONE");
			PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAtLeastOneOfItsChildrenIsNotDoneAndTheSumOfAllEstimatedEffortsIsZero() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		ScopeTestUtils.setProgress(rootScope.getChild(0), "DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		ScopeTestUtils.setProgress(rootScope.getChild(1), "DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), 0.09);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringItsChildrenEffortsEstimatedAndTheirAccomplishedEffort() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getEffort().setDeclared(5);
		ScopeTestUtils.setProgress(rootScope.getChild(0), "DONE");
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(1).getEffort().setDeclared(10);
		ScopeTestUtils.setProgress(rootScope.getChild(1), "DONE");
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(2).getEffort().setDeclared(20);
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		assertEquals(42.8, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringAllItsChildrenHierarchy() {
		final Scope rootScope = ScopeTestUtils.getScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getChild(0).getChild(0).getEffort().setDeclared(5);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0), UserTestUtils.getAdmin(), new Date());
		ScopeTestUtils.setProgress(rootScope.getChild(0).getChild(0).getChild(0), "DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0), UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(0).getChild(0).getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0).getChild(0), UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(0).getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope.getChild(0), UserTestUtils.getAdmin(), new Date());
		ScopeTestUtils.setProgress(rootScope.getChild(0).getChild(1), "DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope.getChild(0), UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(1).getEffort().setDeclared(10);
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());
		ScopeTestUtils.setProgress(rootScope.getChild(1), "DONE");
		PROGRESS_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		rootScope.getChild(2).getEffort().setDeclared(20);
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date());

		assertEquals(45.4, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

}
