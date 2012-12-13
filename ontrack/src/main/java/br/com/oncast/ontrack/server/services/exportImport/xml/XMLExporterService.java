package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackMigrationManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.exceptions.UnableToExportXMLException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;

public class XMLExporterService {

	private final PersistenceService persistanceService;
	private final String version;

	public XMLExporterService(final PersistenceService persistenceService) {
		this.persistanceService = persistenceService;
		version = OntrackMigrationManager.getCurrentVersion();
	}

	public void export(final OutputStream outputStream, final UUID... projectIds) {
		try {
			final List<UUID> requestedIds = Arrays.asList(projectIds);
			new XMLWriter()
					.setVersion(version)
					.setUserList(retrieveAllUsers())
					.setProjectList(requestedIds.isEmpty() ? findAllProjectsWithActions() : findProjectsWithActions(requestedIds))
					.setProjectAuthorizationList(requestedIds.isEmpty() ? retrieveAllProjectAuthorizations() : retrieveProjectAuthorizations(requestedIds))
					.setNotifications(requestedIds.isEmpty() ? retrieveLatestNotifications() : retrieveLatestNotificationsForProjects(requestedIds))
					.export(outputStream);
		}
		catch (final NoResultFoundException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
		catch (final PersistenceException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
	}

	private List<ProjectXMLNode> findAllProjectsWithActions() throws PersistenceException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();
		for (final ProjectRepresentation project : persistanceService.retrieveAllProjectRepresentations()) {
			projectList.add(new ProjectXMLNode(project, persistanceService.retrieveActionsSince(project.getId(), 0)));
		}
		return projectList;
	}

	private List<ProjectXMLNode> findProjectsWithActions(final List<UUID> projectIds) throws PersistenceException, NoResultFoundException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();

		for (final UUID projectId : projectIds) {
			final ProjectRepresentation projectRepresentation = persistanceService.retrieveProjectRepresentation(projectId);
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

	private List<Notification> retrieveLatestNotificationsForProjects(final List<UUID> projectIds) throws PersistenceException {
		return persistanceService.retrieveLatestProjectNotifications(projectIds, getInitialFetchDate());
	}

	private List<Notification> retrieveLatestNotifications() throws PersistenceException {
		return persistanceService.retrieveLatestNotifications(getInitialFetchDate());
	}

	private Date getInitialFetchDate() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
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
		for (final ProjectAuthorization authorization : persistanceService.retrieveAllProjectAuthorizations()) {
			authNodes.add(new ProjectAuthorizationXMLNode(authorization));
		}
		return authNodes;
	}

	private List<ProjectAuthorizationXMLNode> retrieveProjectAuthorizations(final List<UUID> projectIds) throws PersistenceException {
		final List<ProjectAuthorizationXMLNode> authNodes = new ArrayList<ProjectAuthorizationXMLNode>();

		for (final ProjectAuthorization authorization : persistanceService.retrieveAllProjectAuthorizations()) {
			if (projectIds.contains(authorization.getProjectId())) {
				authNodes.add(new ProjectAuthorizationXMLNode(authorization));
			}
		}
		return authNodes;
	}

	public void listProjects(final OutputStream out) throws PersistenceException, IOException {
		for (final ProjectRepresentation representation : persistanceService.retrieveAllProjectRepresentations()) {
			out.write(representation.getId().toString().getBytes());
			out.write(",".getBytes());
		}
		out.flush();
	}

}
