package br.com.oncast.ontrack.server.services.user;

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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class UsersStatusManagerTest {

	@Mock
	private ClientManager clientManager;

	@Mock
	private MulticastService multicastService;

	@Mock
	private AuthorizationManager authorizationManager;

	private SortedSet<String> users;
	private UsersStatusManager usersStatusManager;
	private UserStatusChangeListener userStatusChangeListener;
	private String userEmail1;

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

		userEmail1 = "user1";

		users = new TreeSet<String>();
		users.add(userEmail1);
		users.add("user2");
		users.add("user3");

		final ArgumentCaptor<UserStatusChangeListener> captor = ArgumentCaptor.forClass(UserStatusChangeListener.class);
		doNothing().when(clientManager).addUserStatusChangeListener(captor.capture());

		usersStatusManager = new UsersStatusManager(clientManager, multicastService, authorizationManager);

		userStatusChangeListener = captor.getValue();

		when(authorizationManager.listAuthorizedProjects(userEmail1)).thenReturn(user1AuthorizedProjects);
	}

	@Test
	public void shouldMulticastUserOnlineEventToAllUsersInAllAuthorizedProjectsWhenAUserGetsOnline() throws Exception {
		userStatusChangeListener.onUserOnline(userEmail1);

		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
	}

	@Test
	public void shouldNotMulticastUserOnlineEventMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOnline(userEmail1);

		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);

		for (int i = 0; i < 8; i++) {
			userStatusChangeListener.onUserOnline(userEmail1);
		}

		verifyNoMoreInteractions(multicastService);
	}

	@Test
	public void shouldMulticastUserOfflineEventToAllUsersInAllAuthorizedProjectsWhenAUserGetsOffline() throws Exception {
		userStatusChangeListener.onUserOnline(userEmail1);
		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
		reset(multicastService);

		userStatusChangeListener.onUserOffline(userEmail1);
		verifyForAllAuthorizedProjectsForUser1(UserOfflineEvent.class);
	}

	@Test
	public void shouldNotMulticastUserOfflineEventMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOnline(userEmail1);
		verifyForAllAuthorizedProjectsForUser1(UserOnlineEvent.class);
		reset(multicastService);

		userStatusChangeListener.onUserOffline(userEmail1);
		verifyForAllAuthorizedProjectsForUser1(UserOfflineEvent.class);

		for (int i = 0; i < 8; i++) {
			userStatusChangeListener.onUserOffline(userEmail1);
		}

		verifyNoMoreInteractions(multicastService);
	}

	@Test
	public void shouldBeAbleToRetrieveAllOnlineUsers() throws Exception {
		when(clientManager.getOnlineUsers()).thenReturn(users);

		assertEquals(users, usersStatusManager.getOnlineUsers());
	}

	@Test
	public void shouldBeAbleToRetrieveActiveUsers() throws Exception {
		when(clientManager.getUsersAtProject(any(UUID.class))).thenReturn(users);

		assertEquals(users, usersStatusManager.getUsersAtProject(new UUID()));
	}

	@Test
	public void shouldMulticastProjectWhenAUserOpenAProject() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userEmail1);

		verify(multicastService).multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserOpenProjectEvent.class, userEmail1)),
				eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserOpenTheSameProjectMoreThanOnce() throws Exception {
		for (int i = 0; i < 10; i++) {
			userStatusChangeListener.onUserOpenProject(project1, userEmail1);
		}

		verify(multicastService).multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserOpenProjectEvent.class, userEmail1)),
				eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserClosesAProjectThatWasNotOpenedBefore() throws Exception {
		userStatusChangeListener.onUserCloseProject(project1, userEmail1);

		verify(multicastService, never())
				.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserClosedProjectEvent.class, userEmail1)), eq(project1));
	}

	@Test
	public void shouldMulticastProjectWhenAUserClosesAProject() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userEmail1);
		reset(multicastService);

		userStatusChangeListener.onUserCloseProject(project1, userEmail1);

		verify(multicastService)
				.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserClosedProjectEvent.class, userEmail1)), eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectWhenAUserClosesAProjectMoreThanOnce() throws Exception {
		userStatusChangeListener.onUserOpenProject(project1, userEmail1);
		reset(multicastService);

		for (int i = 0; i < 10; i++) {
			userStatusChangeListener.onUserCloseProject(project1, userEmail1);
		}

		verify(multicastService)
				.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserClosedProjectEvent.class, userEmail1)), eq(project1));
	}

	@Test
	public void shouldNotMulticastProjectUntilTheLastUserClosesTheProject() throws Exception {
		final int timesThatAUserOpenedAProject = 7;
		for (int i = 0; i < timesThatAUserOpenedAProject; i++) {
			userStatusChangeListener.onUserOpenProject(project1, userEmail1);
		}
		reset(multicastService);

		for (int i = 0; i < timesThatAUserOpenedAProject; i++) {
			verify(multicastService, never())
					.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserClosedProjectEvent.class, userEmail1)), eq(project1));
			userStatusChangeListener.onUserCloseProject(project1, userEmail1);
		}

		verify(multicastService)
				.multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(UserClosedProjectEvent.class, userEmail1)), eq(project1));
	}

	private void verifyForAllAuthorizedProjectsForUser1(final Class<? extends UserStatusEvent> clazz) {
		for (final ProjectRepresentation r : user1AuthorizedProjects) {
			verify(multicastService).multicastToAllUsersButCurrentUserClientInSpecificProject(argThat(hasUserEmail(clazz, userEmail1)),
					eq(r.getId()));
		}
	}

	private <T extends UserStatusEvent> Matcher<T> hasUserEmail(final Class<T> class1, final String userEmail) {
		assert userEmail != null;

		return new ArgumentMatcher<T>() {
			@Override
			public boolean matches(final Object argument) {
				final UserStatusEvent event = (UserStatusEvent) argument;
				return userEmail.equals(event.getUserEmail());
			}
		};
	}

}
