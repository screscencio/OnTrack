package br.com.oncast.ontrack.server.services.admin;

import java.util.Date;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

public class ServerStatisticsService {

	private final PersistenceService persistenceService;

	public ServerStatisticsService(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public long getActionsCountSince(final Date date) {
		try {
			return persistenceService.countActionsSince(date);
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public int getUsersCount() {
		try {
			return persistenceService.retrieveAllUsers().size();
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	public int getProjectsCount() {
		try {
			return persistenceService.retrieveAllProjectRepresentations().size();
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
}
