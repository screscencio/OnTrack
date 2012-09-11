package br.com.oncast.ontrack.server.services.notification;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertContainsNone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
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
	private ServerPushConnection client4;

	private UUID project1;
	private UUID project2;

	@Mock
	private AuthenticationManager authenticationManager;

	private AuthenticationListener authenticationListener;

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
		client4 = new CometClientConnection("4", "othersession");

		project1 = new UUID();
		project2 = new UUID();
	}

	@Test
	public void thereAreNoClientsWhenThereIsNoRegisteredOrBoundClient() throws Exception {
		assertTrue(manager.getAllClients().isEmpty());
		assertTrue(manager.getClientsAtProject(project1).isEmpty());
		assertTrue(manager.getClientsOfUser(1).isEmpty());
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
		assertTrue(manager.getClientsOfUser(1).isEmpty());
		assertTrue(manager.getClientsOfUser(2).isEmpty());
	}

	@Test
	public void shouldBeAbleToGetAllClients() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client4);

		assertCollectionEquality(asSet(client1, client2, client4), manager.getAllClients());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void manipulationOnReturnedClientSetShouldNotBeAllowed() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerClients(client3, client4);

		final Set<ServerPushConnection> allClients = manager.getAllClients();
		allClients.clear();
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
		bindClients(project2, client3, client4);

		assertCollectionEquality(asSet(client3, client4), manager.getClientsAtProject(project2));
	}

	@Test
	public void shouldBeAbleToBindAClientToAProject() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client3, client4);

		final Set<ServerPushConnection> obtainedClientsForProject1 = manager.getClientsAtProject(project1);
		final Set<ServerPushConnection> obtainedClientsForProject2 = manager.getClientsAtProject(project2);

		final Set<ServerPushConnection> expectedClientsForProject1 = asSet(client1, client2);
		final Set<ServerPushConnection> expectedClientsForProject2 = asSet(client3, client4);

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
		assertContainsNone(asSet(client2, client3, client4), manager.getClientsAtProject(project1));
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
		final int userId = 1;

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		manager.registerClient(client4); // TODO other session
		assertEquals(0, manager.getClientsOfUser(userId).size());

		authenticationListener.onUserLoggedIn(UserTestUtils.createUser(userId), sessionId);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(userId));
	}

	@Test
	public void aUserIsDisassociatedFromSessionOnLogout() throws Exception {
		final String sessionId = "sessionId";
		final User user = UserTestUtils.createUser(1);

		manager.registerClient(client1);
		manager.registerClient(client2);
		manager.registerClient(client3);
		manager.registerClient(client4); // TODO other session
		assertEquals(0, manager.getClientsOfUser(user.getId()).size());

		authenticationListener.onUserLoggedIn(user, sessionId);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getId()));

		authenticationListener.onUserLoggedOut(user, sessionId);
		assertEquals(0, manager.getClientsOfUser(user.getId()).size());
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
		assertEquals(0, manager.getClientsOfUser(user.getId()).size());

		authenticationListener.onUserLoggedIn(user, session1);
		assertCollectionEquality(asSet(client1, client2), manager.getClientsOfUser(user.getId()));

		authenticationListener.onUserLoggedIn(user, session2);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getId()));
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
		assertEquals(0, manager.getClientsOfUser(user.getId()).size());

		authenticationListener.onUserLoggedIn(user, session1);
		authenticationListener.onUserLoggedIn(user, session2);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getId()));

		authenticationListener.onUserLoggedOut(user, session1);
		assertCollectionEquality(asSet(client3), manager.getClientsOfUser(user.getId()));
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
