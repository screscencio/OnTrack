package br.com.oncast.ontrack.shared.model.actions;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ReleaseCreateAction extends ReleaseAction {

	UUID getNewReleaseId();
}
