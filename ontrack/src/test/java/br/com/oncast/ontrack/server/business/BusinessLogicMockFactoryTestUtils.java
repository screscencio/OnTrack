package br.com.oncast.ontrack.server.business;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class BusinessLogicMockFactoryTestUtils {

	public static BusinessLogic createWithJpaPersistenceAndCustomBroadcastMock(final MulticastService broadcastMock) {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), broadcastMock, getClientManagerMock(), getAuthManagerMock());
	}

	public static BusinessLogic createWithDumbPersistenceMockAndDumbBroadcastMock() throws Exception {
		return new BusinessLogicImpl(getPersistenceMock(), getBroadcastMock(), getClientManagerMock(), getAuthManagerMock());
	}

	public static BusinessLogic createWithDumbNonWritablePersistenceMockAndDumbBroadcastMock() throws Exception {
		return new BusinessLogicImpl(getNonWritablePersistenceMock(), getBroadcastMock(), getClientManagerMock(), getAuthManagerMock());
	}

	public static BusinessLogic createWithJpaPersistenceAndDumbBroadcastMock() {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), getBroadcastMock(), getClientManagerMock(), getAuthManagerMock());
	}

	public static BusinessLogic createWithCustomPersistenceMockAndDumbBroadcastMockAndCustomAuthManagerMock(final PersistenceService persistenceService,
			final AuthenticationManager authManager) {
		return new BusinessLogicImpl(persistenceService, getBroadcastMock(), getClientManagerMock(), authManager);
	}

	private static PersistenceServiceJpaImpl getPersistenceServiceJpaImplMockingAuthorization() {
		return new PersistenceServiceJpaImpl() {
			@Override
			public ProjectAuthorization retrieveProjectAuthorization(final long userId, final long projectId) throws PersistenceException {
				return new ProjectAuthorization(null, null);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "hiding" })
	private static PersistenceService getPersistenceMock() throws Exception {
		final List<UserAction> actions = new ArrayList<UserAction>();
		final Date snapshotTimestamp = new Date();

		final PersistenceService mock = Mockito.mock(PersistenceService.class);

		when(mock.retrieveProjectSnapshot(Mockito.anyLong())).thenReturn(new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp));
		when(mock.retrieveActionsSince(Mockito.anyLong(), Mockito.anyLong())).thenReturn(actions);

		doAnswer(new Answer() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				final List<ModelAction> actions = (List<ModelAction>) invocation.getArguments()[1];
				for (final ModelAction modelAction : new ArrayList<ModelAction>(actions)) {
					actions.add(modelAction);
				}
				return null;
			}
		}).when(mock).persistActions(Mockito.anyLong(), Mockito.anyList(), Mockito.any(Date.class));

		when(mock.persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class))).thenAnswer(new Answer<ProjectRepresentation>() {

			@Override
			public ProjectRepresentation answer(final InvocationOnMock invocation) throws Throwable {
				return (ProjectRepresentation) invocation.getArguments()[0];
			}
		});

		when(mock.retrieveProjectAuthorization(Mockito.anyLong(), Mockito.anyLong())).thenReturn(ProjectTestUtils.createAuthorization());

		return mock;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static PersistenceService getNonWritablePersistenceMock() throws Exception {
		final List<UserAction> actions = new ArrayList<UserAction>();
		final Date snapshotTimestamp = new Date();

		final PersistenceService mock = Mockito.mock(PersistenceService.class);

		when(mock.retrieveProjectSnapshot(Mockito.anyLong())).thenReturn(new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp));
		when(mock.retrieveActionsSince(Mockito.anyLong(), Mockito.anyLong())).thenReturn(actions);

		doAnswer(new Answer() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				Assert.fail("The persistence should not be accessed.");
				return null;
			}
		}).when(mock).persistActions(Mockito.anyLong(), Mockito.anyList(), Mockito.any(Date.class));

		when(mock.persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class))).thenAnswer(new Answer<ProjectRepresentation>() {

			@Override
			public ProjectRepresentation answer(final InvocationOnMock invocation) throws Throwable {
				return (ProjectRepresentation) invocation.getArguments()[0];
			}
		});

		when(mock.retrieveProjectAuthorization(Mockito.anyLong(), Mockito.anyLong())).thenReturn(ProjectTestUtils.createAuthorization());

		return mock;

	}

	private static MulticastService getBroadcastMock() {
		return new MulticastService() {
			@Override
			public void multicastActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) {}

			@Override
			public void broadcastProjectCreation(final ProjectRepresentation projectRepresentation) {}
		};
	}

	private static ClientManager getClientManagerMock() {
		return Mockito.mock(ClientManager.class);
	}

	private static AuthenticationManager getAuthManagerMock() {
		final AuthenticationManager auth = Mockito.mock(AuthenticationManager.class);
		final User user = new User("user@domain.com");
		user.setId(1);
		Mockito.when(auth.getAuthenticatedUser()).thenReturn(user);
		return auth;
	}

}
