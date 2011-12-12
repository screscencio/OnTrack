package br.com.oncast.ontrack.server.services.notification;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertNotContains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.utils.mocks.requests.RequestTestUtils;

public class NotificationServiceTest {

	@Mock
	private ClientManager clientManager;

	@Mock
	private ServerPushServerService serverPushServerService;

	@Mock
	private SessionManager sessionManager;

	private NotificationServiceImpl service;

	private UUID client1;

	private UUID client2;

	private Set<UUID> clientsToBeReturnedByTheManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		service = new NotificationServiceImpl(serverPushServerService, clientManager, sessionManager);

		clientsToBeReturnedByTheManager = new HashSet<UUID>();

		client1 = new UUID("1");
		client2 = new UUID("2");

		when(clientManager.getClientsAtProject(Mockito.anyLong())).thenReturn(clientsToBeReturnedByTheManager);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void actionNotificationShouldNotBeSentToTheClientThatOriginatedTheRequest() throws Exception {
		final UUID originator = new UUID();

		clientsToBeReturnedByTheManager.add(originator);
		clientsToBeReturnedByTheManager.add(client1);
		clientsToBeReturnedByTheManager.add(client2);

		final Session sessionMock = Mockito.mock(Session.class);
		when(sessionManager.getCurrentSession()).thenReturn(sessionMock);
		when(sessionMock.getThreadLocalClientId()).thenReturn(originator);

		service.notifyActions(RequestTestUtils.createModelActionSyncRequest());

		final HashSet<UUID> expectedClients = new HashSet<UUID>();
		expectedClients.add(client1);
		expectedClients.add(client2);

		final ArgumentCaptor<Set> argument = ArgumentCaptor.forClass(Set.class);
		verify(serverPushServerService).pushEvent(Mockito.any(ServerPushEvent.class), argument.capture());

		assertCollectionEquality(expectedClients, argument.getValue());
		assertNotContains(originator, argument.getValue());
	}
}
