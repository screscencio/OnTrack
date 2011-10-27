package br.com.oncast.ontrack.server.services.persistence;

import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.server.business.UserAction;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.user.User;

public interface PersistenceService {

	public void persistActions(final List<ModelAction> actionList, final Date timestamp) throws PersistenceException;

	public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException, NoResultFoundException;

	public List<UserAction> retrieveActionsSince(long actionId) throws PersistenceException;

	public void persistProjectSnapshot(ProjectSnapshot projectSnapshot) throws PersistenceException;

	public User findUserByEmail(String email) throws NoResultFoundException, PersistenceException;

	public void persistOrUpdateUser(User user) throws PersistenceException;

	public Password findPasswordForUser(long userId) throws NoResultFoundException, PersistenceException;

	public void persistOrUpdatePassword(Password passwordForUser) throws PersistenceException;
}