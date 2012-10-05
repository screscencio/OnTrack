package br.com.oncast.ontrack.server.services.multicast;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertContainsNone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager.UserStatusChangeListener;
import br.com.oncast.ontrack.server.services.serverPush.CometClientConnection;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ClientManagerTest {

	private static final String DEFAULT_SESSION_ID = "sessionId";

	private ClientManager manager;

	private ServerPushConnection client1;
	private ServerPushConnection client2;
	private ServerPushConnection client3;
	private ServerPushConnection clientWithDifferentSession;

	private UUID project1;
	private UUID project2;

	@Mock
	private AuthenticationManager authenticationManager;

	private AuthenticationListener authenticationListener;

	@Mock
	private UserStatusChangeListener userStatusChangeListener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		final ArgumentCaptor<AuthenticationListener> captor = ArgumentCaptor.forClass(AuthenticationListener.class);
		doNothing().when(authenticationManager).register(captor.capture());
		manager = new ClientManager(authenticationManager);
		authenticationListener = captor.getValue();

		client1 = new CometClientConnection("1", DEFAULT_SESSION_ID);
		client2 = new CometClientConnection("2", DEFAULT_SESSION_ID);
		client3 = new CometClientConnection("3", DEFAULT_SESSION_ID);
		clientWithDifferentSession = new CometClientConnection("4", "othersession");

		project1 = new UUID();
		project2 = new UUID();

		manager.addUserStatusChangeListener(userStatusChangeListener);
	}

	@Test
	public void shouldNotitfyBothUserOfflineAndUserCLosedProjectWhenTheUserLoggsOut() throws Exception {
		final User user = UserTestUtils.createUser();

		registerClients(client1);
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		bindClients(project1, client1);

		authenticationListener.onUserLoggedOut(user, client1.getSessionId());

		verify(userStatusChangeListener).onUserOffline(user.getEmail());
		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());

	}

	@Test
	public void shouldNotifyBothUserOfflineAndUserCLosedProjectWhenTheLastRegisteredClientCloses() throws Exception {
		final User user = UserTestUtils.createUser();

		registerClients(client1);
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		bindClients(project1, client1);

		unregisterClients(client1);
		verify(userStatusChangeListener).onUserOffline(user.getEmail());
		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());

	}

	@Test
	public void shouldNotifyUserOnlineWhenAUserLoggsIn() throws Exception {
		final User user = UserTestUtils.createUser();

		registerClients(client1);
		verify(userStatusChangeListener, never()).onUserOnline(user.getEmail());

		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		verify(userStatusChangeListener).onUserOnline(user.getEmail());
	}

	@Test
	public void shouldNotifyUserOnlineWhenALoggedUserRegisters() throws Exception {
		final User user = UserTestUtils.createUser();

		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		verify(userStatusChangeListener, never()).onUserOnline(user.getEmail());

		registerClients(client1);
		verify(userStatusChangeListener).onUserOnline(user.getEmail());
	}

	@Test
	public void shouldNotifyUserOfflineWhenALoggedUserUnregisters() throws Exception {
		final User user = UserTestUtils.createUser();

		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		registerClients(client1);
		verify(userStatusChangeListener).onUserOnline(user.getEmail());

		unregisterClients(client1);
		verify(userStatusChangeListener).onUserOffline(user.getEmail());
	}

	@Test
	public void shouldNotNotifyUserOfflineWhenANotLoggedUserUnregisters() throws Exception {
		registerClients(client1);
		unregisterClients(client1);

		verifyZeroInteractions(userStatusChangeListener);
	}

	@Test
	public void shouldNotNotifyUserOfflineWhenAnUnregisteredUserLoggsOut() throws Exception {
		final User user = UserTestUtils.createUser();

		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		authenticationListener.onUserLoggedOut(user, client1.getSessionId());

		verifyZeroInteractions(userStatusChangeListener);
	}

	@Test
	public void shouldNotifyUserOfflineWhenALoggedAndRegisteredUserLoggsOut() throws Exception {
		final User user = UserTestUtils.createUser();

		registerClients(client1);
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		authenticationListener.onUserLoggedOut(user, client1.getSessionId());
		verify(userStatusChangeListener).onUserOffline(user.getEmail());
	}

	@Test
	public void shouldNotifyUserCloseProjectWhenTheClientIsUnregistered() throws Exception {
		registerClients(client1);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		bindClients(project1, client1);
		unregisterClients(client1);

		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());
	}

	@Test
	public void shouldNotifyUserCloseProjectWhenTheUserIsBoundToAnotherProjectWithoutUnbindingToThePreviousProject() throws Exception {
		registerClients(client1);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		bindClients(project1, client1);
		verify(userStatusChangeListener).onUserOpenProject(project1, user.getEmail());
		bindClients(project2, client1);
		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());
		verify(userStatusChangeListener).onUserOpenProject(project2, user.getEmail());
	}

	@Test
	public void shouldNotifyWhenAUserOpenAProject() throws Exception {
		registerClients(client1, client2, client3);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		verify(userStatusChangeListener, never()).onUserOpenProject(any(UUID.class), anyString());

		bindClients(project1, client1);
		verify(userStatusChangeListener).onUserOpenProject(project1, user.getEmail());
	}

	@Test
	public void shouldNotifyWhenAUserOpenAProjectEvenWhenTheUserIsAlreadyLoggedIn() throws Exception {
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		registerClients(client1, client2, client3);
		verify(userStatusChangeListener, never()).onUserOpenProject(any(UUID.class), anyString());

		bindClients(project1, client1);
		verify(userStatusChangeListener).onUserOpenProject(project1, user.getEmail());
	}

	@Test
	public void shouldNotifyWhenAUserClosesAProject() throws Exception {
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		registerClients(client1, client2, client3);
		bindClients(project1, client1);

		unbindClients(client1);
		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());
	}

	@Test
	public void shouldNotifyWhenAUserClosesAProjectEvenWhenThereIsOtherClientsBoundToTheProject() throws Exception {
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		registerClients(client1, client2, client3);
		bindClients(project1, client1, client3);

		unbindClients(client1);
		verify(userStatusChangeListener).onUserCloseProject(project1, user.getEmail());
	}

	@Test
	public void notLoggedUsersArentOnlineUsers() throws Exception {
		registerClients(client1, client2, client3);

		assertTrue(manager.getOnlineUsers().isEmpty());
	}

	@Test
	public void loggedUsersAreOnlineUsers() throws Exception {
		registerClients(client1, client2, client3);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));
	}

	@Test
	public void loggedOutUsersArentOnlineUsers() throws Exception {
		registerClients(client1, client2, client3);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));

		authenticationListener.onUserLoggedOut(user, client1.getSessionId());

		assertTrue(manager.getOnlineUsers().isEmpty());
	}

	@Test
	public void untilAnUserHaveAnyActiveSessionsHeIsOnline() throws Exception {
		registerClients(client1, client2, clientWithDifferentSession);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());
		authenticationListener.onUserLoggedIn(user, clientWithDifferentSession.getSessionId());

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));

		authenticationListener.onUserLoggedOut(user, clientWithDifferentSession.getSessionId());

		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));
		assertEquals(1, manager.getOnlineUsers().size());
	}

	@Test
	public void whenAllRegisteredClientsOfAnUserUnregistersTheUserIsNotOnline() throws Exception {
		registerClients(client1, client2);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));

		unregisterClients(client1);

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));

		unregisterClients(client2);

		assertTrue(manager.getOnlineUsers().isEmpty());
	}

	@Test
	public void whenAClientRegistersAndThereIsAnActiveSessionHeIsAutomaticalyOnline() throws Exception {
		registerClients(clientWithDifferentSession);
		final User user = UserTestUtils.createUser();
		authenticationListener.onUserLoggedIn(user, client1.getSessionId());

		assertTrue(manager.getOnlineUsers().isEmpty());

		registerClients(client1);

		assertEquals(1, manager.getOnlineUsers().size());
		assertTrue(manager.getOnlineUsers().contains(user.getEmail()));
	}

	@Test
	public void shouldBeAbleToKnowAllOnlineUsersBoundToAProject() throws Exception {
		final User user1 = UserTestUtils.createUser();
		final User user2 = UserTestUtils.createUser();

		registerClients(client1, clientWithDifferentSession);
		authenticationListener.onUserLoggedIn(user1, client1.getSessionId());
		authenticationListener.onUserLoggedIn(user2, clientWithDifferentSession.getSessionId());

		bindClients(project1, client1);
		bindClients(project2, clientWithDifferentSession);

		assertEquals(1, manager.getUsersAtProject(project1).size());
		assertTrue(manager.getUsersAtProject(project1).contains(user1.getEmail()));
	}

	@Test
	public void shouldReturnOneUserInProjectEvenWhenThereIsMultipleClients() throws Exception {
		final User user1 = UserTestUtils.createUser();

		registerClients(client1, client2, client3);
		authenticationListener.onUserLoggedIn(user1, client1.getSessionId());
		bindClients(project1, client1, client3);

		assertEquals(1, manager.getUsersAtProject(project1).size());
		assertTrue(manager.getUsersAtProject(project1).contains(user1.getEmail()));
	}

	@Test
	public void twoClientsWithSameUserInTwoDifferentProjects() throws Exception {
		final User user1 = UserTestUtils.createUser();

		registerClients(client1, client2, client3);
		authenticationListener.onUserLoggedIn(user1, client1.getSessionId());
		bindClients(project1, client1, client3);
		bindClients(project2, client2);

		assertEquals(1, manager.getUsersAtProject(project1).size());
		assertTrue(manager.getUsersAtProject(project1).contains(user1.getEmail()));

		assertEquals(1, manager.getUsersAtProject(project2).size());
		assertTrue(manager.getUsersAtProject(project2).contains(user1.getEmail()));
	}

	@Test
	public void thereAreNoClientsWhenThereIsNoRegisteredOrBoundClient() throws Exception {
		assertTrue(manager.getAllClients().isEmpty());
		assertTrue(manager.getClientsAtProject(project1).isEmpty());
		assertTrue(manager.getClientsOfUser("1").isEmpty());
	}

	@Test
	public void anEmptySetShouldBeReturnedWhenThereIsNoBoundClientToTheGivenProject() throws Exception {
		registerClients(client1, client2, client3);

		assertFalse(manager.getAllClients().isEmpty());
		assertTrue(manager.getClientsAtProject(project1).isEmpty());
		assertTrue(manager.getClientsAtProject(project2).isEmpty());
	}

	@Test
	public void notLoggedUserHasNoClients() throws Exception {
		registerClients(client1, client2, client3);

		assertFalse(manager.getAllClients().isEmpty());
		assertTrue(manager.getClientsOfUser("1").isEmpty());
		assertTrue(manager.getClientsOfUser("2").isEmpty());
	}

	@Test
	public void shouldBeAbleToGetAllClients() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, clientWithDifferentSession);

		assertCollectionEquality(asSet(client1, client2, clientWithDifferentSession), manager.getAllClients());
	}

	@Test
	public void registeredClientsShouldBeListedEvenAfterUnbound() throws Exception {
		registerClients(client1, client2);
		registerAndBindClients(project1, client2, client3);

		assertCollectionEquality(asSet(client1, client2, client3), manager.getAllClients());
	}

	@Test
	public void shouldNotListClientsAfterItWasUnregistered() throws Exception {
		registerClients(client1, client2, client3);
		unregisterClients(client2);

		assertCollectionEquality(asSet(client1, client3), manager.getAllClients());
	}

	@Test
	public void unregisteredClientsShouldBeUnboundFromPreviousProject() throws Exception {
		registerAndBindClients(project1, client1, client2, client3);
		unregisterClients(client2);

		assertCollectionEquality(asSet(client1, client3), manager.getClientsAtProject(project1));
	}

	@Test
	public void shouldBeAbleToBindAClientByHisIdWhenHeIsAlreadyRegistered() {
		registerClients(client1, client2, client3);
		bindClients(project2, client1, client2);

		assertCollectionEquality(asSet(client1, client2), manager.getClientsAtProject(project2));
	}

	@Test
	public void shouldBeAbleToBindAClientThatWasNotRegisteredYet() {
		registerClients(client1, client2, client3);
		bindClients(project2, client3, clientWithDifferentSession);

		assertCollectionEquality(asSet(client3, clientWithDifferentSession), manager.getClientsAtProject(project2));
	}

	@Test
	public void shouldBeAbleToBindAClientToAProject() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client3, clientWithDifferentSession);

		final Set<ServerPushConnection> obtainedClientsForProject1 = manager.getClientsAtProject(project1);
		final Set<ServerPushConnection> obtainedClientsForProject2 = manager.getClientsAtProject(project2);

		final Set<ServerPushConnection> expectedClientsForProject1 = asSet(client1, client2);
		final Set<ServerPushConnection> expectedClientsForProject2 = asSet(client3, clientWithDifferentSession);

		assertCollectionEquality(expectedClientsForProject1, obtainedClientsForProject1);
		assertContainsNone(expectedClientsForProject2, obtainedClientsForProject1);

		assertCollectionEquality(expectedClientsForProject2, obtainedClientsForProject2);
		assertContainsNone(expectedClientsForProject1, obtainedClientsForProject2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBeAbleToBindAClientToAProjectWithIdZero() throws Exception {
		registerAndBindClients(UUID.INVALID_UUID, client1, client2);
	}

	@Test
	public void shouldBeAbleToUnboundAClientFromAnyProject() throws Exception {
		registerAndBindClients(project1, client1, client2, client3);
		unbindClients(client2);

		assertCollectionEquality(asSet(client1, client3), manager.getClientsAtProject(project1));
	}

	@Test
	public void theClientShouldBeUnboundFromPreviousProjectWhenBoundToAnother() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client2, client3);

		assertCollectionEquality(asSet(client1), manager.getClientsAtProject(project1));
		assertContainsNone(asSet(client2, client3, clientWithDifferentSession), manager.getClientsAtProject(project1));
	}

	@Test
	public void duplicatedClientsAreNotAllowed() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project1, client2, client3);

		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsAtProject(project1));
	}

	@Test
	public void aUserIsAssociatedWithSessionOnLogin() throws Exception {
		final String sessionId = "sessionId";
		final String userEmail = "1";

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		manager.registerClient(clientWithDifferentSession); // TODO other session
		assertEquals(0, manager.getClientsOfUser(userEmail).size());

		authenticationListener.onUserLoggedIn(UserTestUtils.createUser(userEmail), sessionId);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(userEmail));
	}

	@Test
	public void aUserIsDisassociatedFromSessionOnLogout() throws Exception {
		final String sessionId = "sessionId";
		final User user = UserTestUtils.createUser(1);

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		manager.registerClient(clientWithDifferentSession); // TODO other session
		assertEquals(0, manager.getClientsOfUser(user.getEmail()).size());

		authenticationListener.onUserLoggedIn(user, sessionId);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getEmail()));

		authenticationListener.onUserLoggedOut(user, sessionId);
		assertEquals(0, manager.getClientsOfUser(user.getEmail()).size());
	}

	@Test
	public void clientsOfUserShouldConsiderMultipleSessions() throws Exception {
		final String session1 = "session1";
		final String session2 = "session2";
		final User user = UserTestUtils.createUser(1);

		final CometClientConnection client1 = new CometClientConnection("1", session1);
		final CometClientConnection client2 = new CometClientConnection("2", session1);
		final CometClientConnection client3 = new CometClientConnection("1", session2);
		final CometClientConnection client4 = new CometClientConnection("1", "other session");

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		manager.registerClient(client4);
		assertEquals(0, manager.getClientsOfUser(user.getEmail()).size());

		authenticationListener.onUserLoggedIn(user, session1);
		assertCollectionEquality(asSet(client1, client2), manager.getClientsOfUser(user.getEmail()));

		authenticationListener.onUserLoggedIn(user, session2);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getEmail()));
	}

	@Test
	public void userLogoutAffectsJustItsSession() throws Exception {
		final String session1 = "session1";
		final String session2 = "session2";
		final User user = UserTestUtils.createUser(1);

		final CometClientConnection client1 = new CometClientConnection("1", session1);
		final CometClientConnection client2 = new CometClientConnection("2", session1);
		final CometClientConnection client3 = new CometClientConnection("1", session2);

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		assertEquals(0, manager.getClientsOfUser(user.getEmail()).size());

		authenticationListener.onUserLoggedIn(user, session1);
		authenticationListener.onUserLoggedIn(user, session2);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getEmail()));

		authenticationListener.onUserLoggedOut(user, session1);
		assertCollectionEquality(asSet(client3), manager.getClientsOfUser(user.getEmail()));
	}

	private void bindClients(final UUID projectId, final ServerPushConnection... clientIds) {
		for (final ServerPushConnection clientId : clientIds) {
			manager.bindClientToProject(clientId, projectId);
		}
	}

	private void registerAndBindClients(final UUID projectId, final ServerPushConnection... clientIds) {
		for (final ServerPushConnection clientId : clientIds) {
			manager.registerClient(clientId);
			manager.bindClientToProject(clientId, projectId);
		}
	}

	private void unbindClients(final ServerPushConnection... clientIds) {
		for (final ServerPushConnection clientId : clientIds) {
			manager.unbindClientFromProject(clientId);
		}
	}

	private void registerClients(final ServerPushConnection... clientIds) {
		for (final ServerPushConnection clientId : clientIds) {
			manager.registerClient(clientId);
		}
	}

	private void unregisterClients(final ServerPushConnection... clientIds) {
		for (final ServerPushConnection clientId : clientIds) {
			manager.unregisterClient(clientId);
		}
	}

	private Set<ServerPushConnection> asSet(final ServerPushConnection... clients) {
		final Set<ServerPushConnection> set = new HashSet<ServerPushConnection>();
		for (final ServerPushConnection client : clients) {
			set.add(client);
		}
		return set;
	}
}
