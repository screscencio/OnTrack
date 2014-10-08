package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ContextChangeListener {
	void onProjectChanged(UUID projectId, Long loadedProjectRevision);
}
