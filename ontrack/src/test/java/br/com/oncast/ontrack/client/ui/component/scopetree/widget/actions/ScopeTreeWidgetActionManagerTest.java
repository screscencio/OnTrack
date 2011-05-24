package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class ScopeTreeWidgetActionManagerTest {

	private ScopeTreeActionManager scopeManager;
	private Scope rootScope;
	private ScopeInsertChildAction normalAction;
	private ScopeInsertChildAction normalActionWithBadWidgetAction;
	private ScopeInsertChildAction exceptionAction;
	private ScopeTreeActionFactory factoryMock;
	private ScopeTreeAction widgetExceptionActionMock;

	@Before
	public void setUp() throws Exception {
		factoryMock = mock(ScopeTreeActionFactory.class);
		rootScope = new Scope("root");
		exceptionAction = mock(ScopeInsertChildAction.class);
		normalActionWithBadWidgetAction = new ScopeInsertChildAction(rootScope);
		doThrow(new UnableToCompleteActionException("")).when(exceptionAction).rollback();
		normalAction = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction widgetActionMock = mock(ScopeTreeAction.class);
		widgetExceptionActionMock = mock(ScopeTreeAction.class);
		doThrow(new ScopeNotFoundException("")).when(widgetExceptionActionMock).execute();
		when(factoryMock.createEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);
		when(factoryMock.createEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);
		when(factoryMock.createEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);
		scopeManager = new ScopeTreeActionManager(factoryMock);
	}

	@Test
	public void executeAnActionMustChangeTheTreeProperly() {
		scopeManager.execute(normalAction);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void ifActionsThrowExceptionNothingHappens() {
		scopeManager.execute(exceptionAction);
	}

	@Test(expected = RuntimeException.class)
	public void ifWidgetActionsThrowExceptionThenIsRolledBack() throws UnableToCompleteActionException {
		scopeManager.execute(normalActionWithBadWidgetAction);
		verify(normalActionWithBadWidgetAction, atMost(1)).rollback();
	}

	@Test
	public void undoMustRevertChangesAtTheTree() {
		scopeManager.execute(normalAction);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void undoWithNoActionsExecutedMustThrowException() {
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoWithNoActionsExecutedMustThrowException() {
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo();
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void undoWithExceptionAtWidgetActionDontChangeTheTreeAndThrowRuntimeException() {
		scopeManager.execute(normalActionWithBadWidgetAction);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoExceptionNormalAction() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction rollbackWidgetException = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(rollbackWidgetException).rollback();
		when(factoryMock.createEquivalentActionFor(rollbackException)).thenReturn(rollbackWidgetException);

		scopeManager.execute(rollbackException);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope);
		final ScopeTreeAction normalWidgetException = mock(ScopeTreeAction.class);
		final ScopeTreeAction execute = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException("")).when(execute).execute();
		when(factoryMock.createEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

		scopeManager.execute(rollbackException);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo();
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoAfterUndoMustDontChangeTheTree() {
		scopeManager.execute(normalAction);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(0, rootScope.getChildren().size());
		scopeManager.redo();
		assertEquals(1, rootScope.getChildren().size());
	}
}
