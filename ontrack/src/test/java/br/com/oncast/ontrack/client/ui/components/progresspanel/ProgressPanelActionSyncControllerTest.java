package br.com.oncast.ontrack.client.ui.components.progresspanel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanActionSyncController.Display;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanActionSyncController.ReleaseMonitor;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class ProgressPanelActionSyncControllerTest {

	private ActionExecutionListener actionExecutionListener;
	private Display display;
	private ProjectContext context;
	private Release myRelease;
	private ReleaseMonitor releaseMonitor;
	private ActionContext actionContext;
	private Scope rootScope;
	private ActionExecutionContext actionExecutionContext;

	@Before
	public void setUp() throws Exception {
		display = Mockito.mock(Display.class);
		context = mock(ProjectContext.class);
		actionContext = mock(ActionContext.class);
		actionExecutionContext = mock(ActionExecutionContext.class);
		myRelease = createRelease();
		rootScope = createScope();

		final ActionExecutionService actionExecutionServiceMock = mock(ActionExecutionService.class);
		final ArgumentCaptor<ActionExecutionListener> captor = ArgumentCaptor.forClass(ActionExecutionListener.class);
		when(actionExecutionServiceMock.addActionExecutionListener(captor.capture())).thenReturn(null);

		final KanbanActionSyncController actionSyncController = new KanbanActionSyncController(actionExecutionServiceMock, myRelease, display,
				mock(ClientErrorMessages.class));
		releaseMonitor = actionSyncController.new ReleaseMonitor(myRelease);
		ReflectionTestUtils.set(actionSyncController, "releaseMonitor", releaseMonitor);
		actionSyncController.registerActionExecutionListener();
		actionExecutionListener = captor.getValue();
	}

	@Test
	public void insertingChildInAScopeBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child = createScope();
		scope.add(child);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child.getId(), scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void removeChildInAScopeBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child = createScope();
		scope.add(child);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child.getId(), scope.getId()));

		scope.remove(child);

		onActionExecution(createScoperemoveAction(ScopeRemoveAction.class, child.getId()));

		shouldOnlyBeUpdated(2);
	}

	@Test
	public void removeChildWithGrandchildInAScopeBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child = createScope();
		scope.add(child);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child.getId(), scope.getId()));

		final Scope grandchild = createScope();
		child.add(grandchild);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, grandchild.getId(), child.getId()));

		scope.remove(child);

		onActionExecution(createScoperemoveAction(ScopeRemoveAction.class, child.getId()));

		shouldOnlyBeUpdated(3);
	}

	@Test
	public void insertingGrandChildInAScopeBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child = createScope();
		scope.add(child);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child.getId(), scope.getId()));

		final Scope grandchild = createScope();
		child.add(grandchild);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, grandchild.getId(), child.getId()));
		shouldOnlyBeUpdated(2);
	}

	@Test
	public void insertingSiblingChildInAScopeWithChildBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child = createScope();
		scope.add(child);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child.getId(), scope.getId()));

		final Scope grandchild = createScope();
		child.add(grandchild);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, grandchild.getId(), child.getId()));

		final Scope sibling = createScope();
		scope.add(sibling);
		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingDownAction.class, sibling.getId(), child.getId()));

		shouldOnlyBeUpdated(3);
	}

	@Test
	public void insertingSiblingInAChildScopeBoundToMyReleaseShouldUpdateTheDisplay() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		final Scope child1 = createScope();
		scope.add(child1);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, child1.getId(), scope.getId()));

		final Scope child2 = createScope();
		scope.add(child2);

		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingDownAction.class, child2.getId(), child1.getId()));
		shouldOnlyBeUpdated(2);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertChildActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, scope.getId(), rootScope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertChildActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertChildAction.class, scope.getId(), rootScope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertParentActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertParentAction.class, scope.getId(), rootScope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertParentActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertParentAction.class, scope.getId(), rootScope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeRemoveRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeRemoveRollbackAction.class, scope.getId(), rootScope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeRemoveRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeRemoveRollbackAction.class, scope.getId(), rootScope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertSiblingDownActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingDownAction.class, scope.getId(), rootScope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertSiblingDownActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingDownAction.class, scope.getId(), rootScope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertSiblingUpActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingUpAction.class, scope.getId(), rootScope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertSiblingUpActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createScopeInsertionAction(ScopeInsertSiblingUpAction.class, scope.getId(), rootScope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAReleaseScopeUpdatePriorityActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseScopeUpdatePriorityAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseScopeUpdatePriorityActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ReleaseScopeUpdatePriorityAction.class, myRelease.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeRemoveActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeRemoveAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeRemoveActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeRemoveAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeUpdateActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeUpdateAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeUpdateActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeUpdateAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertSiblingUpRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeInsertSiblingUpRollbackAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertSiblingUpRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeInsertSiblingUpRollbackAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertSiblingDownRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeInsertSiblingDownRollbackAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertSiblingDownRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeInsertSiblingDownRollbackAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertParentRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeInsertParentRollbackAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertParentRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeInsertParentRollbackAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeInsertChildRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeInsertChildRollbackAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeInsertChildRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeInsertChildRollbackAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeBindReleaseActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeBindReleaseAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeBindReleaseActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeBindReleaseAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAScopeDeclareProgressActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeDeclareProgressAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeDeclareProgressActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeDeclareProgressAction.class, scope.getId()));
		shouldOnlyBeUpdated(1);
	}

	@Test
	public void shouldDoNothingWhenAReleaseRenameActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseRenameAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseRenameActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ReleaseRenameAction.class, myRelease.getId()));
		shouldOnlyHaveTheTitleUpdated();
	}

	@Test
	public void shouldDoNothingWhenAReleaseRemoveActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseRemoveAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseRemoveActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);
		when(context.findRelease(myRelease.getId())).thenThrow(new ReleaseNotFoundException());

		onActionExecution(createAction(ReleaseRemoveAction.class, myRelease.getId()));
		shouldOnlyHaveExited();
	}

	@Test
	public void shouldDoNothingWhenAReleaseCreateActionDefaultWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseCreateAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseCreateActionDefaultWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ReleaseCreateAction.class, myRelease.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldDoNothingWhenAReleaseRemoveRollbackActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseRemoveRollbackAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseRemoveRollbackActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ReleaseRemoveRollbackAction.class, myRelease.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldDoNothingWhenAReleaseUpdatePriorityActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ReleaseUpdatePriorityAction.class, releaseNotBeingShown.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAReleaseUpdatePriorityActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ReleaseUpdatePriorityAction.class, myRelease.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldDoNothingWhenAScopeDeclareEffortActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeDeclareEffortAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeDeclareEffortActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeDeclareEffortAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldDoNothingWhenAScopeDeclareValueActionWithCreatedScopeNotOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		final Release releaseNotBeingShown = createRelease();
		releaseNotBeingShown.addScope(scope);

		onActionExecution(createAction(ScopeDeclareValueAction.class, scope.getId()));
		shouldBeIgnored();
	}

	@Test
	public void shouldUpdateWhenAScopeDeclareValueActionWithCreatedScopeOnReleaseOccurs() throws Exception {
		final Scope scope = createScope();
		myRelease.addScope(scope);

		onActionExecution(createAction(ScopeDeclareValueAction.class, scope.getId()));
		shouldBeIgnored();
	}

	private void onActionExecution(final ModelAction action) throws UnableToCompleteActionException {
		actionExecutionListener.onActionExecution(action, context, actionContext, actionExecutionContext, true);
	}

	private void addToContext(final Release release) throws ReleaseNotFoundException {
		when(context.findRelease(release.getId())).thenReturn(release);
	}

	private void addToContext(final Scope scope) throws ScopeNotFoundException {
		when(context.findScope(scope.getId())).thenReturn(scope);
	}

	private <T extends ModelAction> ModelAction createAction(final Class<T> clazz, final UUID referenceId) {
		final T mock = Mockito.mock(clazz);
		when(mock.getReferenceId()).thenReturn(referenceId);
		return mock;
	}

	private <T extends ScopeInsertAction> ScopeInsertAction createScopeInsertionAction(final Class<T> clazz, final UUID scopeReferenceId,
			final UUID releaseReferenceId) {
		final T mock = Mockito.mock(clazz);
		when(mock.getNewScopeId()).thenReturn(scopeReferenceId);
		when(mock.getReferenceId()).thenReturn(releaseReferenceId);

		return mock;
	}

	private <T extends ScopeRemoveAction> ScopeRemoveAction createScoperemoveAction(final Class<T> clazz, final UUID scopeReferenceId) {
		final T mock = Mockito.mock(clazz);
		when(mock.getReferenceId()).thenReturn(scopeReferenceId);

		return mock;
	}

	private Release createRelease() throws ReleaseNotFoundException {
		final Release release = ReleaseTestUtils.createRelease();
		addToContext(release);
		return release;
	}

	private Scope createScope() throws ScopeNotFoundException {
		final Scope scope = ScopeTestUtils.createScope();
		addToContext(scope);
		return scope;
	}

	private void shouldOnlyBeUpdated(final int times) {
		verify(display, times(times)).update();
		verify(display, never()).exit();
		verify(display, never()).updateReleaseInfo();
	}

	private void shouldOnlyHaveExited() {
		verify(display, never()).update();
		verify(display, times(1)).exit();
		verify(display, never()).updateReleaseInfo();
	}

	private void shouldOnlyHaveTheTitleUpdated() {
		verify(display, times(1)).updateReleaseInfo();
		verify(display, never()).update();
		verify(display, never()).exit();
	}

	private void shouldBeIgnored() {
		verify(display, never()).update();
		verify(display, never()).exit();
		verify(display, never()).updateReleaseInfo();
	}

}
