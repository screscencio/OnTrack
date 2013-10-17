package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface HasDestination {

	UUID getSourceScopeId();

	ModelAction setDestination(UUID desiredParentScopeId, int desiredIndex);

}
