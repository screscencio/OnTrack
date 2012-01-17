package br.com.oncast.ontrack.shared.model.value;

import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ValueInferenceEngineFlow1Test {

	private final String FILE_NAME_PREFIX = "Flow1";
	private Scope original = null;
	private final ValueInferenceEngine valueInferenceEngine = new ValueInferenceEngine();

	@Before
	public void setUp() {
		original = ValueInferenceTestUtils.getOriginalScope(FILE_NAME_PREFIX);
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
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
	}

	@Test
	public void testCaseStep04() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
	}

	@Test
	public void testCaseStep05() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
	}

	@Test
	public void testCaseStep06() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
	}

	@Test
	public void testCaseStep07() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeValueBetweenSiblingWhenOneIsChanged();
	}

	@Test
	public void testCaseStep08() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeValueBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeValueForStronglyDeclaredValues();
	}

	@Test
	public void testCaseStep09() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeValueBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeValueForStronglyDeclaredValues();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreValueAvaliable();
	}

	@Test
	public void testCaseStep10() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeValueBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeValueForStronglyDeclaredValues();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreValueAvaliable();
		shouldUpdateRootValueWhenSomeChildIsChanged();
	}

	@Test
	public void testCaseStep11() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownWhenRootIsModified();
		shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded();
		shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenOneIsDeclared();
		shouldRedistributeValueBetweenSiblingWhenInferedChanges();
		shouldRemoveUnusedInference();
		shouldRedistributeValueBetweenSiblingWhenOneIsChanged();
		shouldNotDistributeValueForStronglyDeclaredValues();
		shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreValueAvaliable();
		shouldUpdateRootValueWhenSomeChildIsChanged();
		shouldUpdateRootValueWhenSomeChildIsChanged2();
	}

	private void shouldApplyInferenceTopDownWhenRootIsModified() {
		original.getValue().setDeclared(1000);
		valueInferenceEngine.process(original);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneIsAdded() {
		final Scope newScope = new Scope("Cancelar pedido");
		original.getChild(1).add(newScope);
		valueInferenceEngine.process(newScope.getParent());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistributeValueBetweenChildrenWhenParentValueIsDeclared() {
		final Scope scopeWithChangedValue = original.getChild(1);
		scopeWithChangedValue.getValue().setDeclared(350);
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		for (final Scope child : scopeWithChangedValue.getChildren()) {
			assertEquals(87.5, child.getValue().getInfered(), 0.09);
		}

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeValueBetweenSiblingWhenOneIsDeclared() {
		final Scope scopeWithChangedValue = original.getChild(1).getChild(0);
		scopeWithChangedValue.getValue().setDeclared(150);
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		assertEquals(66.6, original.getChild(1).getChild(1).getValue().getInfered(), 0.09);
		assertEquals(66.6, original.getChild(1).getChild(2).getValue().getInfered(), 0.09);
		assertEquals(66.6, original.getChild(1).getChild(3).getValue().getInfered(), 0.09);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);
	}

	private void shouldRedistributeValueBetweenSiblingWhenInferedChanges() {
		final Scope parentScopeWithChildrenModification = original.getChild(1);

		parentScopeWithChildrenModification.getChild(1).getValue().setDeclared(150);
		valueInferenceEngine.process(parentScopeWithChildrenModification);

		parentScopeWithChildrenModification.getChild(2).getValue().setDeclared(150);
		valueInferenceEngine.process(parentScopeWithChildrenModification);

		parentScopeWithChildrenModification.getChild(3).getValue().setDeclared(150);
		valueInferenceEngine.process(parentScopeWithChildrenModification);

		assertEquals(600, parentScopeWithChildrenModification.getValue().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(0).getValue().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(2).getValue().getInfered(), 0.09);
		assertEquals(133.3, original.getChild(3).getValue().getInfered(), 0.09);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 5), original);
	}

	private void shouldRemoveUnusedInference() {
		final Scope scopeWithChangedValue = original.getChild(1);
		scopeWithChangedValue.getValue().resetDeclared();
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 6), original);
	}

	private void shouldRedistributeValueBetweenSiblingWhenOneIsChanged() {
		final Scope scopeWithChangedValue = original.getChild(0).getChild(0);
		scopeWithChangedValue.getValue().setDeclared(30);
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 7), original);
	}

	private void shouldNotDistributeValueForStronglyDeclaredValues() {
		final Scope scopeWithChangedValue = original.getChild(0).getChild(1);
		scopeWithChangedValue.getValue().setDeclared(60);
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 8), original);
	}

	private void shouldRemoveUnusedInferenceForChildrenIfThereIsNoMoreValueAvaliable() {
		original.getValue().resetDeclared();
		valueInferenceEngine.process(original);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 9), original);
	}

	private void shouldUpdateRootValueWhenSomeChildIsChanged() {
		original.getChild(2).getValue().setDeclared(350);
		valueInferenceEngine.process(original);

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 10), original);
	}

	private void shouldUpdateRootValueWhenSomeChildIsChanged2() {
		final Scope scopeWithChangedValue = original.getChild(3).getChild(0);
		scopeWithChangedValue.getValue().setDeclared(150);
		valueInferenceEngine.process(scopeWithChangedValue.getParent());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 11), original);
	}

}
