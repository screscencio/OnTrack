package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.inference.InferenceEngineTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

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
		declare(original, 1000);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded() {
		final Scope newScope = ScopeTestUtils.createScope("Cancelar pedido");
		original.getChild(1).add(newScope);
		effortInferenceEngine.process(newScope, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistributeEffortBetweenChildrenWhenParentEffortIsDeclared() {
		final Scope scope = original.getChild(1);
		declare(scope, 350);

		for (final Scope child : scope.getChildren()) {
			assertEquals(87.5, child.getEffort().getInfered(), ERROR);
		}

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenOneIsDeclared() {
		declare(original.getChild(1).getChild(0), 150);

		assertEquals(66.6, original.getChild(1).getChild(1).getEffort().getInfered(), ERROR);
		assertEquals(66.6, original.getChild(1).getChild(2).getEffort().getInfered(), ERROR);
		assertEquals(66.6, original.getChild(1).getChild(3).getEffort().getInfered(), ERROR);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenInferedChanges() {
		final Scope parent = original.getChild(1);

		declare(parent.getChild(1), 150);
		declare(parent.getChild(2), 150);
		declare(parent.getChild(3), 150);

		assertEquals(600, parent.getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(0).getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(2).getEffort().getInfered(), ERROR);
		assertEquals(133.3, original.getChild(3).getEffort().getInfered(), ERROR);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 5), original);
	}

	private void shouldRemoveUnusedInference() {
		resetDeclared(original.getChild(1));
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 6), original);
	}

	private void shouldRedistributeEffortBetweenSiblingWhenOneIsChanged() {
		declare(original.getChild(0).getChild(0), 30);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 7), original);
	}

	private void shouldNotDistributeEffortForStronglyDeclaredEfforts() {
		declare(original.getChild(0).getChild(1), 60);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 8), original);
	}

	private void shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreEffortAvaliable() {
		resetDeclared(original);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 9), original);
	}

	private void shouldUpdateRootEffortWhenSomeChildIsChanged() {
		declare(original.getChild(2), 350);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 10), original);
	}

	private void shouldUpdateRootEffortWhenSomeChildIsChanged2() {
		declare(original.getChild(3).getChild(0), 150);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 11), original);
	}

	private void declare(final Scope scope, final float effort) {
		InferenceEngineTestUtils.declareEffort(scope, effort);
	}

	private void resetDeclared(final Scope scope) {
		scope.getEffort().resetDeclared();
		effortInferenceEngine.process(scope, UserRepresentationTestUtils.getAdmin(), new Date());
	}

}
