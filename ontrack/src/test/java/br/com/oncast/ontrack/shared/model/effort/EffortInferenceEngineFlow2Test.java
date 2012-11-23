package br.com.oncast.ontrack.shared.model.effort;

import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.effort.EffortInferenceTestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class EffortInferenceEngineFlow2Test {

	private final String FILE_NAME_PREFIX = "Flow2";
	private Scope original = null;
	private final EffortInferenceEngine effortInferenceEngine = new EffortInferenceEngine();

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
		original.getEffort().setDeclared(30);
		effortInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentEffortDeclared() {
		final Scope a2 = original.getChild(0).getChild(1);
		a2.getEffort().setDeclared(10);
		effortInferenceEngine.process(a2.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistribuiteEffortWhenRootEffortIsChanged() {
		original.getEffort().setDeclared(60);
		effortInferenceEngine.process(original, UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsEffort() {
		final Scope a21 = original.getChild(0).getChild(1).getChild(0);
		a21.getEffort().setDeclared(7);
		effortInferenceEngine.process(a21.getParent(), UserTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);

	}

}
