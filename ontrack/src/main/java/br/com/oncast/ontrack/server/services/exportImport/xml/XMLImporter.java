package br.com.oncast.ontrack.server.services.exportImport.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.exceptions.UnableToImportXMLException;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

public class XMLImporter {

	private final PersistenceService persistanceService;
	private OntrackXML ontrackXML;

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
		}
		catch (final PersistenceException e) {
			throw new UnableToImportXMLException("The xml import was not concluded. Some operations may be changed the database, but was not rolledback. ", e);
		}
	}

	private void persistUsers(final List<UserXMLNode> userList) throws PersistenceException {
		for (final UserXMLNode user : userList) {
			try {
				persistanceService.retrieveUserByEmail(user.getUser().getEmail());
			}
			catch (final NoResultFoundException e) {
				final User persistedUser = persistanceService.persistOrUpdateUser(user.getUser());
				if (user.hasPassword()) persistPassword(persistedUser.getId(), user.getPassword());
			}
		}
	}

	private void persistPassword(final long userId, final Password password) throws PersistenceException {
		password.setUserId(userId);
		persistanceService.persistOrUpdatePassword(password);
	}

	private void persistProjects(final List<ProjectXMLNode> projects) throws PersistenceException {
		for (final ProjectXMLNode project : projects) {
			final ProjectRepresentation persistedProjectRepresentation = persistanceService.persistOrUpdateProjectRepresentation(project
					.getProjectRepresentation());
			persistActions(persistedProjectRepresentation.getId(), project.getActions());
		}
	}

	private void persistActions(final long projectId, final List<UserAction> userActions) throws PersistenceException {
		for (final UserAction userAction : userActions) {
			final ArrayList<ModelAction> actions = new ArrayList<ModelAction>();
			actions.add(userAction.getModelAction());
			persistanceService.persistActions(projectId, actions, userAction.getTimestamp());
		}
	}
}