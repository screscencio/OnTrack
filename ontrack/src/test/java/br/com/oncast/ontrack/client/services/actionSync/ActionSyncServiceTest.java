package br.com.oncast.ontrack.client.services.actionSync;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.requests.RequestTestUtils;

public class ActionSyncServiceTest {

	@Mock
	private DispatchService requestDispatch;
	@Mock
	private ServerPushClientService serverPush;
	@Mock
	private ActionExecutionService actionExecution;
	@Mock
	private ClientIdentificationProvider clientIdentificationProvider;
	@Mock
	private ProjectRepresentationProvider projectRepresentationProvider;
	@Mock
	private ErrorTreatmentService errorTreatmentService;

	private ActionSyncService ASS;

	private ModelActionSyncRequest request;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void anActionOriginatedInClientShouldNotBeExecutedEvenIfReceivedFromServer() throws Exception {
		final String clientId = "123";
		given().aClientWithId(clientId);
		when().aRequestArriveFrom(clientId);
		verifyThat().noActionWasExecutedInClient();
	}

	@Test
	public void anActionOriginatedInClientShouldBeExecutedOnce() throws Exception {
		final int nTimes = 1;
		when().anActionWasExecutedInClient();
		verifyThat().userActionsWereExecutedInClient(nTimes);
	}

	@Test
	public void actionsNotOriginatedInClientShouldBeExecutedAfterBeingReceivedFromServer() throws Exception {
		final short projectId = 1;
		given().aClientWithId("client being tested").ignoringProjectCheck(projectId);
		when().aRequestArriveFrom("other client");
		verifyThat().nonUserActionsWereExecutedInClient();
	}

	@Test
	public void anActionNotOriginatedInClientShouldNotBeExecutedAsClientActionAfterBeingReceivedFromServer() throws Exception {
		final short projectId = 1;
		given().aClientWithId("client being tested").ignoringProjectCheck(projectId);
		when().aRequestArriveFrom("other client");
		verifyThat().nonUserActionsWereExecutedInClient();
	}

	@Test
	public void anActionReceivedFromServerThatBelongsToAProjectDifferentFromClientCurrentProjectShouldNotBeExecuted() throws Exception {
		final int currentProject = 1;
		final int otherProject = 2;
		given().aClientWithId("client").aClientWithCurrentProject(currentProject);
		when().aRequestArrivedFrom("other client", otherProject);
		verifyThat().noActionWasExecutedInClient();
	}

	@Test
	public void anActionReceivedFromServerThatBelongsToTheSameProjectOfClientCurrentProjectShouldBeExecuted() throws Exception {
		final short project = 1;
		given().aClientWithId("client").aClientWithCurrentProject(project);
		when().aRequestArrivedFrom("other client", project);
		verifyThat().nonUserActionsWereExecutedInClient();
	}

	private Given given() {
		return new Given();
	}

	private When when() {
		return new When();
	}

	private VerifyThat verifyThat() {
		return new VerifyThat();
	}

	private class Given {
		private Given aClientWithId(final String clientId) {
			Mockito.when(clientIdentificationProvider.getClientId()).thenReturn(new UUID(clientId));
			return this;
		}

		private Given aClientWithCurrentProject(final int projectId) {
			Mockito.when(projectRepresentationProvider.getCurrentProjectRepresentation()).thenReturn(ProjectTestUtils.createRepresentation(projectId));
			return this;
		}

		private Given ignoringProjectCheck(final int projectId) {
			Mockito.when(projectRepresentationProvider.getCurrentProjectRepresentation()).thenReturn(ProjectTestUtils.createRepresentation(projectId));
			return this;
		}
	}

	private class When {
		private void aRequestArriveFrom(final String clientId) {
			request = RequestTestUtils.createModelActionSyncRequest(new UUID(clientId));
			fireEvent();
		}

		private void aRequestArrivedFrom(final String clientId, final int projectId) {
			request = RequestTestUtils.createModelActionSyncRequest(new UUID(clientId), projectId);
			fireEvent();
		}

		private void anActionWasExecutedInClient() {
			createInstance();
			final ModelAction action = mock(ModelAction.class);
			actionExecution.onUserActionExecutionRequest(action);
		}

		private void fireEvent() {
			final ArgumentCaptor<ServerActionSyncEventHandler> eventHandlerCaptor = getEventHandlerCaptor();
			createInstance();

			try {
				fireActionSynEvent(request, eventHandlerCaptor);
			}
			catch (final RuntimeException e) {}
		}

		private void createInstance() {
			ASS = new ActionSyncService(requestDispatch, serverPush, actionExecution, clientIdentificationProvider,
					projectRepresentationProvider, errorTreatmentService);
		}

		private ArgumentCaptor<ServerActionSyncEventHandler> getEventHandlerCaptor() {
			final ArgumentCaptor<ServerActionSyncEventHandler> eventHandlerCaptor = ArgumentCaptor.forClass(ServerActionSyncEventHandler.class);
			doNothing().when(serverPush).registerServerEventHandler(eq(ServerActionSyncEvent.class), eventHandlerCaptor.capture());
			return eventHandlerCaptor;
		}

		private void fireActionSynEvent(final ModelActionSyncRequest request, final ArgumentCaptor<ServerActionSyncEventHandler> eventHandlerCaptor) {
			final ServerActionSyncEventHandler eventHandler = eventHandlerCaptor.getValue();
			eventHandler.onEvent(new ServerActionSyncEvent(request));
		}
	}

	private class VerifyThat {
		private void nonUserActionsWereExecutedInClient() throws Exception {
			final int nTimes = request.getActionList().size();
			verify(actionExecution, never()).onUserActionExecutionRequest(any(ModelAction.class));
			verify(actionExecution, times(nTimes)).onNonUserActionRequest(any(ModelAction.class));
			verify(actionExecution, never()).onUserActionRedoRequest();
			verify(actionExecution, never()).onUserActionUndoRequest();
		}

		private void noActionWasExecutedInClient() throws Exception {
			verify(actionExecution, never()).onUserActionExecutionRequest(any(ModelAction.class));
			verify(actionExecution, never()).onNonUserActionRequest(any(ModelAction.class));
			verify(actionExecution, never()).onUserActionRedoRequest();
			verify(actionExecution, never()).onUserActionUndoRequest();
		}

		private void userActionsWereExecutedInClient(final int nTimes) throws Exception {
			verify(actionExecution, never()).onNonUserActionRequest(any(ModelAction.class));
			verify(actionExecution, times(nTimes)).onUserActionExecutionRequest(any(ModelAction.class));
			verify(actionExecution, never()).onUserActionRedoRequest();
			verify(actionExecution, never()).onUserActionUndoRequest();
		}
	}
}
