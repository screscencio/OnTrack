package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ProgressInferenceEngineTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();

	@Test
	public void whenTheParentScopeIsReSetAsNotStartedItShouldReSetAllChildrenToPreviousState() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		final Scope child1 = rootScope.getChild(1);
		final Scope child2 = rootScope.getChild(2);

		ScopeTestUtils.setProgress(child1, ProgressState.UNDER_WORK);
		processProgressInference(child1);
		ScopeTestUtils.setProgress(child2, ProgressState.DONE);
		processProgressInference(child2);

		ScopeTestUtils.setProgress(rootScope, ProgressState.DONE);
		processProgressInference(rootScope);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}

		ScopeTestUtils.setProgress(rootScope, ProgressState.NOT_STARTED);
		processProgressInference(rootScope);

		assertEquals(ProgressState.NOT_STARTED, rootScope.getChild(0).getProgress().getState());
		assertEquals(ProgressState.UNDER_WORK, child1.getProgress().getState());
		assertEquals(ProgressState.DONE, child2.getProgress().getState());

	}

	@Test
	public void whenTheParentScopeIsSetAsDoneItShouldSetAllChildrenToDoneEvenWhenItHasDeclaredProgress() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, ProgressState.UNDER_WORK);
			processProgressInference(child);
		}

		ScopeTestUtils.setProgress(rootScope, ProgressState.DONE);
		processProgressInference(rootScope);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}
	}

	@Test
	public void whenTheParentScopeIsSetAsDoneItShouldSetAllChildrenToDone() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		ScopeTestUtils.setProgress(rootScope, ProgressState.DONE);
		processProgressInference(rootScope);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, ProgressState.DONE);
			processProgressInference(child);
		}

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void aScopeShouldBeDoneIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, ProgressState.DONE);
			processProgressInference(child);
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeShouldBeDoneWhenDeclaredAsDoneEvenWhenItHasUnderWorkChildren() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, ProgressState.DONE);
			processProgressInference(child);
		}

		final Scope child = rootScope.getChild(2);
		ScopeTestUtils.setProgress(child, ProgressState.UNDER_WORK);
		processProgressInference(child);

		ScopeTestUtils.declareProgress(rootScope, ProgressState.DONE);
		processProgressInference(rootScope);

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeShouldBeDoneIfAllItsChildrenAreDoneEvenWhenItHasDeclaredProgress() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		ScopeTestUtils.declareProgress(rootScope, ProgressState.UNDER_WORK);
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.setProgress(child, ProgressState.DONE);
			processProgressInference(child);
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void a2ScopeShouldBeDoneIfAllItsChildrenAreDoneEvenWhenItHasDeclaredProgress() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		ScopeTestUtils.declareProgress(rootScope, ProgressState.UNDER_WORK);
		for (final Scope child : rootScope.getChildren()) {
			ScopeTestUtils.declareProgress(child, ProgressState.DONE);
			processProgressInference(child);
		}

		assertTrue(rootScope.getProgress().isDone());

		ScopeTestUtils.declareProgress(rootScope.getChild(2), ProgressState.UNDER_WORK);
		processProgressInference(rootScope);

		assertTrue(rootScope.getProgress().isUnderWork());
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAtLeastOneOfItsChildrenIsNotDoneAndTheSumOfAllEstimatedEffortsIsZero() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		ScopeTestUtils.setProgress(rootScope.getChild(0), ProgressState.DONE);
		processProgressInference(rootScope);
		ScopeTestUtils.setProgress(rootScope.getChild(1), ProgressState.DONE);
		processProgressInference(rootScope);

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringItsChildrenEffortsEstimatedAndTheirAccomplishedEffort() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getEffort().setDeclared(5);
		ScopeTestUtils.setProgress(rootScope.getChild(0), ProgressState.DONE);
		procressEffortInference(rootScope);
		processProgressInference(rootScope);

		rootScope.getChild(1).getEffort().setDeclared(10);
		ScopeTestUtils.setProgress(rootScope.getChild(1), ProgressState.DONE);
		procressEffortInference(rootScope);
		processProgressInference(rootScope);

		rootScope.getChild(2).getEffort().setDeclared(20);
		procressEffortInference(rootScope);
		processProgressInference(rootScope);

		assertEquals(42.8, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringAllItsChildrenHierarchy() {
		final Scope rootScope = ScopeTestUtils.getScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getChild(0).getChild(0).getEffort().setDeclared(5);
		procressEffortInference(rootScope.getChild(0).getChild(0));
		ScopeTestUtils.setProgress(rootScope.getChild(0).getChild(0).getChild(0), ProgressState.DONE);
		procressEffortInference(rootScope.getChild(0).getChild(0));

		rootScope.getChild(0).getChild(0).getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope.getChild(0).getChild(0));

		rootScope.getChild(0).getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope.getChild(0));
		ScopeTestUtils.setProgress(rootScope.getChild(0).getChild(1), ProgressState.DONE);
		procressEffortInference(rootScope.getChild(0));

		rootScope.getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope);
		ScopeTestUtils.setProgress(rootScope.getChild(1), ProgressState.DONE);
		processProgressInference(rootScope);

		rootScope.getChild(2).getEffort().setDeclared(20);
		procressEffortInference(rootScope);

		assertEquals(45.4, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeShouldBeUnderWorkWhenAChildIsUnderWork() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		ScopeTestUtils.setProgress(rootScope.getChild(1), ProgressState.UNDER_WORK);
		processProgressInference(rootScope);

		assertEquals(ProgressState.UNDER_WORK, rootScope.getProgress().getState());

		ScopeTestUtils.setProgress(rootScope.getChild(0), ProgressState.DONE);
		processProgressInference(rootScope);

		assertEquals(ProgressState.UNDER_WORK, rootScope.getProgress().getState());
	}

	@Test
	public void theScopesStartDayShouldBeTheDayThatTheFirstChildHasBeenMarkedAsUnderWorkIfThereWasntDeclaredAnyUnderWorkBefore() throws Exception {
		final WorkingDay parentCreation = WorkingDayFactory.create(2012, 6, 2);
		final WorkingDay startDay = WorkingDayFactory.create(2012, 6, 3);

		final WorkingDay childBAccomplishDay = WorkingDayFactory.create(2012, 6, 5);
		final WorkingDay endDay = WorkingDayFactory.create(2012, 6, 6);

		final Scope parent = ScopeTestUtils.createScope(parentCreation);

		final Scope childB = ScopeTestUtils.createScope(parentCreation);
		parent.add(childB);
		processProgressInference(parent, parentCreation);

		final Scope childA = ScopeTestUtils.createScope(startDay);
		parent.add(childA);
		processProgressInference(parent, startDay);

		ScopeTestUtils.setProgress(childA, ProgressState.UNDER_WORK, startDay);
		processProgressInference(parent, startDay);

		ScopeTestUtils.setProgress(childB, ProgressState.DONE, childBAccomplishDay);
		processProgressInference(parent, childBAccomplishDay);

		ScopeTestUtils.setProgress(childA, ProgressState.DONE, endDay);
		processProgressInference(parent, endDay);

		assertEquals(startDay, parent.getProgress().getStartDay());
		assertEquals(endDay, parent.getProgress().getEndDay());

	}

	private void processProgressInference(final Scope scope, final WorkingDay day) {
		PROGRESS_INFERENCE_ENGINE.process(scope, UserTestUtils.getAdmin(), day.getJavaDate());
	}

	private void procressEffortInference(final Scope rootScope) {
		EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date(0));
	}

	private void processProgressInference(final Scope scope) {
		processProgressInference(scope, WorkingDayFactory.create(new Date(0)));
	}

}
