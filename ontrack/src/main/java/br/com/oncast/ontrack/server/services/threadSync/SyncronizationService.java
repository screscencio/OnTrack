package br.com.oncast.ontrack.server.services.threadSync;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class SyncronizationService {

	private static Map<UUID, Object> projectMap = new HashMap<UUID, Object>();

	public Object getSyncLockFor(final UUID projectId) {
		if (projectMap.containsKey(projectId)) return projectMap.get(projectId);
		synchronized (this) {
			if (projectMap.containsKey(projectId)) return projectMap.get(projectId);
			projectMap.put(projectId, new Object());
			return projectMap.get(projectId);
		}
	}
}
