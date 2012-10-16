package br.com.oncast.ontrack.client.services.applicationState;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ClientApplicationStateService {

	void startRecording();

	void stopRecording();

	void restore();

	void restore(UUID scopeSelectedId);

}
