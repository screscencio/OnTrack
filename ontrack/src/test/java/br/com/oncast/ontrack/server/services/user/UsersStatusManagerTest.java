package br.com.oncast.ontrack.server.services.user;

import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager.UserStatusChangeListener;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOfflineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOnlineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserStatusEvent;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UsersStatusManagerTest {

	@Mock
	private ClientManager clientManager;

	@Mock
	private MulticastService multicastService;

	@Mock
	private AuthorizationManager authorizationManager;

	private Set<UUID> users;
	private UsersStatusManager usersStatusManager;
	private UserStatusChangeListener userStatusChangeListener;

	private UUID userId1;
	private UUID userId2;

	private UUID project1;
	private UUID project2;

	private List<ProjectRepresentation> user1AuthorizedProjects;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		project1 = new UUID();
		project2 = new UUID();

		user1AuthorizedProjects = new ArrayList<ProjectRepresentation>();
		user1AuthorizedProjects.add(ProjectTestUtils.createRepresentation(project1));
		user1AuthorizedProjects.add(ProjectTestUtils.createRepresentation(project2));

		userId1 = new UUID();
		userId2 = new UUID();

		users = new HashSet<UUID>();
		users.add(userId1);
		users.add(userId2);
		users.add(new UUID());

		final ArgumentCaptor<UserStatusChangeListener> captor = ArgumentCaptor.forClass(UserStatusChangeListener.class);
		doNothing().when(clientManager).addUserStatusChangeListener(captor.capture());

		usersStatusManager = new UsersStatusManager(clientManager, multicastService, authorizationManager);

		userStatusChangeListener = captor.getValue();

		when(authorizationManager.listAuthorizedProjects(userId1)).thenReturn(user1AuthorizedProjects);
		when(authorizationManager.hasAuthorizationFor(any(UUID.class), any(UUID.class))).thenReturn(false);
	}

	@Test
	public void shouldMulticastUserOnlineEventToAllUsersInAllAuthorizedProjectsWhenAUserGetsOnline() throws Exception {
		userStatusChangeListener.onUserOnline(userId1);

		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
	}

	@Test
	public void shouldNotMulticastUserOnlineEventMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOnline(userId1);

		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);

		for (int i = 0; i < 8; i++) {
			userStatusChangeListener.onUserOnline(userId1);
		}

		verifyNoMoreInteractions(multicastService);
	}

	@Test
	public void shouldMulticastUserOfflineEventToAllUsersInAllAuthorizedProjectsWhenAUserGetsOffline() throws Exception {
		userStatusChangeListener.onUserOnline(userId1);
		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
		reset(multicastService);

		userStatusChangeListener.onUserOffline(userId1);
		verifyForAllAuthorizedProjectsForUser1(UserOfflineEvent.class);
	}

	@Test
	public void shouldNotMulticastUserOfflineEventMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOnline(userId1);
		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
		reset(multicastService);

		userStatusChangeListener.onUserOffline(userId1);
		verifyForAllAuthorizedProjectsForUser1(UserOfflineEvent.class);

		for (int i = 0; i < 8; i++) {
			userStatusChangeListener.onUserOffline(userId1);
		}

		verifyNoMoreInteractions(multicastService);
	}

	@Test
	public void shouldBeAbleToRetrieveOnlineUsersThatHasAuthorizationForAGivenProject() throws Exception {
		when(clientManager.getOnlineUsers()).thenReturn(users);

		when(authorizationManager.hasAuthorizationFor(userId1, project1)).thenReturn(true);
		when(authorizationManager.hasAuthorizationFor(userId2, project1)).thenReturn(true);
		when(authorizationManager.hasAuthorizationFor(userId2, project2)).thenReturn(true);

		final HashSet<UUID> authorizedUsers = new HashSet<UUID>();
		authorizedUsers.add(userId1);
		authorizedUsers.add(userId2);

		assertEquals(authorizedUsers, usersStatusManager.getOnlineUsers(project1));
	}

	@Test
	public void shouldBeAbleToRetrieveActiveUsers() throws Exception {
		when(clientManager.getUsersAtProject(any(UUID.class))).thenReturn(users);

		assertEquals(users, usersStatusManager.getUsersAtProject(new UUID()));
	}

	@Test
	public void shouldMulticastProjectWhenAUserOpenAProject() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userId1);

		verify(multicastService).multicastToAllUsersInSpecificProject(argThat(hasUserId(UserOpenProjectEvent.class, userId1)),
				eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserOpenTheSameProjectMoreThanOnce() throws Exception {
		for (int i = 0; i < 10; i++) {
			userStatusChangeListener.onUserOpenProject(project1, userId1);
		}

		verify(multicastService).multicastToAllUsersInSpecificProject(argThat(hasUserId(UserOpenProjectEvent.class, userId1)),
				eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserClosesAProjectThatWasNotOpenedBefore() throws Exception {
		userStatusChangeListener.onUserCloseProject(project1, userId1);

		verify(multicastService, never())
				.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserId(UserClosedProjectEvent.class, userId1)), eq(project1));
	}

	@Test
	public void shouldMulticastProjectWhenAUserClosesAProject() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userId1);
		reset(multicastService);

		userStatusChangeListener.onUserCloseProject(project1, userId1);

		verify(multicastService)
				.multicastToAllUsersInSpecificProject(argThat(hasUserId(UserClosedProjectEvent.class, userId1)), eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserClosesAProjectMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userId1);
		reset(multicastService);

		for (int i = 0; i < 10; i++) {
			userStatusChangeListener.onUserCloseProject(project1, userId1);
		}

		verify(multicastService)
				.multicastToAllUsersInSpecificProject(argThat(hasUserId(UserClosedProjectEvent.class, userId1)), eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectUntilTheLastUserClosesTheProject() throws Exception {
		final int timesThatAUserOpenedAProject = 7;
		for (int i = 0; i < timesThatAUserOpenedAProject; i++) {
			userStatusChangeListener.onUserOpenProject(project1, userId1);
		}
		reset(multicastService);

		for (int i = 0; i < timesThatAUserOpenedAProject; i++) {
			verify(multicastService, never())
					.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserId(UserClosedProjectEvent.class, userId1)), eq(project1));
			userStatusChangeListener.onUserCloseProject(project1, userId1);
		}

		verify(multicastService)
				.multicastToAllUsersInSpecificProject(argThat(hasUserId(UserClosedProjectEvent.class, userId1)), eq(project1));
	}

	private void verifyForAllAuthorizedProjectsForUser1(final Class<? extends UserStatusEvent> clazz) {
		verify(multicastService).multicastToAllProjectsInUserAuthorizationList(argThat(hasUserId(clazz, userId1)),
				eq(user1AuthorizedProjects));
	}

	private <T extends UserStatusEvent> Matcher<T> hasUserId(final Class<T> class1, final UUID userId) {
		assert userId != null;

		return new ArgumentMatcher<T>() {
			@Override
			public boolean matches(final Object argument) {
				final UserStatusEvent event = (UserStatusEvent) argument;
				return userId.equals(event.getUserId());
			}
		};
	}

}
