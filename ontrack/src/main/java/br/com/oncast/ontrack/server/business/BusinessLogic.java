package br.com.oncast.ontrack.server.business;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

public class BusinessLogic {

	private final PersistenceService persistenceService = new PersistenceServiceJpaImpl();

	public void handleIncomingAction(final ModelAction action) {
		// FIXME
		persistenceService.persist(action, new Date());
	}

	public Project loadProject() {
		// FIXME
		final ProjectSnapshot snapshot = persistenceService.retrieveProjectSnapshot();
		final Date timestamp = snapshot.getTimestamp();
		final List<ModelAction> actionList = persistenceService.retrieveActionsSince(timestamp);

	}

}
