package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackMigrationManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.exceptions.UnableToExportXMLException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorizationEntity;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLExporterService {

	private final PersistenceService persistanceService;
	private final String version;

	public XMLExporterService(final PersistenceService persistenceService) {
		this.persistanceService = persistenceService;
		version = OntrackMigrationManager.getCurrentVersion();
	}

	public void export(final OutputStream outputStream) {
		try {
			new XMLWriter()
					.setVersion(version)
					.setUserList(retrieveAllUsers())
					.setProjectList(findAllProjectsWithActions())
					.setProjectAuthorizationList(retrieveAllProjectAuthorizations())
					.export(outputStream);
		}
		catch (final PersistenceException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
	}

	private List<ProjectXMLNode> findAllProjectsWithActions() throws PersistenceException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();

		final List<ProjectRepresentation> allProjectRepresentations = persistanceService.retrieveAllProjectRepresentations();
		for (final ProjectRepresentation projectRepresentation : allProjectRepresentations) {
			projectList.add(new ProjectXMLNode(projectRepresentation, persistanceService.retrieveActionsSince(projectRepresentation.getId(), 0)));
		}

		return projectList;
	}

	private List<UserXMLNode> retrieveAllUsers() throws PersistenceException {
		final List<UserXMLNode> userXMLNodeList = new ArrayList<UserXMLNode>();

		final List<User> users = persistanceService.retrieveAllUsers();
		for (final User user : users) {
			userXMLNodeList.add(associatePasswordTo(user));
		}

		return userXMLNodeList;
	}

	private UserXMLNode associatePasswordTo(final User user) throws PersistenceException {
		final UserXMLNode userXMLNode = new UserXMLNode(user);
		try {
			userXMLNode.setPassword(persistanceService.retrievePasswordForUser(user.getId()));
		}
		catch (final NoResultFoundException e) {
			// This user doesn't have a password.
		}
		return userXMLNode;
	}

	private List<ProjectAuthorizationXMLNode> retrieveAllProjectAuthorizations() throws PersistenceException {
		final List<ProjectAuthorizationXMLNode> authNodes = new ArrayList<ProjectAuthorizationXMLNode>();
		for (final ProjectAuthorizationEntity authorization : persistanceService.retrieveAllProjectAuthorizations()) {
			authNodes.add(new ProjectAuthorizationXMLNode(authorization));
		}
		return authNodes;
	}

}
