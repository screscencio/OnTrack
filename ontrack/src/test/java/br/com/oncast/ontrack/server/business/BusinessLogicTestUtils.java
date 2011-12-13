package br.com.oncast.ontrack.server.business;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class BusinessLogicTestUtils {

	private static AuthenticationManager authenticationMock;
	private static NotificationService notificationMock;
	private static ClientManager clientManagerMock;
	private static SessionManager sessionManager;

	static {
		configureAuthenticationManager();
		notificationMock = mock(NotificationService.class);
		clientManagerMock = mock(ClientManager.class);
		sessionManager = mock(SessionManager.class);
	}

	public static BusinessLogic create() throws Exception {
		return new BusinessLogicImpl(getPersistenceMock(), notificationMock, clientManagerMock, authenticationMock, sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationMock, sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final SessionManager sessionManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification, final ClientManager clientManager,
			final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notification, clientManager, authenticationManager, sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification,
			final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notification, clientManagerMock, authenticationManager, sessionManager);
	}

	public static BusinessLogic createWithJpaPersistence() {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), notificationMock, clientManagerMock, authenticationMock,
				sessionManager);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification, final ClientManager clientManager,
			final AuthenticationManager authenticationManager, final SessionManager sessionManager) {
		return new BusinessLogicImpl(persistence, notification, clientManager, authenticationManager, sessionManager);
	}

	public static BusinessLogic createWithJpaPersistence(final NotificationService notificationMock) {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), notificationMock, clientManagerMock, authenticationMock,
				sessionManager);
	}

	// TODO Use Mockito.mock instead, after authorization is separated from BusinessLogic.
	private static PersistenceServiceJpaImpl getPersistenceServiceJpaImplMockingAuthorization() {
		return new PersistenceServiceJpaImpl() {
			@Override
			public ProjectAuthorization retrieveProjectAuthorization(final long userId, final long projectId) throws PersistenceException {
				return new ProjectAuthorization(null, null);
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static PersistenceService getPersistenceMock() throws Exception {
		final List<UserAction> actions = new ArrayList<UserAction>();
		final Date snapshotTimestamp = new Date();

		final PersistenceService mock = mock(PersistenceService.class);

		when(mock.retrieveProjectSnapshot(anyLong())).thenReturn(new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp));
		when(mock.retrieveActionsSince(anyLong(), anyLong())).thenReturn(actions);

		doAnswer(new Answer() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				final List<ModelAction> actions = (List<ModelAction>) invocation.getArguments()[1];
				for (final ModelAction modelAction : new ArrayList<ModelAction>(actions)) {
					actions.add(modelAction);
				}
				return null;
			}
		}).when(mock).persistActions(anyLong(), anyList(), any(Date.class));

		when(mock.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenAnswer(new Answer<ProjectRepresentation>() {

			@Override
			public ProjectRepresentation answer(final InvocationOnMock invocation) throws Throwable {
				return (ProjectRepresentation) invocation.getArguments()[0];
			}
		});

		when(mock.retrieveProjectAuthorization(anyLong(), anyLong())).thenReturn(ProjectTestUtils.createAuthorization());

		return mock;
	}

	private static AuthenticationManager configureAuthenticationManager() {
		authenticationMock = mock(AuthenticationManager.class);
		final User user = mock(User.class);
		when(authenticationMock.getAuthenticatedUser()).thenReturn(user);
		return authenticationMock;
	}

}
