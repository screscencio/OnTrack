package br.com.oncast.ontrack.shared.model.value;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.ValueInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getModifiedScope;
import static br.com.oncast.ontrack.shared.model.value.ValueInferenceTestUtils.getOriginalScope;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertDeepEquals;

public class ValueInferenceEngineFlow3Test {

	private final String FILE_NAME_PREFIX = "Flow3";
	private Scope original = null;
	private final ValueInferenceEngine valueInferenceEngine = new ValueInferenceEngine();

	@Before
	public void setUp() {
		original = getOriginalScope(FILE_NAME_PREFIX);
	}

	@Test
	public void testCaseStep01() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
	}

	@Test
	public void testCaseStep02() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
		shouldRedistributeInferencesWhenChildrenReceiveValueDeclarations();
	}

	@Test
	public void testCaseStep03() throws UnableToCompleteActionException {
		shouldApplyInferenceTopDownThroughChildren();
		shouldRedistributeInferencesWhenChildrenReceiveValueDeclarations();
		shouldRedistributeInferencesWhenSiblingReceiveValueDeclarations();
	}

	private void shouldApplyInferenceTopDownThroughChildren() {
		original.getChild(0).getValue().setDeclared(12);
		valueInferenceEngine.process(original, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 1), original);
	}

	private void shouldRedistributeInferencesWhenChildrenReceiveValueDeclarations() {
		final Scope scope = original.getChild(0).getChild(0);
		scope.getChild(0).getValue().setDeclared(8);
		valueInferenceEngine.process(scope, UserRepresentationTestUtils.getAdmin(), new Date());
		scope.getChild(1).getValue().setDeclared(8);
		valueInferenceEngine.process(scope, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 2), original);
	}

	private void shouldRedistributeInferencesWhenSiblingReceiveValueDeclarations() {
		final Scope scope = original.getChild(0);
		scope.getChild(1).getValue().setDeclared(20);
		valueInferenceEngine.process(scope, UserRepresentationTestUtils.getAdmin(), new Date());

		assertDeepEquals(getModifiedScope(FILE_NAME_PREFIX, 3), original);
	}
}
