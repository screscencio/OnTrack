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
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class ClientManagerTest {

	private static final String DEFAULT_SESSION_ID = "sessionId";

	private ClientManager manager;

	private UUID client1;
	private UUID client2;
	private UUID client3;
	private UUID client4;

	private long project1;
	private long project2;

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

		client1 = new UUID("1");
		client2 = new UUID("2");
		client3 = new UUID("3");
		client4 = new UUID("4");

		project1 = 1L;
		project2 = 2L;
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

	@Test
	public void manipulationOnReturnedClientSetShouldNotChangeOriginalClientStructure() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerClients(client3, client4);

		final Set<UUID> allClients = manager.getAllClients();
		allClients.clear();
		assertCollectionEquality(asSet(client1, client2, client3, client4), manager.getAllClients());

		final Set<UUID> clientsForProject1 = manager.getClientsAtProject(project1);
		clientsForProject1.clear();
		assertCollectionEquality(asSet(client1, client2), manager.getClientsAtProject(project1));
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

		final Set<UUID> obtainedClientsForProject1 = manager.getClientsAtProject(project1);
		final Set<UUID> obtainedClientsForProject2 = manager.getClientsAtProject(project2);

		final Set<UUID> expectedClientsForProject1 = asSet(client1, client2);
		final Set<UUID> expectedClientsForProject2 = asSet(client3, client4);

		assertCollectionEquality(expectedClientsForProject1, obtainedClientsForProject1);
		assertContainsNone(expectedClientsForProject2, obtainedClientsForProject1);

		assertCollectionEquality(expectedClientsForProject2, obtainedClientsForProject2);
		assertContainsNone(expectedClientsForProject1, obtainedClientsForProject2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBeAbleToBindAClientToAProjectWithIdZero() throws Exception {
		registerAndBindClients(0, client1, client2);
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

		manager.registerClient(client1, sessionId);
		manager.registerClient(client2, sessionId);
		manager.registerClient(client3, sessionId);
		manager.registerClient(client4, "other session");
		assertEquals(0, manager.getClientsOfUser(userId).size());

		authenticationListener.onUserLoggedIn(UserTestUtils.createUser(userId), sessionId);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(userId));
	}

	@Test
	public void aUserIsDisassociatedFromSessionOnLogout() throws Exception {
		final String sessionId = "sessionId";
		final User user = UserTestUtils.createUser(1);

		manager.registerClient(client1, sessionId);
		manager.registerClient(client2, sessionId);
		manager.registerClient(client3, sessionId);
		manager.registerClient(client4, "other session");
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

		manager.registerClient(client1, session1);
		manager.registerClient(client2, session1);
		manager.registerClient(client3, session2);
		manager.registerClient(client4, "other session");
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

		manager.registerClient(client1, session1);
		manager.registerClient(client2, session1);
		manager.registerClient(client3, session2);
		assertEquals(0, manager.getClientsOfUser(user.getId()).size());

		authenticationListener.onUserLoggedIn(user, session1);
		authenticationListener.onUserLoggedIn(user, session2);
		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsOfUser(user.getId()));

		authenticationListener.onUserLoggedOut(user, session1);
		assertCollectionEquality(asSet(client3), manager.getClientsOfUser(user.getId()));
	}

	private void bindClients(final long projectId, final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.bindClientToProject(clientId, projectId);
		}
	}

	private void registerAndBindClients(final long projectId, final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.registerClient(clientId, DEFAULT_SESSION_ID);
			manager.bindClientToProject(clientId, projectId);
		}
	}

	private void unbindClients(final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.unbindClientFromProject(clientId);
		}
	}

	private void registerClients(final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.registerClient(clientId, DEFAULT_SESSION_ID);
		}
	}

	private void unregisterClients(final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.unregisterClient(clientId, DEFAULT_SESSION_ID);
		}
	}

	private Set<UUID> asSet(final UUID... clients) {
		final Set<UUID> set = new HashSet<UUID>();
		for (final UUID client : clients) {
			set.add(client);
		}
		return set;
	}
}
