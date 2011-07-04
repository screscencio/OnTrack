package br.com.oncast.ontrack.server.services.persistence;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.shared.exceptions.persistence.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface PersistenceService {

	public void persistAction(final ModelAction action, final Date timestamp) throws PersistenceException;

	public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException;

	public List<ModelAction> retrieveActionsSince(Date timestamp) throws PersistenceException;
}