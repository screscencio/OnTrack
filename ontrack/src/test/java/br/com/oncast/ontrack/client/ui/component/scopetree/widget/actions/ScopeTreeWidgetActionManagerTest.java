package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionManager;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeTreeWidgetActionManagerTest {

	private ActionManager scopeManager;
	private Scope rootScope;
	private ScopeInsertChildAction normalAction;
	private ScopeInsertChildAction normalActionWithBadWidgetAction;
	private ScopeInsertChildAction exceptionAction;
	private ScopeTreeActionFactory factoryMock;
	private ScopeTreeAction widgetExceptionActionMock;
	private ProjectContext context;

	@Before
	public void setUp() throws Exception {
		factoryMock = mock(ScopeTreeActionFactory.class);
		rootScope = new Scope("root");
		exceptionAction = mock(ScopeInsertChildAction.class);
		normalActionWithBadWidgetAction = new ScopeInsertChildAction(rootScope);
		context = mock(ProjectContext.class);
		doThrow(new UnableToCompleteActionException("")).when(exceptionAction).execute(context);
		normalAction = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction widgetActionMock = mock(ScopeTreeAction.class);
		widgetExceptionActionMock = mock(ScopeTreeAction.class);
		doThrow(new ScopeNotFoundException("")).when(widgetExceptionActionMock).execute();
		when(factoryMock.createEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);
		when(factoryMock.createEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);
		when(factoryMock.createEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);
		scopeManager = new ActionManager(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ScopeAction action, final boolean wasRollback) {

			}
		});
	}

	@Test
	public void executeAnActionMustChangeTheTreeProperly() {
		scopeManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void ifActionsThrowExceptionNothingHappens() {
		scopeManager.execute(exceptionAction, context);
	}

	@Test(expected = RuntimeException.class)
	public void ifWidgetActionsThrowExceptionThenIsRolledBack() throws UnableToCompleteActionException {
		scopeManager.execute(normalActionWithBadWidgetAction, context);
		verify(normalActionWithBadWidgetAction, atMost(1)).rollback(context);
	}

	@Test
	public void undoMustRevertChangesAtTheTree() {
		scopeManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void undoWithNoActionsExecutedMustThrowException() {
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoWithNoActionsExecutedMustThrowException() {
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoExceptionNormalAction() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction rollbackWidgetException = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(rollbackWidgetException).rollback();
		when(factoryMock.createEquivalentActionFor(rollbackException)).thenReturn(rollbackWidgetException);

		scopeManager.execute(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo(context);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction normalWidgetException = mock(ScopeTreeAction.class);
		final ScopeTreeAction execute = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(execute).execute();
		when(factoryMock.createEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

		scopeManager.execute(rollbackException, context);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo(context);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoAfterUndoMustDontChangeTheTree() {
		scopeManager.execute(normalAction, context);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo(context);
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo(context);
		assertEquals(1, rootScope.getChildren().size());
	}
}
