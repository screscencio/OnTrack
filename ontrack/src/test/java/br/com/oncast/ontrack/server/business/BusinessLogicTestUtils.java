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

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManagerImpl;
import br.com.oncast.ontrack.server.services.email.FeedbackMailFactory;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.services.threadSync.SyncronizationService;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class BusinessLogicTestUtils {

	private static AuthenticationManager authenticationMock;
	private static AuthorizationManager authorizationMock;
	private static NotificationService notificationMock;
	private static ClientManager clientManagerMock;
	private static SessionManager sessionManager;
	private static FeedbackMailFactory userQuotaRequestMailFactory;
	private static ActionPostProcessingService postProcessingService;
	private static SyncronizationService syncronizationService;

	static {
		configureAuthenticationManager();
		notificationMock = mock(NotificationService.class);
		clientManagerMock = mock(ClientManager.class);
		sessionManager = mock(SessionManager.class);
		postProcessingService = mock(ActionPostProcessingService.class);
		configureAuthorizationMock();
		userQuotaRequestMailFactory = mock(FeedbackMailFactory.class);
		syncronizationService = new SyncronizationService();
	}

	private static void configureAuthorizationMock() {
		try {
			authorizationMock = mock(AuthorizationManagerImpl.class);
			Mockito.doNothing().when(authorizationMock).assureProjectAccessAuthorization(Mockito.any(UUID.class));
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public static BusinessLogic create() throws Exception {
		return new BusinessLogicImpl(getPersistenceMock(), notificationMock, clientManagerMock, authenticationMock, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationMock, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthorizationManager authorizationManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationMock, authorizationManager,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final AuthorizationManager authorizationManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, authorizationManager,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final AuthorizationManager authorizationManager, final SessionManager sessionManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, authorizationManager,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final ProjectAuthorizationMailFactory mailFactory) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationMock, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final ProjectAuthorizationMailFactory mailFactory) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final SessionManager sessionManager) {
		return new BusinessLogicImpl(persistence, notificationMock, clientManagerMock, authenticationManager, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification, final ClientManager clientManager,
			final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notification, clientManager, authenticationManager, authorizationMock, sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification,
			final AuthenticationManager authenticationManager) {
		return new BusinessLogicImpl(persistence, notification, clientManagerMock, authenticationManager, authorizationMock,
				sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager,
			final ActionPostProcessingService postProcessingService) throws Exception {
		return new BusinessLogicImpl(getPersistenceMock(), notificationMock, clientManagerMock, authenticationManager,
				authorizationManager,
				sessionManager, userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic createWithJpaPersistence() {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), notificationMock, clientManagerMock,
				authenticationMock,
				authorizationMock,
				sessionManager, userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic create(final PersistenceService persistence, final NotificationService notification, final ClientManager clientManager,
			final AuthenticationManager authenticationManager, final SessionManager sessionManager) {
		return new BusinessLogicImpl(persistence, notification, clientManager, authenticationManager, authorizationMock, sessionManager,
				userQuotaRequestMailFactory, syncronizationService);
	}

	public static BusinessLogic createWithJpaPersistence(final NotificationService notificationMock) {
		return new BusinessLogicImpl(getPersistenceServiceJpaImplMockingAuthorization(), notificationMock, clientManagerMock,
				authenticationMock,
				authorizationMock,
				sessionManager, userQuotaRequestMailFactory, syncronizationService);
	}

	// TODO Use Mockito.mock instead, after authorization is separated from BusinessLogic.
	private static PersistenceServiceJpaImpl getPersistenceServiceJpaImplMockingAuthorization() {
		return new PersistenceServiceJpaImpl() {
			@Override
			public User retrieveUserById(final long id) throws NoResultFoundException, PersistenceException {
				try {
					return UserTestUtils.createUser(id);
				}
				catch (final Exception e) {
					throw new PersistenceException(e);
				}
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static PersistenceService getPersistenceMock() throws Exception {
		final List<UserAction> actions = new ArrayList<UserAction>();
		final Date snapshotTimestamp = new Date();

		final PersistenceService mock = mock(PersistenceService.class);

		when(mock.retrieveProjectSnapshot(any(UUID.class))).thenReturn(new ProjectSnapshot(ProjectTestUtils.createProject(), snapshotTimestamp));
		when(mock.retrieveActionsSince(any(UUID.class), anyLong())).thenReturn(actions);

		doAnswer(new Answer() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				final List<ModelAction> actions = (List<ModelAction>) invocation.getArguments()[1];
				for (final ModelAction modelAction : new ArrayList<ModelAction>(actions)) {
					actions.add(modelAction);
				}
				return null;
			}
		}).when(mock).persistActions(any(UUID.class), anyList(), anyLong(), any(Date.class));

		when(mock.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenAnswer(new Answer<ProjectRepresentation>() {

			@Override
			public ProjectRepresentation answer(final InvocationOnMock invocation) throws Throwable {
				return (ProjectRepresentation) invocation.getArguments()[0];
			}
		});

		when(mock.retrieveProjectAuthorization(anyLong(), any(UUID.class))).thenReturn(ProjectTestUtils.createAuthorization());

		return mock;
	}

	private static AuthenticationManager configureAuthenticationManager() {
		authenticationMock = mock(AuthenticationManager.class);
		final User user = mock(User.class);
		when(authenticationMock.getAuthenticatedUser()).thenReturn(user);
		return authenticationMock;
	}

}
