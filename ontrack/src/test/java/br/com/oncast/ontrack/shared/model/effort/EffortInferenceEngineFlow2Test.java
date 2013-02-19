package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;
import static br.com.oncast.ontrack.utils.inference.InferenceEngineTestUtils.declare;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class EffortInferenceEngineFlow2Test {

	private final String FILE_NAME_PREFIX = "Flow2";
	private Scope original = null;

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
	}

	@Test
	public void testCaseStep02() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
	}

	@Test
	public void testCaseStep03() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
		shouldRedistribuiteEffortWhenRootEffortIsChanged();
	}

	@Test
	public void testCaseStep04() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared();
		shouldRedistribuiteEffortWhenRootEffortIsChanged();
		shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort();
	}

	private void shouldInferBottomUpFromModifiedScopeAndTopDownFromIt() {
		declare(original, 30);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared() {
		declare(original.getChild(0).getChild(1), 10);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistribuiteEffortWhenRootEffortIsChanged() {
		declare(original, 60);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort() {
		declare(original.getChild(0).getChild(1).getChild(0), 7);
		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);
	}

}
