package br.com.oncast.ontrack.server.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import br.com.oncast.ontrack.mocks.models.ProjectMock;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.actionBroadcast.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
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

			private final List<ModelAction> actions = new ArrayList<ModelAction>();

			@Override
			public synchronized ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				return new ProjectSnapshot(ProjectMock.getProject(), new Date());
			}

			@Override
			public synchronized List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
				return actions;
			}

			@Override
			public synchronized void persistActions(final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
				for (final ModelAction modelAction : new ArrayList<ModelAction>(actions)) {
					actions.add(modelAction);
				}
			}
		};
	}

	private static PersistenceService getNonWritablePersistenceMock() {
		return new PersistenceService() {

			private final List<ModelAction> actions = new ArrayList<ModelAction>();

			@Override
			public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				return new ProjectSnapshot(ProjectMock.getProject(), new Date());
			}

			@Override
			public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
				return actions;
			}

			@Override
			public void persistActions(final List<ModelAction> actions, final Date timestamp) throws PersistenceException {
				Assert.fail("The persistence should not be accessed.");
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
