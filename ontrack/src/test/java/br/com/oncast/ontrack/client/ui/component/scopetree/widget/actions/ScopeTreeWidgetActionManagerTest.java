package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.shared.model.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ScopeTreeWidgetActionManagerTest {

	private Scope rootScope;
	private String newScopeDescription;
	private ProjectContext context;

	private ScopeInsertChildAction normalAction;
	private ScopeInsertChildAction normalActionWithBadWidgetAction;
	private ScopeInsertChildAction exceptionAction;

	private ScopeTreeActionFactory scopeTreeActionFactoryMock;
	private ScopeTreeAction widgetExceptionActionMock;

	private ActionExecutionManager actionExecutionManager;

	@Before
	public void setUp() throws Exception {
		final ActionExecutionListener actionExecutionListener = mock(ActionExecutionListener.class);
		actionExecutionManager = new ActionExecutionManager(actionExecutionListener);

		scopeTreeActionFactoryMock = mock(ScopeTreeActionFactory.class);
		widgetExceptionActionMock = mock(ScopeTreeAction.class);
		doThrow(new ScopeNotFoundException("")).when(widgetExceptionActionMock).execute(context, true);

		rootScope = new Scope("root");
		newScopeDescription = "description for new scope";

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));

		final ScopeTreeAction widgetActionMock = mock(ScopeTreeAction.class);
		normalAction = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);

	}

	@Test
	public void executeAnActionMustChangeTheTreeProperly() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void ifActionsThrowExceptionNothingHappens() throws UnableToCompleteActionException, ScopeNotFoundException {
		exceptionAction = mock(ScopeInsertChildAction.class);
		doThrow(new UnableToCompleteActionException("")).when(exceptionAction).execute(context);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.doUserAction(exceptionAction, context);
	}

	@Test(expected = RuntimeException.class)
	public void ifWidgetActionsThrowExceptionThenIsRolledBack() throws UnableToCompleteActionException, ScopeNotFoundException {
		normalActionWithBadWidgetAction = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.doUserAction(normalActionWithBadWidgetAction, context);
		verify(normalActionWithBadWidgetAction, atMost(2)).execute(context);
	}

	@Test
	public void undoMustRevertChangesAtTheTree() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void undoWithNoActionsExecutedMustDoNothing() throws UnableToCompleteActionException {
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context);

		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoWithNoActionsExecutedMustDoNothing() throws UnableToCompleteActionException {
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context);

		assertEquals(0, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoExceptionNormalAction() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		final ScopeTreeAction rollbackWidgetException = mock(ScopeTreeAction.class);
		final ScopeAction rollbackAction = mock(ScopeAction.class);

		when(rollbackException.execute(context)).thenReturn(rollbackAction);
		doThrow(new UnableToCompleteActionException("")).when(rollbackWidgetException).execute(context, true);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackAction)).thenReturn(rollbackWidgetException);

		actionExecutionManager.doUserAction(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());
		actionExecutionManager.undoUserAction(context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		final ScopeTreeAction normalWidgetException = mock(ScopeTreeAction.class);
		final ScopeTreeAction execute = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(execute).execute(context, true);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

		actionExecutionManager.doUserAction(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context);
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoAfterUndoMustDontChangeTheTree() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context);
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context);
		assertEquals(1, rootScope.getChildren().size());
	}
}
