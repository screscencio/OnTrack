package br.com.oncast.ontrack.server.services.persistence;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

public interface PersistenceService {

	public void persist(final ModelAction action);

	public Project load();
}