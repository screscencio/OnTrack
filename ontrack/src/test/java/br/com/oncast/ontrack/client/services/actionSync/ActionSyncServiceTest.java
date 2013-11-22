package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.client.i18n.ClientMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.internet.NetworkMonitoringService;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.NullAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

public class ActionSyncServiceTest {

	private ActionSyncService syncer;

	@Mock
	private ModelAction action;

	@Mock
	private ProjectContext context;

	@Mock
	private ActionContext actionContext;

	@Mock
	private ActionExecutionContext executionContext;

	@Mock
	private ActionDispatcher dispatcher;

	@Mock
	private ActionExecutionService actionExecutionService;

	@Mock
	private ClientAlertingService alertingService;

	@Mock
	private ClientStorageService storage;

	@Mock
	private NetworkMonitoringService networkMonitor;

	@Mock
	private ContextProviderService contextProvider;

	@Mock
	private ServerPushClientService serverPush;

	@Mock
	private ClientMetricsService metrics;

	@Mock
	private EventBus eventBus;

	@Mock
	private ClientMessages clientMessages;

	private long lastSyncedActionId;

	private UUID projectId;

	private InOrder inOrder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		lastSyncedActionId = 1l;
		projectId = new UUID();
		when(alertingService.showInfo(anyString())).thenReturn(mock(AlertRegistration.class));
		syncer = new ActionSyncService(dispatcher, actionExecutionService, storage, alertingService, clientMessages, networkMonitor, contextProvider, serverPush, metrics, eventBus);
		syncer.onProjectChanged(projectId, lastSyncedActionId);
		inOrder = inOrder(actionExecutionService, dispatcher, alertingService);
		inOrder.verify(dispatcher).registerListener(syncer);
		inOrder.verify(actionExecutionService).addActionExecutionListener(syncer);
	}

	@Test
	public void shouldDispatchUserActions() {
		onUserAction(action);
		verifyDispatched(action);
	}

	@Test
	public void shouldSaveDispatchUserActionInLocalStorage() {
		onUserAction(action);
		assertStored(action);
	}

	@Test
	public void shouldRemoveConfirmedActionsFromLocalStorage() {
		onUserAction(action);
		syncer.onActionsAcceptedByServer(asList(action), 3l);
		assertStored();
	}

	@Test
	public void shouldNotDispatchNonUserAction() throws Exception {
		syncer.onActionExecution(action, context, actionContext, executionContext, false);
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldNotDispatchActionsWhenThereIsNoConnection() throws Exception {
		syncer.onConnectionLost();
		for (final ModelAction action : createActionsList(5)) {
			onUserAction(action);
		}
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldStoreActionsWhenThereIsNoConnection() throws Exception {
		syncer.onConnectionLost();
		final List<ModelAction> actions = createActionsList(5);
		for (final ModelAction action : actions) {
			onUserAction(action);
		}
		assertStored(actions);
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldRequestAllActionsThatHappenedSinceLastSuccessfullySentAction() throws Exception {
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId);
	}

	@Test
	public void shouldApplyAllActionsThatHappenedSinceLastSuccessfullySentActionOnRequestSuccess() throws Exception {
		final List<ModelAction> serverSideActionList = createActionsList(5);

		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess(serverSideActionList);
		for (final ModelAction action : serverSideActionList) {
			verifyExecutedLocally(action);
		}
	}

	@Test
	public void shouldUpdateLastSyncedActionIdWhenSuccessfullyAppliedAllActions() throws Exception {
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		final long syncIdFromServer = 5;
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess(syncIdFromServer);
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(syncIdFromServer);
	}

	@Test
	public void shouldShowFatalErrorIfTheServerSideActionRequestFails() throws Exception {
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithFailure();
		verifyFatalErrorShown();
	}

	@Test
	public void shouldDoNothingWhenConnectionWasRecoveredWithoutLoosingItFirst() throws Exception {
		syncer.onConnectionRecovered();
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldBeAbleToDispatchActionsAgainWhenConnectionWasRecovered() throws Exception {
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();
		onUserAction(action);
		verifyDispatched(action);
	}

	@Test
	public void shouldRevertConflictedActions() throws Exception {
		final ModelAction reverseAction = onUserAction(action);
		verifyDispatched(action);
		syncer.onActionsRegectedByServer(asList(action), new UnableToHandleActionException());
		verifyExecutedLocally(reverseAction);
	}

	@Test
	public void shouldShowFatalErrorWhenConflictRevertFails() throws Exception {
		final ModelAction reverseAction = onUserAction(action);
		verifyDispatched(action);
		doFailWhenExecuted(reverseAction);
		syncer.onActionsRegectedByServer(asList(action), new UnableToHandleActionException());
		verifyFatalErrorShown();
	}

	@Test
	public void shouldKeepActionAsPendingActionUntilTheDispatchResponds() throws Exception {
		onUserAction(action);
		verifyDispatched(action);
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();
		verifyDispatched(action);
		syncer.onActionsAcceptedByServer(asList(action), 3l);
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(3l).andReturnWithSuccess();
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldRemoveTheConflictedActionFromPendingActionsListWhenDispatchFails() throws Exception {
		onUserAction(action);
		verifyDispatched(action);
		syncer.onActionsRegectedByServer(asList(action), new UnableToHandleActionException());
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldNotRequestSuccessfullySentActionsInReconnectionResync() throws Exception {
		onUserAction(action);
		verifyDispatched(action);
		syncer.onActionsAcceptedByServer(asList(action), 3l);
		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(3l);
	}

	@Test
	public void shouldRevertClientSidePendingActionsBeforeRetrievingServerSidePendingActionsOnReconnection() throws Exception {
		final List<ModelAction> pendingActions = createActionsList(8);
		final List<ModelAction> reversePendingActions = new ArrayList<ModelAction>();

		syncer.onConnectionLost();
		for (final ModelAction action : pendingActions) {
			reversePendingActions.add(0, onUserAction(action));
		}
		syncer.onConnectionRecovered();

		for (final ModelAction reveseAction : reversePendingActions) {
			verifyExecutedLocally(reveseAction);
		}
		verifyRetrievedAllServerActionsSince(lastSyncedActionId);
	}

	@Test
	public void shouldDispatchClientSidePendingActionsAfterRetrievingServerSidePendingActionsOnReconnection() throws Exception {
		final List<ModelAction> pendingActions = createActionsList(8);

		syncer.onConnectionLost();
		for (final ModelAction action : pendingActions) {
			onUserAction(action);
		}
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();

		for (final ModelAction action : pendingActions) {
			verifyExecutedLocally(action);
			verifyDispatched(action);
		}
	}

	@Test
	public void shouldApplyAllActionsPushedFromServer() throws Exception {
		final List<ModelAction> actionList = createActionsList(12);
		final long serverSyncId = lastSyncedActionId + actionList.size();

		syncer.onEvent(new ModelActionSyncEvent(projectId, actionList, actionContext, serverSyncId));
		for (final ModelAction modelAction : actionList) {
			verifyExecutedLocally(modelAction);
		}

		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(serverSyncId);
	}

	@Test
	public void shouldClearPreviousProjectsPendingActionsWhenProjectChanges() throws Exception {
		final int pendingActionsFromPreviousProject = 4;
		final UUID newProjectId = new UUID();
		final Long newProjectSyncId = 9l;

		syncer.onConnectionLost();
		for (final ModelAction action : createActionsList(pendingActionsFromPreviousProject)) {
			onUserAction(action);
		}
		syncer.onProjectChanged(newProjectId, newProjectSyncId);
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActions(newProjectId, newProjectSyncId).andReturnWithSuccess();
		verifyNoMoreInteractions();
	}

	@Test
	public void whenThereArePendingActionsForNewlyLoadedProjectItShouldBeAppliedAndSentToServer() throws Exception {
		final UUID newProjectId = new UUID();
		final List<ActionSyncEntry> pendingEntriesList = createEntriesList(6);
		when(storage.loadActionSyncEntries()).thenReturn(pendingEntriesList);

		syncer.onProjectChanged(newProjectId, 1l);

		for (final ActionSyncEntry entry : pendingEntriesList) {
			verifyAppliedLocally(entry);
			verifyDispatched(entry.getAction());
		}
	}

	@Test
	public void shouldNotClearPendingActionsWhenLocalPendingActionRevertFails() throws Exception {
		syncer.onConnectionLost();
		final List<ModelAction> reverseActions = new ArrayList<ModelAction>();
		final List<ModelAction> pendingActions = createActionsList(5);
		for (final ModelAction action : pendingActions) {
			reverseActions.add(0, onUserAction(action));
		}

		final ModelAction failingReveseAction = reverseActions.get(3);
		doFailWhenExecuted(failingReveseAction);
		syncer.onConnectionRecovered();
		for (int i = 0; i <= reverseActions.indexOf(failingReveseAction); i++) {
			verifyExecutedLocally(reverseActions.get(i));
		}
		verifyFatalErrorShown();
		verifyNoMoreInteractions();

		final List<ActionSyncEntry> lastEntries = captureLastStoredEntriesList();
		assertEquals(pendingActions.size(), lastEntries.size());
		for (int i = 0; i < pendingActions.size(); i++) {
			final ModelAction expected = pendingActions.get(i);
			final ModelAction actual = lastEntries.get(i).getAction();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void shouldClearPendingActionsListWhenSavedPendingActionResyncFails() throws Exception {
		final List<ActionSyncEntry> pendingEntriesList = createEntriesList(6);
		when(storage.loadActionSyncEntries()).thenReturn(pendingEntriesList);

		final ActionSyncEntry failingEntry = pendingEntriesList.get(2);
		doFailWhenExecuted(failingEntry);
		syncer.onProjectChanged(projectId, lastSyncedActionId);

		for (int i = 0; i < 2; i++) {
			final ActionSyncEntry entry = pendingEntriesList.get(i);
			verifyAppliedLocally(entry);
			verifyDispatched(entry.getAction());
		}
		verifyAppliedLocally(failingEntry);
		verifyErrorShown();
		verifyNoMoreInteractions();

		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();
		verifyNoMoreInteractions();
	}

	private void doFailWhenExecuted(final ActionSyncEntry entry) throws UnableToCompleteActionException {
		doThrow(UnableToCompleteActionException.class).when(actionExecutionService).onNonUserActionRequest(entry.getAction(), entry.getContext());
	}

	private void doFailWhenExecuted(final ModelAction action) throws UnableToCompleteActionException {
		doThrow(UnableToCompleteActionException.class).when(actionExecutionService).onNonUserActionRequest(action, actionContext);
	}

	private List<ActionSyncEntry> createEntriesList(final int size) {
		final List<ActionSyncEntry> entries = new ArrayList<ActionSyncEntry>();
		for (final ModelAction action : createActionsList(size)) {
			entries.add(new ActionSyncEntry(action, new NullAction(), mock(ActionContext.class)));
		}
		return entries;
	}

	private void assertStored(final ModelAction... expectedActions) {
		assertStored(asList(expectedActions));
	}

	private void assertStored(final List<ModelAction> expectedActions) {
		final List<ActionSyncEntry> storedEntries = captureLastStoredEntriesList();
		assertEquals(expectedActions.size(), storedEntries.size());
		for (int i = 0; i < expectedActions.size(); i++) {
			assertEquals(expectedActions.get(i), storedEntries.get(i).getAction());
		}
	}

	private void verifyDispatched(final ModelAction action) {
		inOrder.verify(dispatcher).dispatch(action);
	}

	private void verifyExecutedLocally(final ModelAction action) throws UnableToCompleteActionException {
		inOrder.verify(actionExecutionService).onNonUserActionRequest(action, actionContext);
	}

	private void verifyAppliedLocally(final ActionSyncEntry entry) throws UnableToCompleteActionException {
		inOrder.verify(actionExecutionService).onNonUserActionRequest(entry.getAction(), entry.getContext());
	}

	private void verifyErrorShown() {
		inOrder.verify(alertingService).showError(anyString());
	}

	private void verifyFatalErrorShown() {
		inOrder.verify(alertingService).showErrorWithConfirmation(anyString(), any(AlertConfirmationListener.class));
	}

	private ModelActionSyncEventCallbackTestHelper verifyRetrievedAllServerActionsSince(final long lastSyncedActionId) {
		return verifyRetrievedAllServerActions(projectId, lastSyncedActionId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<ActionSyncEntry> captureLastStoredEntriesList() {
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(storage, atLeastOnce()).storeActionSyncEntries(captor.capture());
		final List<List> savedEntries = captor.getAllValues();
		final List<ActionSyncEntry> lastEntries = savedEntries.get(savedEntries.size() - 1);
		return lastEntries;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ModelActionSyncEventCallbackTestHelper verifyRetrievedAllServerActions(final UUID projectId, final long lastSyncedActionId) {
		final ArgumentCaptor<AsyncCallback> captor = ArgumentCaptor.forClass(AsyncCallback.class);
		inOrder.verify(dispatcher).retrieveAllActionsSince(eq(projectId), eq(lastSyncedActionId), captor.capture());
		return new ModelActionSyncEventCallbackTestHelper(captor.getValue());
	}

	private ModelAction onUserAction(final ModelAction action) {
		final ModelAction reverseAction = mock(ModelAction.class);
		when(executionContext.getReverseAction()).thenReturn(reverseAction);
		syncer.onActionExecution(action, context, actionContext, executionContext, true);
		return reverseAction;
	}

	private void verifyNoMoreInteractions() {
		inOrder.verifyNoMoreInteractions();
	}

	private List<ModelAction> createActionsList(final int size) {
		final List<ModelAction> actions = new ArrayList<ModelAction>();
		for (int i = 0; i < size; i++) {
			actions.add(mock(ModelAction.class));
		}
		return actions;
	}

	private class ModelActionSyncEventCallbackTestHelper {

		private final AsyncCallback<ModelActionSyncEvent> callback;
		private List<ModelAction> actionsList;
		private long lastSyncedActionId;

		public ModelActionSyncEventCallbackTestHelper(final AsyncCallback<ModelActionSyncEvent> callback) {
			this.callback = callback;
			this.actionsList = ActionSyncServiceTest.this.createActionsList(0);
			this.lastSyncedActionId = ActionSyncServiceTest.this.lastSyncedActionId;
		}

		public ModelActionSyncEventCallbackTestHelper setActionsList(final List<ModelAction> actionsList) {
			this.actionsList = actionsList;
			return this;
		}

		public ModelActionSyncEventCallbackTestHelper setLastSyncedActionId(final long lastSyncedActionId) {
			this.lastSyncedActionId = lastSyncedActionId;
			return this;
		}

		public void andReturnWithSuccess(final List<ModelAction> actions) {
			setActionsList(actions).setLastSyncedActionId(lastSyncedActionId + actions.size()).andReturnWithSuccess();
		}

		public void andReturnWithSuccess(final long syncId) {
			setLastSyncedActionId(syncId).andReturnWithSuccess();
		}

		public void andReturnWithSuccess() {
			callback.onSuccess(new ModelActionSyncEvent(projectId, actionsList, actionContext, lastSyncedActionId));
		}

		public void andReturnWithFailure() {
			callback.onFailure(new RuntimeException());
		}
	}

}
