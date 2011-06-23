package br.com.oncast.ontrack.server.services.persistence;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface PersistenceService {

	public void persist(final ModelAction action);
}