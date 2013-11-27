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
import br.com.oncast.ontrack.shared.model.action.NullAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

public class ActionSyncServiceTest {

	private ActionSyncService syncer;

	@Mock
	private UserAction action;

	@Mock
	private ProjectContext context;

	@Mock
	private QueuedActionsDispatcher dispatcher;

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
		doAnswer(new Answer<Void>() {
			@Override
			@SuppressWarnings({ "unchecked" })
			public Void answer(final InvocationOnMock invocation) throws Throwable {
				final UUID projectId = (UUID) invocation.getArguments()[0];
				final List<ActionExecutionContext> list = (List<ActionExecutionContext>) invocation.getArguments()[1];
				when(storage.loadPendingActionExecutionContexts(projectId)).thenReturn(list);
				return null;
			}
		}).when(storage).storePendingActionExecutionContexts(any(UUID.class), anyListOf(ActionExecutionContext.class));
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
		syncer.onActionExecution(mock(ActionExecutionContext.class), context, false);
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldNotDispatchActionsWhenThereIsNoConnection() throws Exception {
		syncer.onConnectionLost();
		for (final UserAction action : createActionsList(5)) {
			onUserAction(action);
		}
		verifyNoMoreInteractions();
	}

	@Test
	public void shouldStoreActionsWhenThereIsNoConnection() throws Exception {
		syncer.onConnectionLost();
		final List<UserAction> actions = createActionsList(5);
		for (final UserAction action : actions) {
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
		final List<UserAction> serverSideActionList = createActionsList(5);

		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess(serverSideActionList);
		for (final UserAction action : serverSideActionList) {
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
		final UserAction reverseAction = onUserAction(action);
		verifyDispatched(action);
		syncer.onActionsRegectedByServer(asList(action), new UnableToHandleActionException());
		verifyExecutedLocally(reverseAction);
	}

	@Test
	public void shouldShowFatalErrorWhenConflictRevertFails() throws Exception {
		final UserAction reverseAction = onUserAction(action);
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
		final List<UserAction> pendingActions = createActionsList(8);
		final List<UserAction> reversePendingActions = new ArrayList<UserAction>();

		syncer.onConnectionLost();
		for (final UserAction action : pendingActions) {
			reversePendingActions.add(0, onUserAction(action));
		}
		syncer.onConnectionRecovered();

		for (final UserAction reveseAction : reversePendingActions) {
			verifyExecutedLocally(reveseAction);
		}
		verifyRetrievedAllServerActionsSince(lastSyncedActionId);
	}

	@Test
	public void shouldDispatchClientSidePendingActionsAfterRetrievingServerSidePendingActionsOnReconnection() throws Exception {
		final List<UserAction> pendingActions = createActionsList(8);

		syncer.onConnectionLost();
		for (final UserAction action : pendingActions) {
			onUserAction(action);
		}
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();

		for (final UserAction action : pendingActions) {
			verifyExecutedLocally(action);
			verifyDispatched(action);
		}
	}

	@Test
	public void shouldApplyAllActionsPushedFromServer() throws Exception {
		final List<UserAction> actionList = createActionsList(12);
		final long serverSyncId = lastSyncedActionId + actionList.size();

		syncer.onEvent(modelActionSyncEvent(projectId, actionList, serverSyncId));
		for (final UserAction modelAction : actionList) {
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
		for (final UserAction action : createActionsList(pendingActionsFromPreviousProject)) {
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
		final List<ActionExecutionContext> pendingEntriesList = createEntriesList(6);
		when(storage.loadPendingActionExecutionContexts(newProjectId)).thenReturn(pendingEntriesList);

		syncer.onProjectChanged(newProjectId, 1l);

		for (final ActionExecutionContext entry : pendingEntriesList) {
			verifyAppliedLocally(entry);
			verifyDispatched(entry.getUserAction());
		}
	}

	@Test
	public void shouldNotClearPendingActionsWhenLocalPendingActionRevertFails() throws Exception {
		syncer.onConnectionLost();
		final List<UserAction> reverseActions = new ArrayList<UserAction>();
		final List<UserAction> pendingActions = createActionsList(5);
		for (final UserAction action : pendingActions) {
			reverseActions.add(0, onUserAction(action));
		}

		final UserAction failingReveseAction = reverseActions.get(3);
		doFailWhenExecuted(failingReveseAction);
		syncer.onConnectionRecovered();
		for (int i = 0; i <= reverseActions.indexOf(failingReveseAction); i++) {
			verifyExecutedLocally(reverseActions.get(i));
		}
		verifyFatalErrorShown();
		verifyNoMoreInteractions();

		final List<ActionExecutionContext> lastEntries = captureLastStoredEntriesList();
		assertEquals(pendingActions.size(), lastEntries.size());
		for (int i = 0; i < pendingActions.size(); i++) {
			final UserAction expected = pendingActions.get(i);
			final UserAction actual = lastEntries.get(i).getUserAction();
			assertEquals(expected, actual);
		}
	}

	@Test
	public void shouldClearPendingActionsListWhenSavedPendingActionResyncFails() throws Exception {
		final List<ActionExecutionContext> pendingEntriesList = createEntriesList(6);
		when(storage.loadPendingActionExecutionContexts(projectId)).thenReturn(pendingEntriesList);

		final ActionExecutionContext failingEntry = pendingEntriesList.get(2);
		doFailWhenExecuted(failingEntry);
		syncer.onProjectChanged(projectId, lastSyncedActionId);

		for (int i = 0; i < 2; i++) {
			final ActionExecutionContext entry = pendingEntriesList.get(i);
			verifyAppliedLocally(entry);
			verifyDispatched(entry.getUserAction());
		}
		verifyAppliedLocally(failingEntry);
		verifyErrorShown();
		verifyNoMoreInteractions();

		syncer.onConnectionLost();
		syncer.onConnectionRecovered();
		verifyRetrievedAllServerActionsSince(lastSyncedActionId).andReturnWithSuccess();
		verifyNoMoreInteractions();
	}

	private void doFailWhenExecuted(final ActionExecutionContext entry) throws UnableToCompleteActionException {
		doThrow(UnableToCompleteActionException.class).when(actionExecutionService).onNonUserActionRequest(entry.getUserAction());
	}

	private void doFailWhenExecuted(final UserAction action) throws UnableToCompleteActionException {
		doThrow(UnableToCompleteActionException.class).when(actionExecutionService).onNonUserActionRequest(action);
	}

	private List<ActionExecutionContext> createEntriesList(final int size) {
		final List<ActionExecutionContext> entries = new ArrayList<ActionExecutionContext>();
		for (final UserAction action : createActionsList(size)) {
			entries.add(new ActionExecutionContext(action, new NullAction()));
		}
		return entries;
	}

	private void assertStored(final UserAction... expectedActions) {
		assertStored(asList(expectedActions));
	}

	private void assertStored(final List<UserAction> expectedActions) {
		final List<ActionExecutionContext> storedEntries = captureLastStoredEntriesList();
		assertEquals(expectedActions.size(), storedEntries.size());
		for (int i = 0; i < expectedActions.size(); i++) {
			assertEquals(expectedActions.get(i), storedEntries.get(i).getUserAction());
		}
	}

	private void verifyDispatched(final UserAction action) {
		inOrder.verify(dispatcher).dispatch(action);
	}

	private void verifyExecutedLocally(final UserAction action) throws UnableToCompleteActionException {
		inOrder.verify(actionExecutionService).onNonUserActionRequest(action);
	}

	private void verifyAppliedLocally(final ActionExecutionContext entry) throws UnableToCompleteActionException {
		inOrder.verify(actionExecutionService).onNonUserActionRequest(entry.getUserAction());
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
	private List<ActionExecutionContext> captureLastStoredEntriesList() {
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(storage, atLeastOnce()).storePendingActionExecutionContexts(eq(projectId), captor.capture());
		return captor.getValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ModelActionSyncEventCallbackTestHelper verifyRetrievedAllServerActions(final UUID projectId, final long lastSyncedActionId) {
		final ArgumentCaptor<AsyncCallback> captor = ArgumentCaptor.forClass(AsyncCallback.class);
		inOrder.verify(dispatcher).retrieveAllActionsSince(eq(projectId), eq(lastSyncedActionId), captor.capture());
		return new ModelActionSyncEventCallbackTestHelper(captor.getValue());
	}

	private ModelActionSyncEvent modelActionSyncEvent(final UUID projectId, final List<UserAction> actionList, final long serverSyncId) {
		final ModelActionSyncEvent event = mock(ModelActionSyncEvent.class);
		when(event.getActionList()).thenReturn(actionList);
		when(event.getLastActionId()).thenReturn(serverSyncId);
		when(event.getProjectId()).thenReturn(projectId);
		return event;
	}

	private UserAction onUserAction(final UserAction action) {
		final UserAction reverseAction = mock(UserAction.class);
		final ActionExecutionContext executionContext = mock(ActionExecutionContext.class);
		when(executionContext.getReverseUserAction()).thenReturn(reverseAction);
		when(executionContext.getUserAction()).thenReturn(action);
		syncer.onActionExecution(executionContext, context, true);
		return reverseAction;
	}

	private void verifyNoMoreInteractions() {
		inOrder.verifyNoMoreInteractions();
	}

	private List<UserAction> createActionsList(final int size) {
		final List<UserAction> actions = new ArrayList<UserAction>();
		for (int i = 0; i < size; i++) {
			actions.add(mock(UserAction.class));
		}
		return actions;
	}

	private class ModelActionSyncEventCallbackTestHelper {

		private final AsyncCallback<ModelActionSyncEvent> callback;
		private List<UserAction> actionsList;
		private long lastSyncedActionId;

		public ModelActionSyncEventCallbackTestHelper(final AsyncCallback<ModelActionSyncEvent> callback) {
			this.callback = callback;
			this.actionsList = ActionSyncServiceTest.this.createActionsList(0);
			this.lastSyncedActionId = ActionSyncServiceTest.this.lastSyncedActionId;
		}

		public ModelActionSyncEventCallbackTestHelper setActionsList(final List<UserAction> actionsList) {
			this.actionsList = actionsList;
			return this;
		}

		public ModelActionSyncEventCallbackTestHelper setLastSyncedActionId(final long lastSyncedActionId) {
			this.lastSyncedActionId = lastSyncedActionId;
			return this;
		}

		public void andReturnWithSuccess(final List<UserAction> actions) {
			setActionsList(actions).setLastSyncedActionId(lastSyncedActionId + actions.size()).andReturnWithSuccess();
		}

		public void andReturnWithSuccess(final long syncId) {
			setLastSyncedActionId(syncId).andReturnWithSuccess();
		}

		public void andReturnWithSuccess() {
			callback.onSuccess(modelActionSyncEvent(projectId, actionsList, lastSyncedActionId));
		}

		public void andReturnWithFailure() {
			callback.onFailure(new RuntimeException());
		}
	}

}
