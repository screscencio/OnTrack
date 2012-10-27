package br.com.oncast.ontrack.shared.model.progress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ProgressInferenceEngineTest {

	private static final ProgressInferenceEngine PROGRESS_INFERENCE_ENGINE = new ProgressInferenceEngine();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();

	@Test
	public void doneShouldPropagateToAncestors() throws Exception {
		final Scope rootScope = ScopeTestUtils.createScope();

		Scope scope = rootScope;
		for (int i = 0; i < 15; i++) {
			final Scope child = ScopeTestUtils.createScope();
			scope.add(child);
			scope = child;
		}

		declare(scope, ProgressState.DONE);

		while (!scope.isRoot()) {
			assertEquals(ProgressState.DONE, scope.getProgress().getState());
			scope = scope.getParent();
		}
		assertEquals(ProgressState.DONE, rootScope.getProgress().getState());
	}

	@Test
	public void doneShouldPropagateToDescendants() throws Exception {
		final Scope rootScope = ScopeTestUtils.createScope();

		Scope scope = rootScope;
		for (int i = 0; i < 15; i++) {
			final Scope child = ScopeTestUtils.createScope();
			scope.add(child);
			scope = child;
		}

		declare(rootScope, ProgressState.DONE);

		while (!scope.isRoot()) {
			assertEquals(ProgressState.DONE, scope.getProgress().getState());
			scope = scope.getParent();
		}
	}

	@Test
	public void doneShouldPropagateToDescendantsAndAncestorsWhenDeclaredAtTheMiddle() throws Exception {
		final Scope rootScope = ScopeTestUtils.createScope();

		Scope scope = rootScope;
		for (int i = 0; i < 15; i++) {
			final Scope child = ScopeTestUtils.createScope();
			scope.add(child);
			scope = child;
		}

		declare(rootScope.getChild(0).getChild(0), ProgressState.DONE);

		while (!scope.isRoot()) {
			assertEquals(ProgressState.DONE, scope.getProgress().getState());
			scope = scope.getParent();
		}
		assertEquals(ProgressState.DONE, rootScope.getProgress().getState());
	}

	@Test
	public void whenTheParentScopeIsReSetAsNotStartedItShouldReSetAllChildrenToPreviousState() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		final Scope child1 = rootScope.getChild(1);
		final Scope child2 = rootScope.getChild(2);

		declare(child1, ProgressState.UNDER_WORK);
		declare(child2, ProgressState.DONE);

		declare(rootScope, ProgressState.DONE);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}

		declare(rootScope, ProgressState.NOT_STARTED);

		assertEquals(ProgressState.NOT_STARTED, rootScope.getChild(0).getProgress().getState());
		assertEquals(ProgressState.UNDER_WORK, child1.getProgress().getState());
		assertEquals(ProgressState.DONE, child2.getProgress().getState());

	}

	@Test
	public void whenTheParentScopeIsSetAsDoneItShouldSetAllChildrenToDoneEvenWhenItHasDeclaredProgress() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.UNDER_WORK);
		}

		declare(rootScope, ProgressState.DONE);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}
	}

	@Test
	public void whenTheParentScopeIsSetAsDoneItShouldSetAllChildrenToDone() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		declare(rootScope, ProgressState.DONE);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void aScopeShouldBeDoneIfItsOnlyChildIsDone() {
		final Scope scope = ScopeTestUtils.createScope();
		final Scope child = ScopeTestUtils.createScope();
		final Scope grandChild = ScopeTestUtils.createScope();
		scope.add(child);
		child.add(grandChild);

		final Set<UUID> influencedScopes = declare(grandChild, ProgressState.DONE);

		assertTrue(scope.getProgress().isDone());

		assertEquals(2, influencedScopes.size());
		assertTrue(influencedScopes.contains(scope.getId()));
		assertTrue(influencedScopes.contains(child.getId()));
	}

	@Test
	public void aScopeShouldBeDoneOnlyIfAllItsChildrenAreDone() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			assertFalse(rootScope.getProgress().isDone());
			declare(child, ProgressState.DONE);
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeShouldBeDoneWhenDeclaredAsDoneEvenWhenItHasUnderWorkChildren() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}

		declare(rootScope.getChild(2), ProgressState.UNDER_WORK);
		declare(rootScope, ProgressState.DONE);

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeShouldBeDoneIfAllItsChildrenAreDoneEvenWhenItHasDeclaredProgress() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		declare(rootScope, ProgressState.UNDER_WORK);

		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}

		assertTrue(rootScope.getProgress().isDone());
	}

	@Test
	public void aScopeShouldBeMarkedAsUnderWorkWhenOneChildIsReMarkedAsUnderWork() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		declare(rootScope, ProgressState.UNDER_WORK);

		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}
		assertTrue(rootScope.getProgress().isDone());

		declare(rootScope.getChild(2), ProgressState.UNDER_WORK);

		assertTrue(rootScope.getProgress().isUnderWork());
	}

	@Test
	public void aScopeProgressPercentageShouldBeZeroIfAtLeastOneOfItsChildrenIsNotDoneAndTheSumOfAllEstimatedEffortsIsZero() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		declare(rootScope.getChild(0), ProgressState.DONE);
		declare(rootScope.getChild(1), ProgressState.DONE);

		assertEquals(0, rootScope.getEffort().getAccomplishedPercentual(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringItsChildrenEffortsEstimatedAndTheirAccomplishedEffort() {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getEffort().setDeclared(5);
		procressEffortInference(rootScope);
		declare(rootScope.getChild(0), ProgressState.DONE);

		rootScope.getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope);
		declare(rootScope.getChild(1), ProgressState.DONE);

		rootScope.getChild(2).getEffort().setDeclared(20);
		procressEffortInference(rootScope);
		processProgressInference(rootScope.getChild(2));

		assertEquals(42.8, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringAllItsChildrenHierarchy() {
		final Scope rootScope = ScopeTestUtils.getScope();

		// Declare and mark as done some scopes
		rootScope.getChild(0).getChild(0).getChild(0).getEffort().setDeclared(5);
		procressEffortInference(rootScope.getChild(0).getChild(0));
		declare(rootScope.getChild(0).getChild(0).getChild(0), ProgressState.DONE);
		assertEquals(100, rootScope.getEffort().getAccomplishedPercentual(), 0.1);

		rootScope.getChild(0).getChild(0).getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope.getChild(0).getChild(0));
		assertEquals(33.33, rootScope.getEffort().getAccomplishedPercentual(), 0.1);

		rootScope.getChild(0).getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope.getChild(0));
		declare(rootScope.getChild(0).getChild(1), ProgressState.DONE);

		rootScope.getChild(1).getEffort().setDeclared(10);
		procressEffortInference(rootScope);
		declare(rootScope.getChild(1), ProgressState.DONE);

		rootScope.getChild(2).getEffort().setDeclared(20);
		procressEffortInference(rootScope);

		assertEquals(45.4, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeShouldBeUnderWorkWhenAnyChildIsUnderWork() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		declare(rootScope.getChild(1), ProgressState.UNDER_WORK);

		assertEquals(ProgressState.UNDER_WORK, rootScope.getProgress().getState());

		declare(rootScope.getChild(0), ProgressState.DONE);

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
		processProgressInference(childB, parentCreation);

		final Scope childA = ScopeTestUtils.createScope(startDay);
		parent.add(childA);
		processProgressInference(childA, startDay);

		ScopeTestUtils.setProgress(childA, ProgressState.UNDER_WORK, startDay);
		processProgressInference(childA, startDay);

		ScopeTestUtils.setProgress(childB, ProgressState.DONE, childBAccomplishDay);
		processProgressInference(childB, childBAccomplishDay);

		ScopeTestUtils.setProgress(childA, ProgressState.DONE, endDay);
		processProgressInference(childA, endDay);

		assertEquals(startDay, parent.getProgress().getStartDay());
		assertEquals(endDay, parent.getProgress().getEndDay());

	}

	private Set<UUID> declare(final Scope child, final ProgressState state) {
		ScopeTestUtils.declareProgress(child, state);
		return processProgressInference(child);
	}

	private Set<UUID> processProgressInference(final Scope scope, final WorkingDay day) {
		return PROGRESS_INFERENCE_ENGINE.process(scope, UserTestUtils.getAdmin(), day.getJavaDate());
	}

	private Set<UUID> procressEffortInference(final Scope rootScope) {
		return EFFORT_INFERENCE_ENGINE.process(rootScope, UserTestUtils.getAdmin(), new Date(0));
	}

	private Set<UUID> processProgressInference(final Scope scope) {
		return processProgressInference(scope, WorkingDayFactory.create(new Date(0)));
	}

}
