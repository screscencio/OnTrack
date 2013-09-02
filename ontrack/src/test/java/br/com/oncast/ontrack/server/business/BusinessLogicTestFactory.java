package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.business.actionPostProcessments.ActionPostProcessmentsInitializer;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
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
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BusinessLogicTestFactory {

	private AuthenticationManager authentication;
	private AuthorizationManager authorization;
	private MulticastService multicast;
	private ClientManager clientManager;
	private SessionManager sessionManager;
	private MailFactory mailFactory;
	private SyncronizationService syncronizationService;
	private ActionPostProcessmentsInitializer postProcessments;
	private PersistenceService persistence;
	private final IntegrationService integration;

	private BusinessLogicTestFactory() {
		authentication = getAuthenticationMock();
		multicast = mock(MulticastService.class);
		clientManager = mock(ClientManager.class);
		sessionManager = mock(SessionManager.class);
		authorization = getAuthorizationMock();
		mailFactory = mock(MailFactory.class);
		postProcessments = mock(ActionPostProcessmentsInitializer.class);
		syncronizationService = new SyncronizationService();
		persistence = getPersistenceMock();
		integration = mock(IntegrationService.class);
	}

	public static BusinessLogic createDefault() {
		return businessLogic().create();
	}

	public static BusinessLogicTestFactory businessLogic() {
		return new BusinessLogicTestFactory();
	}

	public static BusinessLogic create(final BusinessLogicTestFactory factory) {
		return factory.create();
	}

	public BusinessLogic create() {
		try {
			return new BusinessLogicImpl(persistence, multicast, clientManager, authentication, authorization, sessionManager, mailFactory, syncronizationService, postProcessments, integration);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public BusinessLogicTestFactory with(final PersistenceService persistenceService) {
		persistence = persistenceService;
		return this;
	}

	public BusinessLogicTestFactory with(final AuthenticationManager authentication) {
		this.authentication = authentication;
		return this;
	}

	public BusinessLogicTestFactory with(final AuthorizationManager authorization) {
		this.authorization = authorization;
		return this;
	}

	public BusinessLogicTestFactory with(final MulticastService multicast) {
		this.multicast = multicast;
		return this;
	}

	public BusinessLogicTestFactory with(final ClientManager clientManager) {
		this.clientManager = clientManager;
		return this;
	}

	public BusinessLogicTestFactory with(final SessionManager sessionManager) {
		this.sessionManager = sessionManager;
		return this;
	}

	public BusinessLogicTestFactory with(final MailFactory mailFactory) {
		this.mailFactory = mailFactory;
		return this;
	}

	public BusinessLogicTestFactory with(final SyncronizationService syncronizationService) {
		this.syncronizationService = syncronizationService;
		return this;
	}

	public BusinessLogicTestFactory with(final ActionPostProcessmentsInitializer postProcessments) {
		this.postProcessments = postProcessments;
		return this;
	}

	public BusinessLogicTestFactory with(final ActionPostProcessingService actionPostProcessingService) {
		this.postProcessments = new ActionPostProcessmentsInitializer(actionPostProcessingService, persistence, multicast, Mockito.mock(NotificationServerService.class));
		return this;
	}

	public static BusinessLogic create(final PersistenceService persistence) {
		return create(businessLogic().with(persistence));
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthorizationManager authorizationManager) {
		return create(businessLogic().with(persistence).with(authorizationManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(authorizationManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager,
			final SessionManager sessionManager) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(authorizationManager).with(sessionManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final MailFactory mailFactory) {
		return create(businessLogic().with(persistence).with(mailFactory));

	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final MailFactory mailFactory) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(mailFactory));
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager) {
		return create(businessLogic().with(persistence).with(authenticationManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final SessionManager sessionManager) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(sessionManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final MulticastService multicast, final ClientManager clientManager, final AuthenticationManager authenticationManager) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(multicast).with(clientManager));
	}

	public static BusinessLogic create(final PersistenceService persistence, final MulticastService multicast, final AuthenticationManager authenticationManager) {
		return create(businessLogic().with(persistence).with(authenticationManager).with(multicast));
	}

	public static BusinessLogic create(final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager, final ActionPostProcessingService postProcessingService)
			throws Exception {
		return create(businessLogic().with(authorizationManager).with(authenticationManager).with(postProcessingService));
	}

	public static BusinessLogic createWithJpaPersistence() {
		return create(businessLogic().with(getJpaPersitenceMockingUsers()));
	}

	public static BusinessLogic create(final PersistenceService persistence, final MulticastService multicast, final ClientManager clientManager, final AuthenticationManager authenticationManager,
			final SessionManager sessionManager) {
		return create(businessLogic().with(persistence).with(multicast).with(clientManager).with(authenticationManager).with(sessionManager));
	}

	public static BusinessLogic createWithJpaPersistence(final MulticastService multicastMock) {
		return create(businessLogic().with(getJpaPersitenceMockingUsers()).with(multicastMock));
	}

	// TODO Use Mockito.mock instead, after authorization is separated from BusinessLogic.
	public static PersistenceServiceJpaImpl getJpaPersitenceMockingUsers() {
		return new PersistenceServiceJpaImpl() {
			@Override
			public User retrieveUserById(final UUID id) throws NoResultFoundException, PersistenceException {
				try {
					if (id.equals(UserTestUtils.getAdmin().getId())) return UserTestUtils.getAdmin();
					return UserTestUtils.createUser(id);
				} catch (final Exception e) {
					throw new PersistenceException(e);
				}
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private PersistenceService getPersistenceMock() {
		try {
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
			}).when(mock).persistActions(any(UUID.class), anyList(), any(UUID.class), any(Date.class));

			when(mock.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenAnswer(new Answer<ProjectRepresentation>() {

				@Override
				public ProjectRepresentation answer(final InvocationOnMock invocation) throws Throwable {
					return (ProjectRepresentation) invocation.getArguments()[0];
				}
			});

			when(mock.retrieveProjectAuthorization(any(UUID.class), any(UUID.class))).thenReturn(ProjectTestUtils.createAuthorization());
			return mock;

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private AuthenticationManager getAuthenticationMock() {
		final AuthenticationManager authenticationMock = mock(AuthenticationManager.class);
		final User user = UserTestUtils.getAdmin();
		when(authenticationMock.getAuthenticatedUser()).thenReturn(user);
		return authenticationMock;
	}

	private AuthorizationManager getAuthorizationMock() {
		try {
			final AuthorizationManager authorizationMock = mock(AuthorizationManager.class);
			when(authorizationMock.authorize(any(UUID.class), anyString(), anyBoolean(), anyBoolean())).thenReturn(new UUID());
			Mockito.doNothing().when(authorizationMock).assureActiveProjectAccessAuthorization(Mockito.any(UUID.class));
			return authorizationMock;
		} catch (final Exception e) {
			throw new RuntimeException();
		}
	}

}
