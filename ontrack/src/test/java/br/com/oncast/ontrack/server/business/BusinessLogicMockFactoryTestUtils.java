package br.com.oncast.ontrack.server.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import br.com.oncast.ontrack.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.actionBroadcast.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class BusinessLogicMockFactoryTestUtils {

	public static BusinessLogic createWithJpaPersistenceAndCustomBroadcastMock(final ActionBroadcastService broadcastMock) {
		return new BusinessLogicImpl(new PersistenceServiceJpaImpl(), broadcastMock);
	}

	public static BusinessLogic createWithDumbPersistenceMockAndDumbBroadcastMock() {
		return new BusinessLogicImpl(getPersistenceMock(), getBroadcastMock());
	}

	public static BusinessLogic createWithDumbNonWritablePersistenceMockAndDumbBroadcastMock() {
		return new BusinessLogicImpl(getNonWritablePersistenceMock(), getBroadcastMock());
	}

	public static BusinessLogic createWithJpaPersistenceAndDumbBroadcastMock() {
		return new BusinessLogicImpl(new PersistenceServiceJpaImpl(), getBroadcastMock());
	}

	private static PersistenceService getPersistenceMock() {
		return new PersistenceService() {

			private final List<UserAction> actions = new ArrayList<UserAction>();

			final Date snapshotTimestamp = new Date();

			@Override
			public synchronized ProjectSnapshot retrieveProjectSnapshot(final long projectId) throws PersistenceException {
				try {
					return new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp);
				}
				catch (final IOException e) {
					throw new PersistenceException(e);
				}
			}

			@Override
			public synchronized List<UserAction> retrieveActionsSince(final long projectId, final long actionId) throws PersistenceException {
				return actions;
			}

			@Override
			@SuppressWarnings("hiding")
			public synchronized void persistActions(final long projectId, final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
				for (final ModelAction modelAction : new ArrayList<ModelAction>(actions)) {
					actions.add(modelAction);
				}
			}

			@Override
			public void persistProjectSnapshot(final ProjectSnapshot projectSnapshot) throws PersistenceException {}

			@Override
			public User findUserByEmail(final String email) throws NoResultFoundException, PersistenceException {
				return null;
			}

			@Override
			public Password findPasswordForUser(final long userId) throws NoResultFoundException, PersistenceException {
				return null;
			}

			@Override
			public void persistOrUpdatePassword(final Password passwordForUser) throws PersistenceException {}

			@Override
			public User persistOrUpdateUser(final User user) throws PersistenceException {
				return null;
			}

			@Override
			public List<User> findAllUsers() throws PersistenceException {
				return null;
			}

			@Override
			public List<Password> findAllPasswords() throws PersistenceException {
				return null;
			}

			@Override
			public ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation project) throws PersistenceException {
				return null;
			}

			@Override
			public ProjectRepresentation findProjectRepresentation(final long projectId) throws PersistenceException {
				return null;
			}

			@Override
			public List<ProjectRepresentation> findAllProjectRepresentations() throws PersistenceException {
				return null;
			}

		};
	}

	private static PersistenceService getNonWritablePersistenceMock() {
		return new PersistenceService() {

			private final List<UserAction> actions = new ArrayList<UserAction>();

			final Date snapshotTimestamp = new Date();

			@Override
			public ProjectSnapshot retrieveProjectSnapshot(final long projectId) throws PersistenceException {
				try {
					return new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp);
				}
				catch (final IOException e) {
					throw new PersistenceException(e);
				}
			}

			@Override
			public List<UserAction> retrieveActionsSince(final long projectId, final long actionId) throws PersistenceException {
				return actions;
			}

			@Override
			@SuppressWarnings("hiding")
			public void persistActions(final long projectId, final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
				Assert.fail("The persistence should not be accessed.");
			}

			@Override
			public void persistProjectSnapshot(final ProjectSnapshot projectSnapshot) throws PersistenceException {}

			@Override
			public User findUserByEmail(final String email) throws NoResultFoundException, PersistenceException {
				return null;
			}

			@Override
			public Password findPasswordForUser(final long userId) throws NoResultFoundException, PersistenceException {
				return null;
			}

			@Override
			public void persistOrUpdatePassword(final Password passwordForUser) throws PersistenceException {}

			@Override
			public User persistOrUpdateUser(final User user) throws PersistenceException {
				return null;
			}

			@Override
			public List<User> findAllUsers() throws PersistenceException {
				return null;
			}

			@Override
			public List<Password> findAllPasswords() throws PersistenceException {
				return null;
			}

			@Override
			public ProjectRepresentation persistOrUpdateProjectRepresentation(final ProjectRepresentation project) throws PersistenceException {
				return null;
			}

			@Override
			public ProjectRepresentation findProjectRepresentation(final long projectId) throws PersistenceException {
				return null;
			}

			@Override
			public List<ProjectRepresentation> findAllProjectRepresentations() throws PersistenceException {
				return null;
			}
		};
	}

	private static ActionBroadcastService getBroadcastMock() {
		return new ActionBroadcastService() {
			@Override
			public void broadcast(final ModelActionSyncRequest modelActionSyncRequest) {}
		};
	}
}
