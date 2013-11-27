package br.com.oncast.ontrack.shared.model.value;

import br.com.oncast.ontrack.shared.model.prioritizationCriteria.ValueInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;

public class ValueInferenceEngineFlow2Test {

	private final String FILE_NAME_PREFIX = "Flow2";
	private Scope original = null;
	private final ValueInferenceEngine valueInferenceEngine = new ValueInferenceEngine();

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
		shouldRedistributeInferenceBetweenSiblingsWhenParentValueDeclared();
	}

	@Test
	public void testCaseStep03() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentValueDeclared();
		shouldRedistribuiteValueWhenRootValueIsChanged();
	}

	@Test
	public void testCaseStep04() {
		shouldInferBottomUpFromModifiedScopeAndTopDownFromIt();
		shouldRedistributeInferenceBetweenSiblingsWhenParentValueDeclared();
		shouldRedistribuiteValueWhenRootValueIsChanged();
		shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsValue();
	}

	private void shouldInferBottomUpFromModifiedScopeAndTopDownFromIt() {
		original.getValue().setDeclared(30);
		valueInferenceEngine.process(original, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenParentValueDeclared() {
		final Scope a2 = original.getChild(0).getChild(1);
		a2.getValue().setDeclared(10);
		valueInferenceEngine.process(a2.getParent(), UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistribuiteValueWhenRootValueIsChanged() {
		original.getValue().setDeclared(60);
		valueInferenceEngine.process(original, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}

	private void shouldRedistributeInferenceBetweenSiblingsWhenOneChangesItsValue() {
		final Scope a21 = original.getChild(0).getChild(1).getChild(0);
		a21.getValue().setDeclared(7);
		valueInferenceEngine.process(a21.getParent(), UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 4), original);

	}

}
