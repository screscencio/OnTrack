package br.com.oncast.ontrack.server.services.multicast;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertContainsNone;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ClientManagerTest {

	private ClientManager manager;

	private UUID client1;
	private UUID client2;
	private UUID client3;
	private UUID client4;

	private long project1;
	private long project2;

	@Before
	public void setup() {
		manager = new ClientManager();

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
	}

	@Test
	public void anEmptySetShouldBeReturnedwhenThereIsNoBoundClientToTheGivenProject() throws Exception {
		assertTrue(manager.getClientsFor(project1).isEmpty());
		assertTrue(manager.getClientsFor(project2).isEmpty());
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

		final Set<UUID> clientsForProject1 = manager.getClientsFor(project1);
		clientsForProject1.clear();
		assertCollectionEquality(asSet(client1, client2), manager.getClientsFor(project1));
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

		assertCollectionEquality(asSet(client1, client3), manager.getClientsFor(project1));
	}

	@Test
	public void shouldBeAbleToBindAClientByHisIdWhenHeIsAlreadyRegistered() {
		registerClients(client1, client2, client3);
		bindClients(project2, client1, client2);

		assertCollectionEquality(asSet(client1, client2), manager.getClientsFor(project2));
	}

	@Test
	public void shouldBeAbleToBindAClientThatWasNotRegisteredYet() {
		registerClients(client1, client2, client3);
		bindClients(project2, client3, client4);

		assertCollectionEquality(asSet(client3, client4), manager.getClientsFor(project2));
	}

	@Test
	public void shouldBeAbleToBindAClientToAProject() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client3, client4);

		final Set<UUID> obtainedClientsForProject1 = manager.getClientsFor(project1);
		final Set<UUID> obtainedClientsForProject2 = manager.getClientsFor(project2);

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

		assertCollectionEquality(asSet(client1, client3), manager.getClientsFor(project1));
	}

	@Test
	public void theClientShouldBeUnboundFromPreviousProjectWhenBoundToAnother() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project2, client2, client3);

		assertCollectionEquality(asSet(client1), manager.getClientsFor(project1));
		assertContainsNone(asSet(client2, client3, client4), manager.getClientsFor(project1));
	}

	@Test
	public void duplicatedClientsAreNotAllowed() throws Exception {
		registerAndBindClients(project1, client1, client2);
		registerAndBindClients(project1, client2, client3);

		assertCollectionEquality(asSet(client1, client2, client3), manager.getClientsFor(project1));
	}

	private void bindClients(final long projectId, final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.bindClientToProject(clientId, projectId);
		}
	}

	private void registerAndBindClients(final long projectId, final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.registerClient(clientId);
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
			manager.registerClient(clientId);
		}
	}

	private void unregisterClients(final UUID... clientIds) {
		for (final UUID clientId : clientIds) {
			manager.unregisterClient(clientId);
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
