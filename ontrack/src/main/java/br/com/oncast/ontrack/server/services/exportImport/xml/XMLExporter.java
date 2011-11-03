package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLExporter {

	private final PersistenceService persistanceService;
	private final XMLWriter exporter;

	// FIXME Verify where we can save the current version of xml.
	// This should be the last migration time stamp executed.
	private final Date version;
	private boolean isConfigured;

	public XMLExporter(final PersistenceService persistanceService) {
		this.persistanceService = persistanceService;
		exporter = new XMLWriter();
		version = new Date();
		isConfigured = false;
	}

	public XMLExporter mountXML() {
		exporter.setUserList(findAllUsers()).setPasswordList(findAllPasswords()).setActionList(findAllActions()).setVersion(version.getTime());
		isConfigured = true;
		return this;
	}

	public void export() {
		if (!assureIsConfigured()) throw new RuntimeException("You must mount xml using mountXML method before use this method.");
		exporter.export();
	}

	private boolean assureIsConfigured() {
		return isConfigured;
	}

	// TODO Verify error treatment
	private List<UserAction> findAllActions() {
		final List<UserAction> actionList = new ArrayList<UserAction>();

		try {
			actionList.addAll(persistanceService.retrieveActionsSince(0));
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
