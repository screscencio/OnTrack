package br.com.oncast.ontrack.server.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import br.com.oncast.ontrack.mocks.models.ProjectMock;
import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.actionBroadcast.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
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
			public synchronized ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				try {
					return new ProjectSnapshot(ProjectMock.getProject(), snapshotTimestamp);
				}
				catch (final IOException e) {
					throw new PersistenceException(e);
				}
			}

			@Override
			public synchronized List<UserAction> retrieveActionsSince(final long actionId) throws PersistenceException {
				return actions;
			}

			@Override
			public synchronized void persistActions(final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
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
			public Password findPasswordForUserId(final long userId) throws NoResultFoundException, PersistenceException {
				// FIXME Auto-generated catch block
				return null;
			}

			@Override
			public void persistPassword(final Password passwordForUser) throws PersistenceException {
				// FIXME Auto-generated catch block

			}

			@Override
			public void persistOrUpdateUser(final User user) throws PersistenceException {
				// FIXME Auto-generated catch block

			}

		};
	}

	private static PersistenceService getNonWritablePersistenceMock() {
		return new PersistenceService() {

			private final List<UserAction> actions = new ArrayList<UserAction>();

			final Date snapshotTimestamp = new Date();

			@Override
			public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				try {
					return new ProjectSnapshot(ProjectMock.getProject(), snapshotTimestamp);
				}
				catch (final IOException e) {
					throw new PersistenceException(e);
				}
			}

			@Override
			public List<UserAction> retrieveActionsSince(final long actionId) throws PersistenceException {
				return actions;
			}

			@Override
			public void persistActions(final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
				Assert.fail("The persistence should not be accessed.");
			}

			@Override
			public void persistProjectSnapshot(final ProjectSnapshot projectSnapshot) throws PersistenceException {}

			@Override
			public User findUserByEmail(final String email) throws NoResultFoundException, PersistenceException {
				return null;
			}

			@Override
			public Password findPasswordForUserId(final long userId) throws NoResultFoundException, PersistenceException {
				// FIXME Auto-generated catch block
				return null;
			}

			@Override
			public void persistPassword(final Password passwordForUser) throws PersistenceException {
				// FIXME Auto-generated catch block

			}

			@Override
			public void persistOrUpdateUser(final User user) throws PersistenceException {
				// FIXME Auto-generated catch block

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
