package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackMigrationManager;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLExporter {

	private final PersistenceService persistanceService;
	private final XMLWriter exporter;

	private final String version;
	private boolean isConfigured;
	private final OutputStream outputStream;

	public XMLExporter(final PersistenceService persistanceService, final OutputStream outputStream) {
		this.persistanceService = persistanceService;
		this.outputStream = outputStream;
		exporter = new XMLWriter();
		version = OntrackMigrationManager.getCurrentVersion();
		isConfigured = false;
	}

	public XMLExporter mountXML() {
		exporter.setUserList(findAllUsers()).setPasswordList(findAllPasswords()).setActionList(findAllActions()).setVersion(version);
		isConfigured = true;
		return this;
	}

	public void export() {
		if (!assureIsConfigured()) throw new RuntimeException("You must mount xml using mountXML method before use this method.");
		exporter.export(outputStream);
	}

	private boolean assureIsConfigured() {
		return isConfigured;
	}

	// TODO Verify error treatment
	private List<UserAction> findAllActions() {
		final List<UserAction> actionList = new ArrayList<UserAction>();

		try {
			// FIXME Use a correct project id.
			actionList.addAll(persistanceService.retrieveActionsSince(0, 0));
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
		}

		return actionList;
	}

	// TODO Verify error treatment
	private List<Password> findAllPasswords() {
		final List<Password> passwordList = new ArrayList<Password>();

		try {
			passwordList.addAll(persistanceService.findAllPasswords());
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
		}

		return passwordList;
	}

	// TODO Verify error treatment
	private List<User> findAllUsers() {
		final List<User> userList = new ArrayList<User>();

		try {
			userList.addAll(persistanceService.findAllUsers());
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
		}

		return userList;
	}

}
