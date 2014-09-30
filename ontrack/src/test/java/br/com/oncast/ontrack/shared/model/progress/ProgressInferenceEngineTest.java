package br.com.oncast.ontrack.shared.model.progress;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.TestUtils;
import br.com.oncast.ontrack.utils.assertions.AssertTestUtils;
import br.com.oncast.ontrack.utils.inference.InferenceEngineTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static br.com.oncast.ontrack.shared.model.progress.ProgressState.DONE;
import static br.com.oncast.ontrack.shared.model.progress.ProgressState.NOT_STARTED;
import static br.com.oncast.ontrack.shared.model.progress.ProgressState.UNDER_WORK;

public class ProgressInferenceEngineTest {

	@Test
	public void insertingASiblingOfADoneScopeShouldUpdateAccomplishedEffort() throws Exception {
		final Scope parent = ScopeTestUtils.createScope();
		declare(parent, 20);
		final Scope child1 = ScopeTestUtils.createScope();
		final Scope child2 = ScopeTestUtils.createScope();
		insertChild(parent, child1);
		declare(child1, DONE);
		assertProgress(DONE, parent);

		final Set<UUID> updatedScopes = insertSibling(child1, child2);

		assertProgress(DONE, child1);
		assertProgress(NOT_STARTED, parent);
		assertAccomplishedEffort(0, child2);
		assertEquals(10, child2.getEffort().getInfered(), TestUtils.TOLERATED_FLOAT_DIFFERENCE);
		assertAccomplishedEffort(10, child1);
		assertAccomplishedEffort(10, parent);
		AssertTestUtils.assertContainsAll(updatedScopes, child1.getId(), parent.getId(), child2.getId());
	}

	@Test
	public void declatrtingDoneToLastNotDoneScopeShouldPropagateDoneToAncestors() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();

		declare(rootScope, 9);

		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}

		assertEquals(100, rootScope.getEffort().getAccomplishedPercentual(), 0);
		final Scope child1 = rootScope.getChild(1);
		declare(child1, ProgressState.NOT_STARTED);
		assertEquals(66.6, rootScope.getEffort().getAccomplishedPercentual(), 0.1);

		final Scope grandChild1 = ScopeTestUtils.createScope();
		insertChild(child1, grandChild1);
		assertEquals(3.0, grandChild1.getEffort().getInfered(), 0);

		final Scope grandChild2 = ScopeTestUtils.createScope();
		insertChild(child1, grandChild2);
		assertEquals(1.5, grandChild2.getEffort().getInfered(), 0);

		Set<UUID> updatedScopes = declare(grandChild1, DONE);
		assertEquals(83.3, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
		assertEquals(3, updatedScopes.size());
		assertTrue(updatedScopes.contains(grandChild1.getId()));
		assertTrue(updatedScopes.contains(child1.getId()));
		assertTrue(updatedScopes.contains(rootScope.getId()));

		updatedScopes = declare(grandChild2, DONE);
		assertEquals(100, rootScope.getEffort().getAccomplishedPercentual(), 0);
		assertEquals(DONE, child1.getProgress().getState());
		assertEquals(DONE, rootScope.getProgress().getState());

		assertEquals(3, updatedScopes.size());
		assertTrue(updatedScopes.contains(grandChild2.getId()));
		assertTrue(updatedScopes.contains(child1.getId()));
		assertTrue(updatedScopes.contains(rootScope.getId()));

		updatedScopes = declare(rootScope, NOT_STARTED);
		assertEquals(DONE, child1.getProgress().getState());
		assertEquals(DONE, rootScope.getProgress().getState());

		assertEquals(2, updatedScopes.size());
		assertTrue(updatedScopes.contains(child1.getId()));
		assertTrue(updatedScopes.contains(rootScope.getId()));
	}

	@Test
	public void declaringAParentAsUnderWorkShouldNotChangeTheChildsStates() throws Exception {
		final Scope rootScope = ScopeTestUtils.getSimpleScope();
		for (final Scope child : rootScope.getChildren()) {
			declare(child, ProgressState.DONE);
		}
		declare(rootScope.getChild(0), UNDER_WORK);
		assertEquals(UNDER_WORK, rootScope.getProgress().getState());

		declare(rootScope, DONE);
		declare(rootScope, UNDER_WORK);
		assertEquals(UNDER_WORK, rootScope.getProgress().getState());

		assertEquals(UNDER_WORK, rootScope.getChild(0).getProgress().getState());
		for (int i = 1; i < rootScope.getChildCount(); i++) {
			assertEquals(DONE, rootScope.getChild(i).getProgress().getState());
		}
	}

	@Test
	public void doneShouldPropagateToAncestors() throws Exception {
		final Scope rootScope = ScopeTestUtils.createScope();

		Scope scope = rootScope;
		for (int i = 0; i < 15; i++) {
			final Scope child = ScopeTestUtils.createScope();
			insertChild(scope, child);
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
			insertChild(scope, child);
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
			insertChild(scope, child);
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

		declare(child1, UNDER_WORK);
		declare(child2, DONE);

		declare(rootScope, DONE);

		for (final Scope child : rootScope.getChildren()) {
			assertTrue(child.getProgress().isDone());
		}

		declare(rootScope, NOT_STARTED);

		assertEquals(NOT_STARTED, rootScope.getChild(0).getProgress().getState());
		assertEquals(UNDER_WORK, child1.getProgress().getState());
		assertEquals(DONE, child2.getProgress().getState());

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
		insertChild(scope, child);
		insertChild(child, grandChild);

		final Set<UUID> influencedScopes = declare(grandChild, ProgressState.DONE);

		assertTrue(scope.getProgress().isDone());

		assertEquals(3, influencedScopes.size());
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
		declare(rootScope.getChild(0), 5);
		declare(rootScope.getChild(0), ProgressState.DONE);

		declare(rootScope.getChild(1), 10);
		declare(rootScope.getChild(1), ProgressState.DONE);

		declare(rootScope.getChild(2), 20);

		assertEquals(42.8, rootScope.getEffort().getAccomplishedPercentual(), 0.1);
	}

	@Test
	public void aScopeProgressPercentageShouldBeDefinedConsideringAllItsChildrenHierarchy() {
		final Scope rootScope = ScopeTestUtils.getScope();

		// Declare and mark as done some scopes
		declare(rootScope.getChild(0).getChild(0).getChild(0), 5);
		declare(rootScope.getChild(0).getChild(0).getChild(0), ProgressState.DONE);
		assertEquals(100, rootScope.getEffort().getAccomplishedPercentual(), 0.1);

		declare(rootScope.getChild(0).getChild(0).getChild(1), 10);
		assertEquals(33.33, rootScope.getEffort().getAccomplishedPercentual(), 0.1);

		declare(rootScope.getChild(0).getChild(1), 10);
		declare(rootScope.getChild(0).getChild(1), ProgressState.DONE);

		declare(rootScope.getChild(1), 10);
		declare(rootScope.getChild(1), ProgressState.DONE);

		declare(rootScope.getChild(2), 20);

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
		insertChild(parent, childB);

		final Scope childA = ScopeTestUtils.createScope(startDay);
		InferenceEngineTestUtils.insertChild(parent, childA, startDay.getJavaDate());

		declare(childA, ProgressState.UNDER_WORK, startDay);

		declare(childB, ProgressState.DONE, childBAccomplishDay);

		declare(childA, ProgressState.DONE, endDay);

		assertEquals(startDay, parent.getProgress().getStartDay());
		assertEquals(endDay, parent.getProgress().getEndDay());

	}

	private Set<UUID> declare(final Scope scope, final ProgressState progress, final WorkingDay day) {
		return InferenceEngineTestUtils.declareProgress(scope, progress, day);
	}

	private Set<UUID> declare(final Scope scope, final ProgressState progress) {
		return InferenceEngineTestUtils.declareProgress(scope, progress);
	}

	private Set<UUID> declare(final Scope scope, final float effort) {
		return InferenceEngineTestUtils.declareEffort(scope, effort);
	}

	private void assertAccomplishedEffort(final float effort, final Scope scope) {
		assertEquals(effort, scope.getEffort().getAccomplished(), 0.1);
	}

	private void assertProgress(final ProgressState state, final Scope scope) {
		assertEquals(state, scope.getProgress().getState());
	}

	private Set<UUID> insertSibling(final Scope sibling, final Scope newScope) {
		return InferenceEngineTestUtils.insertSibling(sibling, newScope);
	}

	private Set<UUID> insertChild(final Scope parent, final Scope newScope) {
		return InferenceEngineTestUtils.insertChild(parent, newScope);
	}

}
