package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));

		final ActionExecutionListener actionExecutionListener = mock(ActionExecutionListener.class);
		actionExecutionManager = new ActionExecutionManager(actionExecutionListener);

		scopeTreeActionFactoryMock = mock(ScopeTreeActionFactory.class);
		widgetExceptionActionMock = mock(ScopeTreeAction.class);
		doThrow(new ScopeNotFoundException("")).when(widgetExceptionActionMock).execute(context, actionContext, true);

		rootScope = ScopeTestUtils.createScope("root");
		newScopeDescription = "description for new scope";

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));

		final ScopeTreeAction widgetActionMock = mock(ScopeTreeAction.class);
		normalAction = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalAction)).thenReturn(widgetActionMock);

	}

	@Test
	public void executeAnActionMustChangeTheTreeProperly() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context, actionContext);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void ifActionsThrowExceptionNothingHappens() throws UnableToCompleteActionException, ScopeNotFoundException {
		exceptionAction = mock(ScopeInsertChildAction.class);
		doThrow(new UnableToCompleteActionException(null, ActionExecutionErrorMessageCode.UNKNOWN)).when(exceptionAction).execute(Mockito.eq(context), Mockito.any(ActionContext.class));
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(exceptionAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.doUserAction(exceptionAction, context, actionContext);
	}

	@Test(expected = RuntimeException.class)
	public void ifWidgetActionsThrowExceptionThenIsRolledBack() throws UnableToCompleteActionException, ScopeNotFoundException {
		normalActionWithBadWidgetAction = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(normalActionWithBadWidgetAction)).thenReturn(widgetExceptionActionMock);

		actionExecutionManager.doUserAction(normalActionWithBadWidgetAction, context, actionContext);
		verify(normalActionWithBadWidgetAction, atMost(2)).execute(context, actionContext);
	}

	@Test
	public void undoMustRevertChangesAtTheTree() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context, actionContext);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context, actionContext);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void undoWithNoActionsExecutedMustDoNothing() throws UnableToCompleteActionException {
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context, actionContext);

		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoWithNoActionsExecutedMustDoNothing() throws UnableToCompleteActionException {
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context, actionContext);

		assertEquals(0, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoExceptionNormalAction() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		final ScopeTreeAction rollbackWidgetException = mock(ScopeTreeAction.class);
		final ScopeAction rollbackAction = mock(ScopeAction.class);

		when(rollbackException.execute(context, actionContext)).thenReturn(rollbackAction);
		doThrow(new UnableToCompleteActionException(null, ActionExecutionErrorMessageCode.UNKNOWN)).when(rollbackWidgetException).execute(context, actionContext, true);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackAction)).thenReturn(rollbackWidgetException);

		actionExecutionManager.doUserAction(rollbackException, context, actionContext);
		assertEquals(1, rootScope.getChildren().size());
		actionExecutionManager.undoUserAction(context, actionContext);
		assertEquals(1, rootScope.getChildren().size());
	}

	@Test(expected = RuntimeException.class)
	public void executeOkUndoOkRedoException() throws Exception {
		final ScopeInsertChildAction rollbackException = new ScopeInsertChildAction(rootScope.getId(), newScopeDescription);
		final ScopeTreeAction normalWidgetException = mock(ScopeTreeAction.class);
		final ScopeTreeAction execute = mock(ScopeTreeAction.class);

		doThrow(new UnableToCompleteActionException(null, ActionExecutionErrorMessageCode.UNKNOWN)).when(execute).execute(context, actionContext, true);
		when(scopeTreeActionFactoryMock.createEquivalentActionFor(rollbackException)).thenReturn(normalWidgetException).thenReturn(execute);

		actionExecutionManager.doUserAction(rollbackException, context, actionContext);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context, actionContext);
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context, actionContext);
		assertEquals(0, rootScope.getChildren().size());
	}

	@Test
	public void redoAfterUndoMustDontChangeTheTree() throws UnableToCompleteActionException {
		actionExecutionManager.doUserAction(normalAction, context, actionContext);
		assertEquals(1, rootScope.getChildren().size());

		actionExecutionManager.undoUserAction(context, actionContext);
		assertEquals(0, rootScope.getChildren().size());

		actionExecutionManager.redoUserAction(context, actionContext);
		assertEquals(1, rootScope.getChildren().size());
	}
}
