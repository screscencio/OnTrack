package br.com.oncast.ontrack.server.services.persistence.jpa;

import static br.com.oncast.ontrack.utils.assertions.AssertTestUtils.assertCollectionEquality;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class PersistenceServiceTest {

	private static final int PROJECT_ID = 1;

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
		persistenceService.persistActions(PROJECT_ID, ActionTestUtils.getSomeActions(), new Date());

		final List<UserAction> userActions = persistenceService.retrieveActionsSince(PROJECT_ID, 0);

		final List<ModelAction> secondWaveOfActions = ActionTestUtils.getActions2();
		persistenceService.persistActions(PROJECT_ID, secondWaveOfActions, new Date());

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

		new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son").execute(new ProjectContext(project1));

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
		persistenceService.persistOrUpdateProjectRepresentation(new ProjectRepresentation("Name"));
		final ProjectRepresentation foundProjectRepresentation = persistenceService.retrieveProjectRepresentation(2);
		assertEquals("Name", foundProjectRepresentation.getName());
	}

	@Test
	public void thePersistedProjectRepresentationShouldHaveItsGeneratedIdSet() throws Exception {
		final ProjectRepresentation projectRepresentation = new ProjectRepresentation("Name");
		assertEquals(0, projectRepresentation.getId());

		persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation);
		assertEquals(2, projectRepresentation.getId());

		final ProjectRepresentation foundProjectRepresentation = persistenceService.retrieveProjectRepresentation(2);
		assertEquals("Name", foundProjectRepresentation.getName());
	}

	@Test
	public void shouldBeAbleToFindAllProjectRepresentations() throws Exception {
		assertEquals(1, persistenceService.retrieveAllProjectRepresentations().size());

		final ArrayList<ProjectRepresentation> projectRepresentations = new ArrayList<ProjectRepresentation>();
		for (int i = 2; i <= 11; i++) {
			final ProjectRepresentation projectRepresentation = new ProjectRepresentation(i, "Name" + i);
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

		assertCollectionEquality(Arrays.asList(project1, project3), extractProjectsFromAuthorization(authorizations));
	}

	private List<ProjectRepresentation> extractProjectsFromAuthorization(final List<ProjectAuthorization> authorizations) {
		final List<ProjectRepresentation> projects = new ArrayList<ProjectRepresentation>();
		for (final ProjectAuthorization authorization : authorizations) {
			projects.add(authorization.getProject());
		}
		return projects;
	}

	@Test(expected = PersistenceException.class)
	public void inexistentUserCannotBeAuthorized() throws Exception {
		persistenceService.authorize(new User("inexistent@email.com"), persistenceService.retrieveProjectRepresentation(PROJECT_ID));
	}

	@Test(expected = PersistenceException.class)
	public void inexistentProjectCannotBeAuthorized() throws Exception {
		final User user = createAndPersistUser();
		persistenceService.authorize(user, ProjectTestUtils.createProjectRepresentation(404, "inexistent project name"));
	}

	@Test
	public void cannotAuthorizeAUserToProjectTwice() throws Exception {
		final User user = createAndPersistUser();
		final ProjectRepresentation project = persistenceService.retrieveProjectRepresentation(PROJECT_ID);

		boolean reached = false;
		try {
			persistenceService.authorize(user, project);
			reached = true;
			persistenceService.authorize(user, project);
			fail();
		}
		catch (final PersistenceException e) {
			assertTrue(reached);
		}

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

	private User createAndPersistUser() throws PersistenceException {
		return persistenceService.persistOrUpdateUser(new User("email@provider.com"));
	}

	private void authorize(final User user, final ProjectRepresentation... projects) throws PersistenceException {
		for (final ProjectRepresentation project : projects) {
			persistenceService.authorize(user, project);
		}
	}

	private ProjectRepresentation createProjectRepresentation(final String projectName) throws PersistenceException {
		return persistenceService.persistOrUpdateProjectRepresentation(ProjectTestUtils.createProjectRepresentation(projectName));
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
		final Scope projectScope = new Scope("Project", new UUID("0"));
		final Release projectRelease = new Release("proj", new UUID("release0"));

		try {
			final ProjectSnapshot projectSnapshot = new ProjectSnapshot(new Project(persistenceService.retrieveProjectRepresentation(PROJECT_ID), projectScope,
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
