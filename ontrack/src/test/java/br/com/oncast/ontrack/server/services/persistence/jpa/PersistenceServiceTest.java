package br.com.oncast.ontrack.server.services.persistence.jpa;

import static br.com.oncast.ontrack.utils.ListUtils.lastOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class PersistenceServiceTest {

	private static final UUID PROJECT_ID = new UUID();

	private static final long USER_ID = 1;

	private PersistenceService persistenceService;

	private EntityManager entityManager;

	@Before
	public void before() throws Exception {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		persistenceService = new PersistenceServiceJpaImpl();
		assureProjectRepresentationExistance();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void shouldOnlyReturnActionsAfterAGivenId() throws Exception {
		persistenceService.persistActions(PROJECT_ID, ActionTestUtils.createSomeActions(), USER_ID, new Date());

		final List<UserAction> userActions = persistenceService.retrieveActionsSince(PROJECT_ID, 0);

		final List<ModelAction> secondWaveOfActions = ActionTestUtils.getActions2();
		persistenceService.persistActions(PROJECT_ID, secondWaveOfActions, USER_ID, new Date());

		final List<UserAction> actionsReceived = persistenceService.retrieveActionsSince(PROJECT_ID, userActions.get(userActions.size() - 1).getId());
		assertEquals(secondWaveOfActions.size(), actionsReceived.size());

		for (int i = 0; i < secondWaveOfActions.size(); i++) {
			assertEquals(secondWaveOfActions.get(i).getReferenceId(), actionsReceived.get(i).getModelAction().getReferenceId());
		}
	}

	@Test
	public void shouldRetrieveSnapshotAfterExecutingActionAndPersistingIt() throws Exception {
		final ProjectSnapshot snapshot1 = loadProjectSnapshot();
		final Project project1 = snapshot1.getProject();

		final ActionContext actionContext = Mockito.mock(ActionContext.class);
		when(actionContext.getUserEmail()).thenReturn(DefaultAuthenticationCredentials.USER_EMAIL);
		when(actionContext.getTimestamp()).thenReturn(new Date(Long.MAX_VALUE));
		final ProjectContext context = new ProjectContext(project1);
		context.addUser(UserTestUtils.getAdmin());
		new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son").execute(context, actionContext);

		snapshot1.setProject(project1);
		snapshot1.setTimestamp(new Date());
		persistenceService.persistProjectSnapshot(snapshot1);

		final ProjectSnapshot snapshot2 = loadProjectSnapshot();
		final Project project2 = snapshot2.getProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void shouldRetrieveSnapshotWithCorrectProgressStartDateAndEndDate() throws Exception {
		final ProjectSnapshot snapshot1 = loadProjectSnapshot();
		final Project project1 = snapshot1.getProject();

		final WorkingDay startDate = WorkingDayFactory.create(2011, Calendar.OCTOBER, 3);
		final WorkingDay endDate = WorkingDayFactory.create(2011, Calendar.OCTOBER, 5);

		ScopeTestUtils.setStartDate(project1.getProjectScope(), startDate);
		ScopeTestUtils.setEndDate(project1.getProjectScope(), endDate);

		snapshot1.setProject(project1);
		snapshot1.setTimestamp(new Date());
		persistenceService.persistProjectSnapshot(snapshot1);

		final ProjectSnapshot snapshot2 = loadProjectSnapshot();
		final Project project2 = snapshot2.getProject();
		final Progress progress = project2.getProjectScope().getProgress();

		assertEquals(startDate, progress.getStartDay());
		assertEquals(endDate, progress.getEndDay());
	}

	@Test
	public void shouldPersistUser() throws PersistenceException, NoResultFoundException {
		final User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		final User newUser = persistenceService.retrieveUserByEmail(email);
		assertEquals(user.getEmail(), newUser.getEmail());
	}

	@Test
	public void shouldUpdateUser() throws PersistenceException, NoResultFoundException {
		final User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		final User newUser = persistenceService.retrieveUserByEmail(email);
		assertEquals(user.getEmail(), newUser.getEmail());

		final String newEmail = "newEmail@email.com";
		newUser.setEmail(newEmail);
		persistenceService.persistOrUpdateUser(newUser);
		final User updatedUser = persistenceService.retrieveUserByEmail(newEmail);
		assertEquals(newEmail, updatedUser.getEmail());
		assertEquals(newUser.getId(), updatedUser.getId());
	}

	@Test
	public void shouldPersistPasswordForAnExistentUser() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.retrieveUserByEmail(email);
		final Password password = new Password();
		password.setUserId(user.getId());
		final String passwordText = "password";
		password.setPassword(passwordText);
		persistenceService.persistOrUpdatePassword(password);
		final Password newPassword = persistenceService.retrievePasswordForUser(user.getId());
		assertTrue(newPassword.authenticate(passwordText));
	}

	@Test
	public void shouldUpdateAndExistentPassword() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.retrieveUserByEmail(email);
		final Password password = new Password();
		password.setUserId(user.getId());
		final String passwordText = "password";
		password.setPassword(passwordText);
		persistenceService.persistOrUpdatePassword(password);
		final Password firstPassword = persistenceService.retrievePasswordForUser(user.getId());
		assertTrue(firstPassword.authenticate(passwordText));

		final String newPassword = "newPassword";
		firstPassword.setPassword(newPassword);
		persistenceService.persistOrUpdatePassword(firstPassword);

		final Password secondPassword = persistenceService.retrievePasswordForUser(user.getId());
		assertFalse(secondPassword.authenticate(passwordText));
		assertTrue(secondPassword.authenticate(newPassword));
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNoResultFoundExceptionWhenUserDontHaveAPassword() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.retrieveUserByEmail(email);
		persistenceService.retrievePasswordForUser(user.getId());
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNoResultFoundExceptionWhenUserNotExist() throws PersistenceException, NoResultFoundException {
		persistenceService.retrievePasswordForUser(213);
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNotResultFoundExceptionWhenUserNotFound() throws NoResultFoundException, PersistenceException {
		persistenceService.retrieveUserByEmail("inexistant@email.com");
	}

	@Test
	public void shouldRetrieveAllUsers() throws Exception {
		final User user1 = new User();
		user1.setEmail("user@user1");
		persistenceService.persistOrUpdateUser(user1);
		final User user2 = new User();
		user2.setEmail("user@user2");
		persistenceService.persistOrUpdateUser(user2);
		final User user3 = new User();
		user3.setEmail("user@user3");
		persistenceService.persistOrUpdateUser(user3);
		final User user4 = new User();
		user4.setEmail("user@user4");
		persistenceService.persistOrUpdateUser(user4);

		final List<User> userList = persistenceService.retrieveAllUsers();
		assertEquals(4, userList.size());

	}

	@Test
	public void shouldPersistProjectRepresentation() throws Exception {
		final ProjectRepresentation projectRepresentation = new ProjectRepresentation("Name");
		persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation);
		final ProjectRepresentation foundProjectRepresentation = persistenceService.retrieveProjectRepresentation(projectRepresentation.getId());
		assertEquals("Name", foundProjectRepresentation.getName());
	}

	public void thePersistedProjectRepresentationShouldKeepTheGivenId() throws Exception {
		final ProjectRepresentation projectRepresentation = new ProjectRepresentation("Name");

		persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation);

		final ProjectRepresentation foundProjectRepresentation = persistenceService.retrieveProjectRepresentation(projectRepresentation.getId());
		assertEquals("Name", foundProjectRepresentation.getName());
		assertEquals(projectRepresentation.getId(), foundProjectRepresentation.getId());
	}

	@Test
	public void shouldBeAbleToFindAllProjectRepresentations() throws Exception {
		assertEquals(1, persistenceService.retrieveAllProjectRepresentations().size());

		final ArrayList<ProjectRepresentation> projectRepresentations = new ArrayList<ProjectRepresentation>();
		for (int i = 2; i <= 11; i++) {
			final ProjectRepresentation projectRepresentation = new ProjectRepresentation(new UUID(), "Name" + i);
			persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation);
			projectRepresentations.add(projectRepresentation);
			assertEquals(i, persistenceService.retrieveAllProjectRepresentations().size());
		}
		assertTrue(persistenceService.retrieveAllProjectRepresentations().containsAll(projectRepresentations));
	}

	@Test
	public void shouldBeAbleToFindAllProjectAuthorizationsThatAGivenUserIsRelatedTo() throws Exception {
		final User user = createAndPersistUser();
		final ProjectRepresentation project1 = createProjectRepresentation("project1");
		createProjectRepresentation("project2");
		final ProjectRepresentation project3 = createProjectRepresentation("project3");

		authorize(user, project1, project3);

		final List<ProjectAuthorization> authorizations = persistenceService.retrieveProjectAuthorizations(user.getId());

		final List<ProjectRepresentation> obtainedProjectAuthorizations = extractProjectsFromAuthorization(authorizations);
		assertTrue(obtainedProjectAuthorizations.contains(project1));
		assertTrue(obtainedProjectAuthorizations.contains(project3));
	}

	@Test
	public void shouldBeAbleToRetrieveProjectAuthorizationForSpecificUserAndProject() throws Exception {
		final User user1 = createAndPersistUser();
		final User user2 = createAndPersistUser();
		final ProjectRepresentation project1 = createProjectRepresentation("project1");
		final ProjectRepresentation project2 = createProjectRepresentation("project2");

		authorize(user1, project1);
		authorize(user2, project2);

		assertNotNull(persistenceService.retrieveProjectAuthorization(user1.getId(), project1.getId()));
		assertNotNull(persistenceService.retrieveProjectAuthorization(user2.getId(), project2.getId()));

		assertNull(persistenceService.retrieveProjectAuthorization(user1.getId(), project2.getId()));
		assertNull(persistenceService.retrieveProjectAuthorization(user2.getId(), project1.getId()));
	}

	private List<ProjectRepresentation> extractProjectsFromAuthorization(final List<ProjectAuthorization> authorizations) throws PersistenceException,
			NoResultFoundException {
		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();
		for (final ProjectAuthorization authorization : authorizations) {
			projects.add(persistenceService.retrieveProjectRepresentation(authorization.getProjectId()));
		}
		return projects;
	}

	@Test(expected = PersistenceException.class)
	public void inexistentUserCannotBeAuthorized() throws Exception {
		persistenceService.authorize("inexistent@email.com", PROJECT_ID);
	}

	@Test(expected = PersistenceException.class)
	public void inexistentProjectCannotBeAuthorized() throws Exception {
		final User user = createAndPersistUser();
		persistenceService.authorize(user.getEmail(), new UUID());
	}

	@Test
	public void cannotAuthorizeAUserToProjectTwice() throws Exception {
		final User user = createAndPersistUser();
		final ProjectRepresentation project = persistenceService.retrieveProjectRepresentation(PROJECT_ID);

		boolean reached = false;
		try {
			persistenceService.authorize(user.getEmail(), project.getId());
			reached = true;
			persistenceService.authorize(user.getEmail(), project.getId());
			fail();
		}
		catch (final PersistenceException e) {
			assertTrue(reached);
		}

	}

	@Test
	public void userIdShouldBeBoundToUserActionCotainer() throws Exception {
		final long userId = 123;
		persistenceService.persistActions(PROJECT_ID, ActionTestUtils.createSomeActions(), userId, new Date());
		final List<UserAction> retrievedActions = persistenceService.retrieveActionsSince(PROJECT_ID, 0);

		assertEquals(userId, lastOf(retrievedActions).getUserId());
	}

	@Test
	@Ignore("Run only when you want to generate data to test the release burn up chart")
	public void generateDataForBurnUp() throws Exception {
		final ProjectSnapshot snapshot1 = loadProjectSnapshot();
		final Project project1 = snapshot1.getProject();
		ScopeTestUtils.populateWithTestData(project1);

		snapshot1.setProject(project1);
		snapshot1.setTimestamp(new Date());
		persistenceService.persistProjectSnapshot(snapshot1);
	}

	@Test
	public void shouldBeAbleToPersistFileRepresentations() throws Exception {
		final FileRepresentation fileRepresentation = new FileRepresentation(new UUID(), "fileName", "fileHash", new UUID());
		persistenceService.persistOrUpdateFileRepresentation(fileRepresentation);
	}

	@Test
	public void shouldBeAbleToRetrieveFileRepresentations() throws Exception {
		final FileRepresentation fileRepresentation = new FileRepresentation(new UUID(), "fileName", "fileHash", new UUID());
		persistenceService.persistOrUpdateFileRepresentation(fileRepresentation);

		final FileRepresentation retrievedFileRepresentation = persistenceService.retrieveFileRepresentationById(fileRepresentation.getId());
		DeepEqualityTestUtils.assertObjectEquality(fileRepresentation, retrievedFileRepresentation);
	}

	@Test
	public void shouldPersistAndRetrieveSingleUserNotification() throws Exception {
		final User user = createAndPersistUser();
		final Notification notification = getBuilder("msg").addReceipient(user).getNotification();

		persistenceService.persistOrUpdateNotification(notification);

		final List<Notification> latestNotificationsForUser = persistenceService.retrieveLatestNotificationsForUser(user, 50);
		assertEquals(1, latestNotificationsForUser.size());
		DeepEqualityTestUtils.assertObjectEquality(notification, latestNotificationsForUser.get(0));
	}

	private NotificationBuilder getBuilder(final String desc) throws PersistenceException {
		final ProjectRepresentation project = ProjectTestUtils.createRepresentation(new UUID(""));

		persistenceService.persistOrUpdateProjectRepresentation(project);

		return new NotificationBuilder(NotificationType.IMPEDIMENT, project, UserTestUtils.createUser(1))
				.setDescription(desc);
	}

	@Test
	public void shouldPersistAndRetrieveSingleUserNotificationWhenThereIsMoreNotifications() throws Exception {
		final User user1 = createAndPersistUser();
		final Notification notification1 = getBuilder("msg1").addReceipient(user1).getNotification();
		persistenceService.persistOrUpdateNotification(notification1);

		final User user2 = createAndPersistUser();
		final Notification notification2 = getBuilder("msg2").addReceipient(user2).getNotification();
		persistenceService.persistOrUpdateNotification(notification2);

		final List<Notification> latestNotificationsForUser = persistenceService.retrieveLatestNotificationsForUser(user1, 50);
		assertEquals(1, latestNotificationsForUser.size());
		DeepEqualityTestUtils.assertObjectEquality(notification1, latestNotificationsForUser.get(0));
	}

	@Test
	public void shouldPersistAndRetrieveMultipleUserNotifications() throws Exception {
		final User user1 = createAndPersistUser();
		final Notification notification1 = getBuilder("msg1").addReceipient(user1).getNotification();
		persistenceService.persistOrUpdateNotification(notification1);

		final User user2 = createAndPersistUser();
		final Notification notification2 = getBuilder("msg2").addReceipient(user2).getNotification();
		persistenceService.persistOrUpdateNotification(notification2);

		final User user3 = createAndPersistUser();
		final Notification notification3 = getBuilder("msg3").addReceipient(user3).addReceipient(user1)
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification3);

		final List<Notification> latestNotificationsForUser = persistenceService.retrieveLatestNotificationsForUser(user1, 50);
		assertEquals(2, latestNotificationsForUser.size());
		DeepEqualityTestUtils.assertObjectEquality(notification3, latestNotificationsForUser.get(0));
		DeepEqualityTestUtils.assertObjectEquality(notification1, latestNotificationsForUser.get(1));
	}

	@Test
	public void shouldPersistAndRetrieveMultipleUserNotificationsInTheCorrectOrder() throws Exception {
		final User user1 = createAndPersistUser();
		final Notification notification1 = getBuilder("msg1").addReceipient(user1).setTimestamp(new Date(1))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification1);

		final User user2 = createAndPersistUser();
		final Notification notification2 = getBuilder("msg2").addReceipient(user1).addReceipient(user2)
				.setTimestamp(new Date(1000))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification2);

		final User user3 = createAndPersistUser();
		final Notification notification3 = getBuilder("msg3").addReceipient(user3).addReceipient(user1)
				.setTimestamp(new Date(100))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification3);

		final List<Notification> latestNotificationsForUser = persistenceService.retrieveLatestNotificationsForUser(user1, 50);

		assertEquals(3, latestNotificationsForUser.size());
		DeepEqualityTestUtils.assertObjectEquality(notification2, latestNotificationsForUser.get(0));
		DeepEqualityTestUtils.assertObjectEquality(notification3, latestNotificationsForUser.get(1));
		DeepEqualityTestUtils.assertObjectEquality(notification1, latestNotificationsForUser.get(2));
	}

	@Test
	public void shouldPersistAndRetrieveMultipleUserNotificationsLimitedByMaxRequested() throws Exception {
		final User user1 = createAndPersistUser();
		final Notification notification1 = getBuilder("msg1").addReceipient(user1).setTimestamp(new Date(1))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification1);

		final User user2 = createAndPersistUser();
		final Notification notification2 = getBuilder("msg2").addReceipient(user1).addReceipient(user2)
				.setTimestamp(new Date(1000))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification2);

		final User user3 = createAndPersistUser();
		final Notification notification3 = getBuilder("msg3").addReceipient(user3).addReceipient(user1)
				.setTimestamp(new Date(100))
				.getNotification();
		persistenceService.persistOrUpdateNotification(notification3);

		final List<Notification> latestNotificationsForUser = persistenceService.retrieveLatestNotificationsForUser(user1, 2);

		assertEquals(2, latestNotificationsForUser.size());
		DeepEqualityTestUtils.assertObjectEquality(notification2, latestNotificationsForUser.get(0));
		DeepEqualityTestUtils.assertObjectEquality(notification3, latestNotificationsForUser.get(1));
	}

	private User createAndPersistUser() throws Exception {
		return persistenceService.persistOrUpdateUser(UserTestUtils.createUser());
	}

	private void authorize(final User user, final ProjectRepresentation... projects) throws PersistenceException {
		for (final ProjectRepresentation project : projects) {
			persistenceService.authorize(user.getEmail(), project.getId());
		}
	}

	private ProjectRepresentation createProjectRepresentation(final String projectName) throws PersistenceException {
		return persistenceService.persistOrUpdateProjectRepresentation(ProjectTestUtils.createRepresentation(projectName));
	}

	private ProjectSnapshot loadProjectSnapshot() throws PersistenceException, UnableToLoadProjectException, ProjectNotFoundException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot(PROJECT_ID);
		}
		catch (final NoResultFoundException e) {
			snapshot = createBlankProject();
		}
		return snapshot;
	}

	private ProjectSnapshot createBlankProject() throws UnableToLoadProjectException, ProjectNotFoundException {
		final Scope projectScope = ScopeTestUtils.createScope("Project", UUID.INVALID_UUID);
		final Release projectRelease = new Release("proj", new UUID("release0"));

		try {
			final ProjectSnapshot projectSnapshot = new ProjectSnapshot(ProjectTestUtils.createProject(
					persistenceService.retrieveProjectRepresentation(PROJECT_ID), projectScope,
					projectRelease), new Date(0));
			return projectSnapshot;
		}
		catch (final IOException e) {
			throw new UnableToLoadProjectException("It was not possible to create a blank project.");
		}
		catch (final PersistenceException e) {
			throw new UnableToLoadProjectException("It was not possible to create a blank project, because the project with id '" + PROJECT_ID
					+ "' was not found.");
		}
		catch (final NoResultFoundException e) {
			throw new ProjectNotFoundException("It was not possible to create a blank project, because the project representation with id '" + PROJECT_ID
					+ "' was not found.");
		}
	}

	private void assureProjectRepresentationExistance() throws Exception {
		persistenceService.persistOrUpdateProjectRepresentation(ProjectTestUtils.createRepresentation(PROJECT_ID));
	}
}
