package br.com.oncast.ontrack.server.services.actionSync;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushClient;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;

public class ActionBroadcastServiceImpl implements ActionBroadcastService {

	private final ServerPushServerService serverPushServerService;
	protected Map<String, ServerPushClient> clientMap = new HashMap<String, ServerPushClient>();

	public ActionBroadcastServiceImpl(final ServerPushServerService serverPushServerService) {
		this.serverPushServerService = serverPushServerService;
		this.serverPushServerService.registerConnectionListener(createServerPushConnectionListener());
	}

	@Override
	public void broadcast(final ModelAction action) {
		serverPushServerService.pushEvent(new ServerActionSyncEvent(action), getClientsWithout(serverPushServerService.getSender()));
	}

	private Set<ServerPushClient> getClientsWithout(final ServerPushClient clientToBeRemoved) {
		final Set<ServerPushClient> clients = new HashSet<ServerPushClient>();
		for (final ServerPushClient client : clientMap.values()) {
			if (client.getSessionId().equals(clientToBeRemoved.getSessionId())) continue;
			clients.add(client);
		}

		return clients;
	}

	private ServerPushConnectionListener createServerPushConnectionListener() {
		return new ServerPushConnectionListener() {
			@Override
			public void onClientConnected(final ServerPushClient client) {
				// FIXME +++Implement a log service.
				// TODO Remove SYSO
				System.out.println("Putting a new client into the map.");
				clientMap.put(client.getSessionId(), client);
			}

			@Override
			public void onClientDisconnected(final ServerPushClient client) {
				// TODO Remove SYSO
				System.out.println("Removing a client from map.");
				clientMap.remove(client.getSessionId());
			}
		};
	}

}
