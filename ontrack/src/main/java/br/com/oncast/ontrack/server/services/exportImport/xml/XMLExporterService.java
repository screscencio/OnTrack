package br.com.oncast.ontrack.server.services.exportImport.xml;

import br.com.oncast.ontrack.server.services.authentication.Password;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class XMLExporterService {

	private final PersistenceService persistanceService;
	private final String version;

	public XMLExporterService(final PersistenceService persistenceService) {
		this.persistanceService = persistenceService;
		version = OntrackMigrationManager.getCurrentVersion();
	}

	public void export(final OutputStream outputStream, final List<UUID> projectIds) {
		try {
			new XMLWriter().setVersion(version).setProjectsList(projectIds.isEmpty() ? getAllProjectsWithActions() : getProjectsWithActions(projectIds))
					.setProjectAuthorizationsList(projectIds.isEmpty() ? getAllProjectAuthorizations() : getProjectAuthorizationsFor(projectIds))
					.setNotifications(projectIds.isEmpty() ? getLatestNotificationsForAllProjects() : getLatestNotificationsFor(projectIds))
					.setUserList(projectIds.isEmpty() ? getAllUsers() : new ArrayList<UserXMLNode>()).export(outputStream);
		} catch (final NoResultFoundException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		} catch (final PersistenceException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
	}

	public void exportUsers(final OutputStream outputStream) {
		try {
			new XMLWriter().setVersion(version).setProjectsList(new ArrayList<ProjectXMLNode>()).setProjectAuthorizationsList(new ArrayList<ProjectAuthorizationXMLNode>())
					.setNotifications(new ArrayList<Notification>()).setUserList(getAllUsers()).export(outputStream);
		} catch (final PersistenceException e) {
			throw new UnableToExportXMLException("Could not mount xml.", e);
		}
	}

	private List<ProjectXMLNode> getAllProjectsWithActions() throws PersistenceException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();
		for (final ProjectRepresentation project : persistanceService.retrieveAllProjectRepresentations()) {
			projectList.add(new ProjectXMLNode(project, persistanceService.retrieveActionsSince(project.getId(), 0)));
		}
		return projectList;
	}

	private List<ProjectXMLNode> getProjectsWithActions(final List<UUID> projectIds) throws PersistenceException, NoResultFoundException {
		final List<ProjectXMLNode> projectList = new ArrayList<ProjectXMLNode>();

		for (final UUID projectId : projectIds) {
			final ProjectRepresentation projectRepresentation = persistanceService.retrieveProjectRepresentation(projectId);
			projectList.add(new ProjectXMLNode(projectRepresentation, persistanceService.retrieveActionsSince(projectRepresentation.getId(), 0)));
		}

		return projectList;
	}

	private List<UserXMLNode> getAllUsers() throws PersistenceException {
		final List<UserXMLNode> userXMLNodeList = new ArrayList<UserXMLNode>();

		final List<User> users = persistanceService.retrieveAllUsers();
		for (final User user : users) {
			userXMLNodeList.add(associatePasswordTo(user));
		}

		return userXMLNodeList;
	}

	private List<Notification> getLatestNotificationsFor(final List<UUID> projectIds) throws PersistenceException {
		return persistanceService.retrieveLatestProjectNotifications(projectIds, getInitialFetchDate());
	}

	private List<Notification> getLatestNotificationsForAllProjects() throws PersistenceException {
		return persistanceService.retrieveLatestNotifications(getInitialFetchDate());
	}

	private Date getInitialFetchDate() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	private UserXMLNode associatePasswordTo(final User user) throws PersistenceException {
		final UserXMLNode userXMLNode = new UserXMLNode(user);
		final List<Password> passwordsForUser = persistanceService.retrievePasswordsForUser(user.getId());
		if (!passwordsForUser.isEmpty()) userXMLNode.setPassword(passwordsForUser.get(passwordsForUser.size() - 1));
		return userXMLNode;
	}

	private List<ProjectAuthorizationXMLNode> getAllProjectAuthorizations() throws PersistenceException {
		final List<ProjectAuthorizationXMLNode> authNodes = new ArrayList<ProjectAuthorizationXMLNode>();
		for (final ProjectAuthorization authorization : persistanceService.retrieveAllProjectAuthorizations()) {
			authNodes.add(new ProjectAuthorizationXMLNode(authorization));
		}
		return authNodes;
	}

	private List<ProjectAuthorizationXMLNode> getProjectAuthorizationsFor(final List<UUID> projectIds) throws PersistenceException {
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
	}

}
