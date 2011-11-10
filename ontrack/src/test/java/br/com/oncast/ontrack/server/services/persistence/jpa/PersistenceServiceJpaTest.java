package br.com.oncast.ontrack.server.services.persistence.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class PersistenceServiceJpaTest {

	private PersistenceServiceJpaImpl persistenceService;

	private EntityManager entityManager;

	@Before
	public void before() {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		persistenceService = new PersistenceServiceJpaImpl();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void shouldOnlyReturnActionsAfterAGivenId() throws Exception {
		for (final ModelAction action : ActionMock.getActions()) {
			final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
			actionList.add(action);
			persistenceService.persistActions(actionList, new Date());
		}

		final List<UserAction> userActions = persistenceService.retrieveActionsSince(0);

		final List<ModelAction> secondWaveOfActions = ActionMock.getActions2();
		for (final ModelAction action : secondWaveOfActions) {
			final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
			actionList.add(action);
			persistenceService.persistActions(actionList, new Date());
		}

		final List<UserAction> actionsReceived = persistenceService.retrieveActionsSince(userActions.get(userActions.size() - 1).getId());
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

		final User newUser = persistenceService.findUserByEmail(email);
		assertEquals(user.getEmail(), newUser.getEmail());
	}

	@Test
	public void shouldUpdateUser() throws PersistenceException, NoResultFoundException {
		final User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		final User newUser = persistenceService.findUserByEmail(email);
		assertEquals(user.getEmail(), newUser.getEmail());

		final String newEmail = "newEmail@email.com";
		newUser.setEmail(newEmail);
		persistenceService.persistOrUpdateUser(newUser);
		final User updatedUser = persistenceService.findUserByEmail(newEmail);
		assertEquals(newEmail, updatedUser.getEmail());
		assertEquals(newUser.getId(), updatedUser.getId());
	}

	@Test
	public void shouldPersistPasswordForAnExistentUser() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.findUserByEmail(email);
		final Password password = new Password();
		password.setUserId(user.getId());
		final String passwordText = "password";
		password.setPassword(passwordText);
		persistenceService.persistOrUpdatePassword(password);
		final Password newPassword = persistenceService.findPasswordForUser(user.getId());
		assertTrue(newPassword.authenticate(passwordText));
	}

	@Test
	public void shouldUpdateAndExistentPassword() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.findUserByEmail(email);
		final Password password = new Password();
		password.setUserId(user.getId());
		final String passwordText = "password";
		password.setPassword(passwordText);
		persistenceService.persistOrUpdatePassword(password);
		final Password firstPassword = persistenceService.findPasswordForUser(user.getId());
		assertTrue(firstPassword.authenticate(passwordText));

		final String newPassword = "newPassword";
		firstPassword.setPassword(newPassword);
		persistenceService.persistOrUpdatePassword(firstPassword);

		final Password secondPassword = persistenceService.findPasswordForUser(user.getId());
		assertFalse(secondPassword.authenticate(passwordText));
		assertTrue(secondPassword.authenticate(newPassword));
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNoResultFoundExceptionWhenUserDontHaveAPassword() throws PersistenceException, NoResultFoundException {
		User user = new User();
		final String email = "user1@email.com";
		user.setEmail(email);
		persistenceService.persistOrUpdateUser(user);

		user = persistenceService.findUserByEmail(email);
		persistenceService.findPasswordForUser(user.getId());
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNoResultFoundExceptionWhenUserNotExist() throws PersistenceException, NoResultFoundException {
		persistenceService.findPasswordForUser(213);
	}

	@Test(expected = NoResultFoundException.class)
	public void shouldThrowNotResultFoundExceptionWhenUserNotFound() throws NoResultFoundException, PersistenceException {
		persistenceService.findUserByEmail("inexistant@email.com");
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

		final List<User> userList = persistenceService.findAllUsers();
		assertEquals(4, userList.size());

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

	private ProjectSnapshot loadProjectSnapshot() throws PersistenceException, UnableToLoadProjectException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot();
		}
		catch (final NoResultFoundException e) {
			snapshot = createBlankProject();
		}
		return snapshot;
	}

	private ProjectSnapshot createBlankProject() throws UnableToLoadProjectException {
		final Scope projectScope = new Scope("Project", new UUID("0"));
		final Release projectRelease = new Release("proj", new UUID("release0"));

		try {
			return new ProjectSnapshot(new Project(projectScope, projectRelease), new Date(0));
		}
		catch (final IOException e) {
			throw new UnableToLoadProjectException("It was not possible to create a blank project.");
		}
	}

}
