package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.exceptions.UnableToImportXMLException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLImporter {

	private final PersistenceService persistanceService;
	private OntrackXML ontrackXML;
	private final HashMap<Long, User> userIdMap = new HashMap<Long, User>();
	private final HashMap<Long, ProjectRepresentation> projectIdMap = new HashMap<Long, ProjectRepresentation>();
	private long adminId = -1;

	public XMLImporter(final PersistenceService persistenceService) {
		this.persistanceService = persistenceService;
	}

	public XMLImporter loadXML(final File file) {
		final Serializer serializer = new Persister();

		try {
			ontrackXML = serializer.read(OntrackXML.class, file);
		}
		catch (final Exception e) {
			throw new UnableToImportXMLException("Unable to deserialize xml file.", e);
		}
		return this;
	}

	public void persistObjects() {
		if (ontrackXML == null) throw new RuntimeException("You must use loadXML method to load xml before use this method.");

		try {
			persistUsers(ontrackXML.getUsers());
			persistProjects(ontrackXML.getProjects());
			persistAuthorizations(ontrackXML.getProjectAuthorizations());
		}
		catch (final PersistenceException e) {
			throw new UnableToImportXMLException("The xml import was not concluded. Some operations may be changed the database, but was not rolledback. ", e);
		}
	}

	private void persistUsers(final List<UserXMLNode> userNodes) throws PersistenceException {
		for (final UserXMLNode userNode : userNodes) {
			User persistedUser;
			try {
				persistedUser = persistanceService.retrieveUserByEmail(userNode.getUser().getEmail());
			}
			catch (final NoResultFoundException e) {
				persistedUser = persistanceService.persistOrUpdateUser(userNode.getUser());
				if (userNode.hasPassword()) persistPassword(persistedUser.getId(), userNode.getPassword());
			}
			userIdMap.put(userNode.getId(), persistedUser);
		}
	}

	private void persistPassword(final long userId, final Password password) throws PersistenceException {
		password.setUserId(userId);
		persistanceService.persistOrUpdatePassword(password);
	}

	private void persistProjects(final List<ProjectXMLNode> projectNodes) throws PersistenceException {
		for (final ProjectXMLNode projectNode : projectNodes) {
			final ProjectRepresentation persistedRepresentation = persistanceService.persistOrUpdateProjectRepresentation(projectNode
					.getProjectRepresentation());
			persistActions(persistedRepresentation.getId(), projectNode.getActions());
			projectIdMap.put(projectNode.getId(), persistedRepresentation);
		}
	}

	private void persistActions(final long projectId, final List<UserAction> userActions) throws PersistenceException {
		for (final UserAction userAction : userActions) {
			final ArrayList<ModelAction> actions = new ArrayList<ModelAction>();
			actions.add(userAction.getModelAction());
			final User user = userIdMap.get(userAction.getUserId());
			persistanceService.persistActions(projectId, user == null ? getAdminId() : user.getId(), actions, userAction.getTimestamp());
		}
	}

	private long getAdminId() throws PersistenceException {
		try {
			return adminId < 0 ? adminId = persistanceService.retrieveUserByEmail(DefaultAuthenticationCredentials.USER_EMAIL).getId() : adminId;
		}
		catch (final NoResultFoundException e) {
			return adminId = 1;
		}
	}

	private void persistAuthorizations(final List<ProjectAuthorizationXMLNode> projectAuthorizationNodes) throws PersistenceException {
		for (final ProjectAuthorizationXMLNode authNode : projectAuthorizationNodes) {
			persistanceService.authorize(userIdMap.get(authNode.getUserId()).getEmail(), projectIdMap.get(authNode.getProjectId()).getId());
		}

	}

}