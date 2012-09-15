package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class EffortInferenceEngineFlow1Test {

	private static final double ERROR = 1e-1;
	private final String FILE_NAME_PREFIX = "Flow1";
	private Scope original = null;
	private final EffortInferenceEngine effortInferenceEngine = new EffortInferenceEngine();

	@Before
	public void setUp() {
		original = EffortInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
	}

	@Test
	public void testCaseStep04() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
	}

	@Test
	public void testCaseStep05() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
	}

	@Test
	public void testCaseStep06() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
	}

	@Test
	public void testCaseStep07() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeEffortBetweenSiblingWhenOneIsChanged();
	}

	@Test
	public void testCaseStep08() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeEffortBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeEffortForStronglyDeclaredEfforts();
	}

	@Test
	public void testCaseStep09() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeEffortBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeEffortForStronglyDeclaredEfforts();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreEffortAvaliable();
	}

	@Test
	public void testCaseStep10() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeEffortBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeEffortForStronglyDeclaredEfforts();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreEffortAvaliable();
		shouldUpdateRootEffortWhenSomeChildIsChanged();
	}

	@Test
	public void testCaseStep11() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeEffortBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeEffortBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeEffortForStronglyDeclaredEfforts();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreEffortAvaliable();
		shouldUpdateRootEffortWhenSomeChildIsChanged();
		shouldUpdateRootEffortWhenSomeChildIsChanged2();
	}

	private void shouldApplyInferenceTopDownWhenRootIsModified() {
		original.getEffort().setDeclared(1000);
		effortInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded() {
		final Scope newScope = ScopeTestUtils.createScope("Cancelar pedido");
		original.getChild(1).add(newScope);
		effortInferenceEngine.process(newScope.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared() {
		final Scope scopeWithChangedEffort = original.getChild(1);
		scopeWithChangedEffort.getEffort().setDeclared(350);
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		for (final Scope child : scopeWithChangedEffort.getChildren()) {
			assertEquals(87.5, child.getEffort().getInfered(), ERROR);
		}

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared() {
		final Scope scopeWithChangedEffort = original.getChild(1).getChild(0);
		scopeWithChangedEffort.getEffort().setDeclared(150);
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		assertEquals(66.6, original.getChild(1).getChild(1).getEffort().getInfered(), ERROR);
		assertEquals(66.6, original.getChild(1).getChild(2).getEffort().getInfered(), ERROR);
		assertEquals(66.6, original.getChild(1).getChild(3).getEffort().getInfered(), ERROR);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenInferedChanges() {
		final Scope parentScopeWithChildrenModification = original.getChild(1);

		parentScopeWithChildrenModification.getChild(1).getEffort().setDeclared(150);
		effortInferenceEngine.process(parentScopeWithChildrenModification, UserTestUtils.getAdmin(), new Date());

		parentScopeWithChildrenModification.getChild(2).getEffort().setDeclared(150);
		effortInferenceEngine.process(parentScopeWithChildrenModification, UserTestUtils.getAdmin(), new Date());

		parentScopeWithChildrenModification.getChild(3).getEffort().setDeclared(150);
		effortInferenceEngine.process(parentScopeWithChildrenModification, UserTestUtils.getAdmin(), new Date());

		assertEquals(600, parentScopeWithChildrenModification.getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(0).getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(2).getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(3).getEffort().getInfered(), ERROR);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 5), original);
	}

	private void shouldRemoveUnusedInference() {
		final Scope scopeWithChangedEffort = original.getChild(1);
		scopeWithChangedEffort.getEffort().resetDeclared();
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 6), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenOneIsChanged() {
		final Scope scopeWithChangedEffort = original.getChild(0).getChild(0);
		scopeWithChangedEffort.getEffort().setDeclared(30);
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 7), original);
	}

	private void shouldNotDistributeEffortForStronglyDeclaredEfforts() {
		final Scope scopeWithChangedEffort = original.getChild(0).getChild(1);
		scopeWithChangedEffort.getEffort().setDeclared(60);
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 8), original);
	}

	private void shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreEffortAvaliable() {
		original.getEffort().resetDeclared();
		effortInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 9), original);
	}

	private void shouldUpdateRootEffortWhenSomeChildIsChanged() {
		original.getChild(2).getEffort().setDeclared(350);
		effortInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 10), original);
	}

	private void shouldUpdateRootEffortWhenSomeChildIsChanged2() {
		final Scope scopeWithChangedEffort = original.getChild(3).getChild(0);
		scopeWithChangedEffort.getEffort().setDeclared(150);
		effortInferenceEngine.process(scopeWithChangedEffort.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 11), original);
	}

}
