package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackMigrationManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.exceptions.UnableToExportXMLException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
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
		try {
			exporter.setUserList(findAllUsers()).setProjectList(findAllProjectsWithActions()).setVersion(version);
			isConfigured = true;
			return this;
		}
		catch (final PersistenceException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
	}

	public void export() {
		if (!assureIsConfigured()) throw new RuntimeException("You must mount xml using mountXML method before use this method.");
		exporter.export(outputStream);
	}

	private boolean assureIsConfigured() {
		return isConfigured;
	}

	private List<ProjectXMLNode> findAllProjectsWithActions() throws PersistenceException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();

		final List<ProjectRepresentation> allProjectRepresentations = persistanceService.findAllProjectRepresentations();
		for (final ProjectRepresentation projectRepresentation : allProjectRepresentations) {
			projectList.add(new ProjectXMLNode(projectRepresentation, persistanceService.retrieveActionsSince(projectRepresentation.getId(), 0)));
		}

		return projectList;
	}

	private List<UserXMLNode> findAllUsers() throws PersistenceException {
		final List<UserXMLNode> userXMLNodeList = new ArrayList<UserXMLNode>();

		final List<User> users = persistanceService.findAllUsers();
		for (final User user : users) {
			userXMLNodeList.add(associatePasswordTo(user));
		}

		return userXMLNodeList;
	}

	private UserXMLNode associatePasswordTo(final User user) throws PersistenceException {
		final UserXMLNode userXMLNode = new UserXMLNode(user);
		try {
			userXMLNode.setPassword(persistanceService.findPasswordForUser(user.getId()));
		}
		catch (final NoResultFoundException e) {
			// This user doesn't have a password.
		}
		return userXMLNode;
	}

}
