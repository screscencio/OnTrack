package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertChildScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class ScopeTreeWidgetActionManagerTest {

	private ScopeTreeWidgetActionManager scopeManager;
	private Scope rootScope;
	private InsertChildScopeAction normalAction;
	private InsertChildScopeAction normalActionWithBadWidgetAction;
	private InsertChildScopeAction exceptionAction;
	private ScopeTreeWidgetActionFactory factoryMock;
	private ScopeTreeWidgetAction widgetExceptionActionMock;

	@Before
	public void setUp() throws UnableToCompleteActionException {
		factoryMock = mock(ScopeTreeWidgetActionFactory.class);
		rootScope = new Scope("root");
		exceptionAction = mock(InsertChildScopeAction.class);
		normalActionWithBadWidgetAction = new InsertChildScopeAction(rootScope);
		doThrow(new UnableToCompleteActionException("")).when(exceptionAction).rollback();
		normalAction = new InsertChildScopeAction(rootScope);
		final ScopeTreeWidgetAction widgetActionMock = mock(ScopeTreeWidgetAction.class);
		widgetExceptionActionMock = mock(ScopeTreeWidgetAction.class);
		doThrow(new UnableToCompleteActionException("")).when(widgetExceptionActionMock).execute();
		when(factoryMock.getEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);
		when(factoryMock.getEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);
		when(factoryMock.getEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);
		scopeManager = new ScopeTreeWidgetActionManager(factoryMock);
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
	public void executeOkUndoExceptionNormalAction() throws UnableToCompleteActionException {
		final InsertChildScopeAction rollbackException = new InsertChildScopeAction(rootScope);
		final ScopeTreeWidgetAction rollbackWidgetException = mock(ScopeTreeWidgetAction.class);

		doThrow(new UnableToCompleteActionException("")).when(rollbackWidgetException).rollback();
		when(factoryMock.getEquivalentActionFor(rollbackException)).thenReturn(rollbackWidgetException);

		scopeManager.execute(rollbackException);
		assertEquals(1, rootScope.getChildren().size());
		scopeManager.undo();
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws UnableToCompleteActionException {
		final InsertChildScopeAction rollbackException = new InsertChildScopeAction(rootScope);
		final ScopeTreeWidgetAction normalWidgetException = mock(ScopeTreeWidgetAction.class);
		final ScopeTreeWidgetAction execute = mock(ScopeTreeWidgetAction.class);

		doThrow(new UnableToCompleteActionException("")).when(execute).execute();
		when(factoryMock.getEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

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
