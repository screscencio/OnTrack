package br.com.oncast.ontrack.server.services.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMail;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AuthorizationManagerTest {

	private static final int PROJECT_ID = 1;
	private EntityManager entityManager;

	private PersistenceService persistence;
	private AuthenticationManager authenticationManager;
	private ProjectAuthorizationMailFactory mailFactory;
	private User authenticatedUser;
	private User admin;

	@Before
	public void setUp() throws Exception {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();

		configureMockDefaultBehavior();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	private void configureMockDefaultBehavior() throws Exception {
		authenticationManager = mock(AuthenticationManager.class);
		persistence = mock(PersistenceService.class);
		mailFactory = mock(ProjectAuthorizationMailFactory.class);

		admin = UserTestUtils.createUser(DefaultAuthenticationCredentials.USER_EMAIL);
		authenticatedUser = UserTestUtils.createUser(100);
		authenticatedUser.setProjectInvitationQuota(1);

		configureToRetrieveAdmin();
		authorizeUser(authenticatedUser, PROJECT_ID);
		configureToRetrieveSnapshot(PROJECT_ID);
	}

	private void configureToRetrieveAdmin() throws NoResultFoundException, PersistenceException, UserNotFoundException {
		when(persistence.retrieveUserByEmail(DefaultAuthenticationCredentials.USER_EMAIL)).thenReturn(admin);
		when(authenticationManager.findUserByEmail(authenticatedUser.getEmail())).thenReturn(authenticatedUser);
		when(authenticationManager.isUserAuthenticated()).thenReturn(true);
	}

	private void configureToRetrieveSnapshot(final int projectId) throws Exception {
		final ProjectSnapshot snapshot = mock(ProjectSnapshot.class);
		when(persistence.retrieveProjectSnapshot(projectId)).thenReturn(snapshot);
		when(snapshot.getProject()).thenReturn(ProjectTestUtils.createProject());
	}

	private void authorizeUser(final User user, final long projectId) throws PersistenceException, NoResultFoundException {
		when(authenticationManager.getAuthenticatedUser()).thenReturn(user);
		final ProjectAuthorization authorization = mock(ProjectAuthorization.class);
		when(persistence.retrieveUserByEmail(user.getEmail())).thenReturn(user);
		when(persistence.retrieveProjectAuthorization(user.getId(), projectId)).thenReturn(authorization);
	}

	@Test
	public void shouldBeAbleToAuthorizeAnExistentUser() throws Exception {
		final String mail = "user@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenReturn(UserTestUtils.createUser(mail));
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, false);
		verify(persistence).authorize(mail, PROJECT_ID);
	}

	@Test
	public void authorizeUserShouldCreateTheUserIfTheGivenUserDoesNotExist() throws Exception {
		final String mail = "inexistent@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenThrow(new UserNotFoundException());
		when(authenticationManager.createNewUser(mail, "", 0, 0)).thenReturn(new User(mail));

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, false);

		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(authenticationManager).createNewUser(captor.capture(), eq(""), eq(0), eq(0));

		assertEquals(mail, captor.getValue());
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void shouldFailToAuthorizeUserThatIsAlreadyAuthorized() throws Exception {
		final String mail = "user@mail.com";
		final User user = UserTestUtils.createUser(mail);

		when(authenticationManager.findUserByEmail(mail)).thenReturn(user);
		when(persistence.retrieveProjectAuthorization(user.getId(), PROJECT_ID)).thenReturn(new ProjectAuthorization(user, null));

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, false);
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void shouldNotAuthorizeUserInvitationWhenQuotaExceeded() throws Exception {
		final String mail = "user@mail.com";

		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(PROJECT_ID)).thenReturn(ProjectTestUtils.createRepresentation());
		authenticatedUser.setProjectInvitationQuota(0);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, true);
	}

	@Test
	public void authorizingUserShouldSendMailToUserWhenRequested() throws Exception {
		final String mail = "user@mail.com";

		final ProjectAuthorizationMail mockMail = mock(ProjectAuthorizationMail.class);
		when(mockMail.setProject(Mockito.<ProjectRepresentation> anyObject())).thenReturn(mockMail);
		when(mockMail.currentUser(Mockito.anyString())).thenReturn(mockMail);
		when(mailFactory.createMail()).thenReturn(mockMail);
		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(PROJECT_ID)).thenReturn(ProjectTestUtils.createRepresentation());

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, true);

		verify(mockMail).sendTo(mail, false);
	}

	@Test
	public void authorizingUserShouldNotSendMailToUserWhenNoUserIsAuthenticatedEvenWhenRequested() throws Exception {
		final String mail = "user@mail.com";

		final ProjectAuthorizationMail mockMail = mock(ProjectAuthorizationMail.class);
		when(mockMail.setProject(Mockito.<ProjectRepresentation> anyObject())).thenReturn(mockMail);
		when(mockMail.currentUser(Mockito.anyString())).thenReturn(mockMail);
		when(mailFactory.createMail()).thenReturn(mockMail);
		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(PROJECT_ID)).thenReturn(ProjectTestUtils.createRepresentation());
		when(authenticationManager.isUserAuthenticated()).thenReturn(false);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, true);

		verify(mockMail, times(0)).sendTo(mail, false);
	}

	@Test
	public void authorizeShouldDecreaseInvitationQuotaOfUserThatRequestedTheOperation() throws Exception {
		final String mail = "user@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenReturn(UserTestUtils.createUser(mail));

		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(PROJECT_ID, mail, false);
		Assert.assertEquals(0, authenticatedUser.getProjectInvitationQuota());
		verify(persistence).persistOrUpdateUser(authenticatedUser);
	}

	@Test(expected = AuthorizationException.class)
	public void assureProjectAccessAuthorizationShouldFailWhenUserIsNotAuthorized() throws Exception {
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), PROJECT_ID)).thenReturn(null);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).assureProjectAccessAuthorization(PROJECT_ID);
		verify(persistence).retrieveProjectAuthorization(authenticatedUser.getId(), PROJECT_ID);
	}

	@Test
	public void assureProjectAccessAuthorizationShouldSucceedWhenUserIsAuthorized() throws Exception {
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), PROJECT_ID)).thenReturn(new ProjectAuthorization(authenticatedUser, null));
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).assureProjectAccessAuthorization(PROJECT_ID);
		verify(persistence).retrieveProjectAuthorization(authenticatedUser.getId(), PROJECT_ID);
	}

	@Test
	public void listAuthorizedProjectsReturnsOnlyAuthorizedProjects01() throws Exception {

		final List<ProjectAuthorization> authorizations = ProjectTestUtils.createAuthorizations(3, authenticatedUser);
		when(persistence.retrieveProjectAuthorizations(authenticatedUser.getId())).thenReturn(authorizations);

		final List<ProjectRepresentation> projects = AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory)
				.listAuthorizedProjects(
						authenticatedUser);

		assertEquals(projects.size(), authorizations.size());
		for (final ProjectAuthorization auth : authorizations) {
			assertTrue(projects.contains(auth.getProject()));
		}

		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void listAuthorizedProjectsReturnsOnlyAuthorizedProjects02() throws Exception {

		ProjectTestUtils.createAuthorizations(3, UserTestUtils.createUser());
		when(persistence.retrieveProjectAuthorizations(authenticatedUser.getId())).thenReturn(new ArrayList<ProjectAuthorization>());

		final List<ProjectRepresentation> projects = AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory)
				.listAuthorizedProjects(
						authenticatedUser);

		assertEquals(0, projects.size());

		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void authorizeAdminShouldCreateNewAuthorizationWithAdminCredentials() throws Exception {
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorizeAdmin(new ProjectRepresentation(PROJECT_ID, ""));
		verify(persistence).authorize(DefaultAuthenticationCredentials.USER_EMAIL, PROJECT_ID);
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldSucceedIfUserHasNoQuotaButIsAuthorizingItself() throws UnableToAuthorizeUserException,
			PersistenceException {
		final String mail = authenticatedUser.getEmail();
		authenticatedUser.setProjectInvitationQuota(0);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail,
				authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldSucceedIfUserHasQuota() throws UnableToAuthorizeUserException,
			PersistenceException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(1);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail,
				authenticatedUser);
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void validateAndUpdateUserUserInvitaionQuotaShouldThrowExceptionIfUserHasNoQuota() throws UnableToAuthorizeUserException, PersistenceException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(0);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail,
				authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldUpdateQuota() throws UnableToAuthorizeUserException,
			PersistenceException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(2);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail,
				authenticatedUser);
		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldNotifyUserInformationChangeWhenSucceed() throws PersistenceException,
			UnableToAuthorizeUserException {
		final String mail = "user@mail.com";
		final NotificationService notificationService = mock(NotificationService.class);

		authenticatedUser.setProjectInvitationQuota(1);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory, notificationService)
				.validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser);
		verify(notificationService).notifyUserInformationChange(authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldNotNotifyUserInformationChangeWhenFailed() {
		final NotificationService notificationService = mock(NotificationService.class);
		final String mail = "user@mail.com";

		authenticatedUser.setProjectInvitationQuota(0);
		try {
			AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory, notificationService)
					.validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser);
		}
		catch (final Exception e) {}
		verify(notificationService, times(0)).notifyUserInformationChange(authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldSucceedIfUserHasQuota() throws PersistenceException, AuthorizationException {
		authenticatedUser.setProjectCreationQuota(1);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
	}

	@Test(expected = AuthorizationException.class)
	public void validateAndUpdateUserProjectCreationQuotaShouldThrowExceptionIfUserHasNoQuota() throws AuthorizationException, PersistenceException {
		authenticatedUser.setProjectCreationQuota(0);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldUpdateQuota() throws AuthorizationException, PersistenceException {
		authenticatedUser.setProjectCreationQuota(2);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		Assert.assertEquals(1, authenticatedUser.getProjectCreationQuota());
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldNotifyUserInformationChangeWhenSucceed() throws PersistenceException, AuthorizationException {
		final NotificationService notificationService = mock(NotificationService.class);
		authenticatedUser.setProjectCreationQuota(1);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory, notificationService)
				.validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		verify(notificationService).notifyUserInformationChange(authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldNotNotifyUserInformationChangeWhenFailed() throws PersistenceException, AuthorizationException {
		final NotificationService notificationService = mock(NotificationService.class);
		authenticatedUser.setProjectCreationQuota(0);
		try {
			AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory, notificationService)
					.validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		}
		catch (final Exception e) {}
		verify(notificationService, times(0)).notifyUserInformationChange(authenticatedUser);
	}

}
