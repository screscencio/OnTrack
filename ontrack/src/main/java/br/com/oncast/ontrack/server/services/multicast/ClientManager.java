package br.com.oncast.ontrack.server.services.multicast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ClientManager {

	private static final Logger LOGGER = Logger.getLogger(ClientManager.class);

	private static final long UNBOUND_PROJECT_INDEX = 0;
	private final Map<Long, Set<UUID>> clientsByProject = new HashMap<Long, Set<UUID>>();

	public void bindClientToProject(final UUID clientId, final long projectId) {
		if (projectId == UNBOUND_PROJECT_INDEX) throw new IllegalArgumentException("Client was not bound to the project: The given 'projectId' should not be 0");

		LOGGER.debug("Binding client '" + clientId + "' to project '" + projectId + "'.");
		add(clientId, projectId);
	}

	public void unbindClientFromProject(final UUID clientId) {
		LOGGER.debug("Unbinding client '" + clientId + "' from its project.");
		add(clientId, UNBOUND_PROJECT_INDEX);
	}

	public void registerClient(final UUID clientId) {
		add(clientId, UNBOUND_PROJECT_INDEX);
	}

	public void unregisterClient(final UUID clientId) {
		remove(clientId);
	}

	public Set<UUID> getClientsFor(final long projectId) {
		final HashSet<UUID> clients = new HashSet<UUID>();
		if (clientsByProject.containsKey(projectId)) clients.addAll(clientsByProject.get(projectId));
		return clients;
	}

	public Set<UUID> getAllClients() {
		final Set<UUID> allClients = new HashSet<UUID>();
		for (final Set<UUID> clientIds : clientsByProject.values()) {
			allClients.addAll(clientIds);
		}
		return allClients;
	}

	private void add(final UUID client, final long projectId) {
		remove(client);
		if (!clientsByProject.containsKey(projectId)) {
			clientsByProject.put(projectId, new HashSet<UUID>());
		}

		clientsByProject.get(projectId).add(client);
	}

	private void remove(final UUID client) {
		for (final Set<UUID> clients : clientsByProject.values()) {
			if (clients.contains(client)) {
				clients.remove(client);
				break;
			}
		}
	}

}
