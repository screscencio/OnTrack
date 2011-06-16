package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeTreeWidgetActionManagerTest {

	private ActionExecutionManager actionExecutionManager;
	private Scope rootScope;
	private ScopeInsertChildAction normalAction;
	private ScopeInsertChildAction normalActionWithBadWidgetAction;
	private ScopeInsertChildAction exceptionAction;
	private ScopeTreeActionFactory scopeTreeActionFactoryMock;
	private ScopeTreeAction widgetExceptionActionMock;
	private ProjectContext context;

	@Before
	public void setUp() throws Exception {
		final ActionExecutionListener actionExecutionListener = mock(ActionExecutionListener.class);
		actionExecutionManager = new ActionExecutionManager(actionExecutionListener);

		scopeTreeActionFactoryMock = mock(ScopeTreeActionFactory.class);
		widgetExceptionActionMock = mock(ScopeTreeAction.class);
		doThrow(new ScopeNotFoundException("")).when(widgetExceptionActionMock).execute(context);

		rootScope = new Scope("root");
		context = new ProjectContext(new Project(rootScope, new Release("")));

		final ScopeTreeAction widgetActionMock = mock(ScopeTreeAction.class);
		normalAction = new ScopeInsertChildAction(rootScope);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);

	}

	@Test
	public void executeAnActionMustChangeTheTreeProperly() {
		actionExecutionManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void ifActionsThrowExceptionNothingHappens() throws UnableToCompleteActionException, ScopeNotFoundException {
		exceptionAction = mock(ScopeInsertChildAction.class);
		doThrow(new UnableToCompleteActionException("")).when(exceptionAction).execute(context);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.execute(exceptionAction, context);
	}

	@Test(expected = RuntimeException.class)
	public void ifWidgetActionsThrowExceptionThenIsRolledBack() throws UnableToCompleteActionException, ScopeNotFoundException {
		normalActionWithBadWidgetAction = new ScopeInsertChildAction(rootScope);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.execute(normalActionWithBadWidgetAction, context);
		verify(normalActionWithBadWidgetAction, atMost(1)).rollback(context);
	}

	@Test
	public void undoMustRevertChangesAtTheTree() {
		actionExecutionManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void undoWithNoActionsExecutedMustDoNothing() {
		assertEquals(0, rootScope.getChildren().size());
		actionExecutionManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoWithNoActionsExecutedMustDoNothing() {
		assertEquals(0, rootScope.getChildren().size());
		actionExecutionManager.redo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoExceptionNormalAction() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction rollbackWidgetException = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(rollbackWidgetException).rollback(context);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackException)).thenReturn(rollbackWidgetException);

		actionExecutionManager.execute(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());
		actionExecutionManager.undo(context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction normalWidgetException = mock(ScopeTreeAction.class);
		final ScopeTreeAction execute = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(execute).execute(context);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

		actionExecutionManager.execute(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());
		actionExecutionManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
		actionExecutionManager.redo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoAfterUndoMustDontChangeTheTree() {
		actionExecutionManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
		actionExecutionManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
		actionExecutionManager.redo(context);
		assertEquals(1, rootScope.getChildren().size());
	}
}
