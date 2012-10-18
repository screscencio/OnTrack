package br.com.oncast.ontrack.shared.model.action.scope;

import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.ProgressInferenceTestUtils;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.stringrepresentation.StringRepresentationSymbolsProvider;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ScopeUpdateActionUndoAndRedoTest {

	private final String FILE_NAME_PREFIX = "Flow1";

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgress1()
			throws UnableToCompleteActionException {
		runWith("b1 bla " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "DONE");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgress2()
			throws UnableToCompleteActionException {
		runWith("b1 bla " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "test");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithoutProgress()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithProgressAndEffort()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "7 " + StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "uw");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithEffort()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "7");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithRelease()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "r1");
	}

	@Test
	public void shouldReturnToSameStateAfterMultipleUndosAndRedosInScopeUpdateWithReleaseAndEffortAndProgress()
			throws UnableToCompleteActionException {
		runWith("b1 bla sde " + StringRepresentationSymbolsProvider.RELEASE_SYMBOL + "reelease " + StringRepresentationSymbolsProvider.EFFORT_SYMBOL + "0 "
				+ StringRepresentationSymbolsProvider.PROGRESS_SYMBOL + "NOT_STARTED");
	}

	private void runWith(final String updatePattern) throws UnableToCompleteActionException {
		final Scope currentScope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope parent = currentScope.getChild(1);
		final Scope scope = parent.getChild(0);
		final ProjectContext context = ProjectTestUtils.createProjectContext(currentScope, ReleaseFactoryTestUtil.create("r"));

		DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);

		ModelAction action = new ScopeUpdateAction(scope.getId(), updatePattern);
		ModelAction rollbackAction = executeAction(parent, action, context);

		DeepEqualityTestUtils.assertObjectEquality(getModifiedScope(updatePattern), currentScope);

		for (int i = 0; i < 10; i++) {
			action = executeAction(parent, rollbackAction, context);
			DeepEqualityTestUtils.assertObjectEquality(ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6), currentScope);
			rollbackAction = executeAction(parent, action, context);
			DeepEqualityTestUtils.assertObjectEquality(
					getModifiedScope(updatePattern),
					currentScope);
		}
	}

	private Object getModifiedScope(final String updatePattern)
			throws UnableToCompleteActionException {
		final Scope scope = ProgressInferenceTestUtils.getModifiedScope(FILE_NAME_PREFIX, 6);

		final Scope parent = scope.getChild(1);
		final Scope child = parent.getChild(0);
		executeAction(parent, new ScopeUpdateAction(child.getId(), updatePattern),
				ProjectTestUtils.createProjectContext(scope, ReleaseFactoryTestUtil.create("")));

		return scope;
	}

	private ModelAction executeAction(final Scope scope, final ModelAction action, final ProjectContext context) throws UnableToCompleteActionException {
		final ActionContext actionContext = Mockito.mock(ActionContext.class);
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(Long.MAX_VALUE));

		final ModelAction rollbackAction = action.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(scope);
		return rollbackAction;
	}
}
