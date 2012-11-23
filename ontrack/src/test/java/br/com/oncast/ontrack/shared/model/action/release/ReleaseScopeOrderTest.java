package br.com.oncast.ontrack.shared.model.action.release;

import static org.mockito.Mockito.when;

import java.util.Date;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.deepEquality.custom.DeepEqualityComparator;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ReleaseScopeOrderTest {

	private static final ScopeDeepComparator SCOPE_DEEP_COMPARATOR_INSTANCE = new ScopeDeepComparator();

	private static class ScopeDeepComparator implements DeepEqualityComparator<Scope> {

		protected ScopeDeepComparator() {}

		@Override
		public void assertObjectEquality(final Scope expected, final Scope actual) throws DeepEqualityException {
			Assert.assertEquals(expected.getId(), actual.getId());
		}
	}

	@BeforeClass
	public static void setUpClass() {
		DeepEqualityTestUtils.setCustomDeepEqualityComparator(Scope.class, SCOPE_DEEP_COMPARATOR_INSTANCE);
	}

	@AfterClass
	public static void tearDownClass() {
		DeepEqualityTestUtils.removeCustomDeepEqualityComparator(Scope.class);
	}

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));
	}

	@Test
	public void testThatScopeUpdateActionDoesntChangeReleaseOrder1() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeUpdateAction(new UUID("s11"), "11 @rootRelease/release1");
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeUpdateActionDoesntChangeReleaseOrder2() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeUpdateAction(new UUID("s12"), "12 @rootRelease/release1");
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeUpdateActionDoesntChangeReleaseOrder4() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeUpdateAction(new UUID("s42"), "42 @rootRelease/release2");
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeUpdateActionDoesntChangeReleaseOrderAfterDoingAndUndoingIt() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeUpdateAction(new UUID("s11"), "11 @rootRelease/release1");
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeUpdateActionDoesntChangeReleaseOrderAfterDoingUndoingAndRedoingIt() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeUpdateAction(new UUID("s11"), "11 @rootRelease/release1");
		final ActionExecutionContext executionContextDo = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContextUndo = ActionExecuter.executeAction(context, actionContext,
				executionContextDo.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContextUndo.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeRemoveActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeRemoveAction(new UUID("s12"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeRemoveActionDoesntChangeReleaseOrderAfterUndoRedoAndUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeRemoveAction(new UUID("s12"));
		final ActionExecutionContext executionContext1 = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContext2 = ActionExecuter.executeAction(context, actionContext,
				executionContext1.getReverseAction());
		final ActionExecutionContext executionContext3 = ActionExecuter.executeAction(context, actionContext,
				executionContext2.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContext3.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatReleaseRemoveActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ReleaseRemoveAction(new UUID("release1"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatReleaseRemoveActionDoesntChangeReleaseOrderAfterUndoRedoAndUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ReleaseRemoveAction(new UUID("release1"));
		final ActionExecutionContext executionContext1 = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContext2 = ActionExecuter.executeAction(context, actionContext,
				executionContext1.getReverseAction());
		final ActionExecutionContext executionContext3 = ActionExecuter.executeAction(context, actionContext,
				executionContext2.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContext3.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveUpActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveUpAction(new UUID("s12"));
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveUpActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveUpAction(new UUID("s12"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveDownActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveDownAction(new UUID("s12"));
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveDownActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveDownAction(new UUID("s12"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveLeftActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveLeftAction(new UUID("s12"));
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveLeftActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveLeftAction(new UUID("s12"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveRightActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveRightAction(new UUID("s12"));
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeMoveRightActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeMoveRightAction(new UUID("s12"));
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeDeclareProgressActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeDeclareProgressAction(new UUID("s12"), "DONE");
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeDeclareProgressActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeDeclareProgressAction(new UUID("s12"), "DONE");
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeDeclareEffortActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeDeclareEffortAction(new UUID("s12"), true, 5);
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeDeclareEffortActionDoesntChangeReleaseOrderAfterUndo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeDeclareEffortAction(new UUID("s12"), true, 5);
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrder() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release1");
		ActionExecuter.executeAction(context, actionContext, action);

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrderAfterUndo1() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release1");
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrderAfterUndo2() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release3");
		final ActionExecutionContext executionContext = ActionExecuter.executeAction(context, actionContext, action);
		ActionExecuter.executeAction(context, actionContext, executionContext.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrderAfterUndoAndRedo() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release1");
		final ActionExecutionContext executionContext1 = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContext2 = ActionExecuter.executeAction(context, actionContext,
				executionContext1.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContext2.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrderAfterUndoRedoAndUndo1() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release1");
		final ActionExecutionContext executionContext1 = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContext2 = ActionExecuter.executeAction(context, actionContext,
				executionContext1.getReverseAction());
		final ActionExecutionContext executionContext3 = ActionExecuter.executeAction(context, actionContext,
				executionContext2.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContext3.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@Test
	public void testThatScopeBindReleaseActionDoesntChangeReleaseOrderAfterUndoRedoAndUndo2() throws UnableToCompleteActionException {
		final Project project = getProjectMock();
		final ProjectContext context = new ProjectContext(project);

		final ModelAction action = new ScopeBindReleaseAction(new UUID("s12"), "release5");
		final ActionExecutionContext executionContext1 = ActionExecuter.executeAction(context, actionContext, action);
		final ActionExecutionContext executionContext2 = ActionExecuter.executeAction(context, actionContext,
				executionContext1.getReverseAction());
		final ActionExecutionContext executionContext3 = ActionExecuter.executeAction(context, actionContext,
				executionContext2.getReverseAction());
		ActionExecuter.executeAction(context, actionContext, executionContext3.getReverseAction());

		DeepEqualityTestUtils.assertObjectEquality(getProjectMock().getProjectRelease(), project.getProjectRelease());
	}

	@SuppressWarnings("unused")
	private Project getProjectMock() {
		final Release release0 = createRelease("rootRelease", null);
		final Release release1 = createRelease("release1", release0);
		final Release release2 = createRelease("release2", release0);
		final Scope scope0 = createScope("s0", null, null);
		final Scope scope1 = createScope("s1", null, scope0);
		final Scope scope11 = createScope("s11", release1, scope1);
		final Scope scope12 = createScope("s12", release1, scope1);
		final Scope scope13 = createScope("s13", release2, scope1);
		final Scope scope2 = createScope("s2", release2, scope0);
		final Scope scope3 = createScope("s3", release1, scope0);
		final Scope scope4 = createScope("s4", null, scope0);
		final Scope scope41 = createScope("s41", release1, scope4);
		final Scope scope42 = createScope("s42", release2, scope4);
		final Scope scope43 = createScope("s43", null, scope4);
		final Scope scope431 = createScope("s431", release1, scope43);
		final Scope scope432 = createScope("s432", null, scope43);
		final Scope scope5 = createScope("s5", release2, scope0);
		return ProjectTestUtils.createProject(scope0, release0);
	}

	private Release createRelease(final String id, final Release parent) {
		final Release release = new Release(id, new UUID(id));
		if (parent != null) parent.addChild(release);
		return release;
	}

	private Scope createScope(final String id, final Release release, final Scope parent) {
		final Scope scope = ScopeTestUtils.createScope(id, new UUID(id));
		if (release != null) release.addScope(scope);
		if (parent != null) parent.add(scope);
		return scope;
	}
}
