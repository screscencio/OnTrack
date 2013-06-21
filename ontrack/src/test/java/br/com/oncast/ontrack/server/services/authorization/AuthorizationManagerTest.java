package br.com.oncast.ontrack.server.services.authorization;

import static br.com.oncast.ontrack.server.services.authorization.AuthorizationManagerImplTestUtils.create;
import static br.com.oncast.ontrack.utils.model.ProjectTestUtils.createRepresentation;
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
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.authentication.UserInformationChangeEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectRemovedEvent;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class AuthorizationManagerTest {

	private EntityManager entityManager;

	private PersistenceService persistence;
	private AuthenticationManager authenticationManager;
	private MailFactory mailFactory;
	private MulticastService multicastService;
	private User authenticatedUser;
	private User admin;
	private ProjectRepresentation project;
	private UUID projectId;

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
		mailFactory = mock(MailFactory.class);
		multicastService = mock(MulticastService.class);

		admin = persist(UserTestUtils.getAdmin());
		authenticatedUser = createUser();
		authenticatedUser.setProjectInvitationQuota(1);
		project = createProjectRepresentation();
		projectId = project.getId();

		configureToRetrieveAdmin();
		authorizeUser(authenticatedUser, project.getId());
		configureToRetrieveSnapshot(project);
	}

	@Test
	public void shouldBeAbleToAuthorizeAnExistentUser() throws Exception {
		final String mail = createUser().getEmail();

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, false);
		verify(persistence).authorize(mail, projectId);
	}

	@Test
	public void authorizeUserShouldCreateTheUserIfTheGivenUserDoesNotExist() throws Exception {
		final String mail = "inexistent@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenThrow(new UserNotFoundException());
		when(authenticationManager.createNewUser(eq(mail), Mockito.anyString(), eq(0), eq(0))).thenReturn(UserTestUtils.createUser(mail));

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, false);

		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(authenticationManager).createNewUser(captor.capture(), Mockito.anyString(), eq(0), eq(0));

		assertEquals(mail, captor.getValue());
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void shouldFailToAuthorizeUserThatIsAlreadyAuthorized() throws Exception {
		final User user = createUser();
		authorizeUser(user, projectId);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, user.getEmail(), false);
	}

	@Test
	public void shouldAuthorizeAnExistingUserInvitationEvenWhenQuotaExceeded() throws Exception {
		final String mail = "user@mail.com";

		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(ProjectTestUtils.createRepresentation());
		authenticatedUser.setProjectInvitationQuota(0);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, false);
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void shouldNotAuthorizeNewUserInvitationWhenQuotaExceeded() throws Exception {
		final String mail = "user@mail.com";

		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenThrow(new UserNotFoundException());
		when(authenticationManager.createNewUser(eq(mail), Mockito.anyString(), eq(0), eq(0))).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(ProjectTestUtils.createRepresentation());
		authenticatedUser.setProjectInvitationQuota(0);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, true);
	}

	@Test
	public void authorizingUserShouldSendMailToUserWhenRequested() throws Exception {
		final String mail = "user@mail.com";

		final ProjectAuthorizationMail mockMail = mock(ProjectAuthorizationMail.class);
		when(mockMail.setProject(Mockito.<ProjectRepresentation> anyObject())).thenReturn(mockMail);
		when(mockMail.currentUser(Mockito.anyString())).thenReturn(mockMail);
		when(mailFactory.createProjectAuthorizationMail()).thenReturn(mockMail);
		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(ProjectTestUtils.createRepresentation());

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, true);

		verify(mockMail).sendTo(mail, null);
	}

	@Test
	public void authorizingUserShouldSendMailAsAdminWhenNoUserIsAuthenticatedEvenWhenRequested() throws Exception {
		final String mail = "user@mail.com";

		final ProjectAuthorizationMail mockMail = mock(ProjectAuthorizationMail.class);
		when(mockMail.setProject(Mockito.any(ProjectRepresentation.class))).thenReturn(mockMail);
		when(mockMail.currentUser(Mockito.anyString())).thenReturn(mockMail);
		when(mailFactory.createProjectAuthorizationMail()).thenReturn(mockMail);
		final User requestUser = UserTestUtils.createUser(mail);
		when(authenticationManager.findUserByEmail(mail)).thenReturn(requestUser);
		when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(ProjectTestUtils.createRepresentation(projectId));
		when(authenticationManager.isUserAuthenticated()).thenReturn(false);

		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, true);

		verify(mockMail).currentUser(admin.getEmail());
		verify(mockMail).sendTo(mail, null);
	}

	@Test
	public void authorizeShouldDecreaseInvitationQuotaOfUserThatRequestedTheOperationWhenTheInviteeIsANewUser() throws Exception {
		final String mail = "user@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenThrow(new UserNotFoundException());
		when(authenticationManager.createNewUser(eq(mail), Mockito.anyString(), eq(0), eq(0))).thenReturn(UserTestUtils.createUser(mail));

		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, false);
		Assert.assertEquals(0, authenticatedUser.getProjectInvitationQuota());
		verify(persistence).persistOrUpdateUser(authenticatedUser);
	}

	@Test
	public void authorizeShouldNotDecreaseInvitationQuotaOfUserThatRequestedTheOperationWhenTheInviteeIsAnExistingUser() throws Exception {
		final String mail = "user@mail.com";

		when(authenticationManager.findUserByEmail(mail)).thenReturn(UserTestUtils.createUser(mail));

		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).authorize(projectId, mail, false);
		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
	}

	@Test(expected = AuthorizationException.class)
	public void assureProjectAccessAuthorizationShouldFailWhenUserIsNotAuthorized() throws Exception {
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), projectId)).thenReturn(null);
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).assureProjectAccessAuthorization(projectId);
		verify(persistence).retrieveProjectAuthorization(authenticatedUser.getId(), projectId);
	}

	@Test
	public void assureProjectAccessAuthorizationShouldSucceedWhenUserIsAuthorized() throws Exception {
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), projectId)).thenReturn(
				new ProjectAuthorization(authenticatedUser.getId(), new UUID()));
		AuthorizationManagerImplTestUtils.create(persistence, authenticationManager, mailFactory).assureProjectAccessAuthorization(projectId);
		verify(persistence).retrieveProjectAuthorization(authenticatedUser.getId(), projectId);
	}

	@Test
	public void listAuthorizedProjectsReturnsOnlyAuthorizedProjects01() throws Exception {

		final List<ProjectAuthorization> authorizations = ProjectTestUtils.createAuthorizations(3, authenticatedUser);
		for (final ProjectAuthorization authorization : authorizations) {
			final UUID projectId = authorization.getProjectId();
			when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(createRepresentation(projectId));
		}

		when(persistence.retrieveProjectAuthorizations(authenticatedUser.getId())).thenReturn(authorizations);

		final List<ProjectRepresentation> projects = create(persistence, authenticationManager, mailFactory)
				.listAuthorizedProjects(authenticatedUser.getId());

		assertEquals(projects.size(), authorizations.size());
		for (final ProjectAuthorization auth : authorizations) {
			assertTrue(projects.contains(createRepresentation(auth.getProjectId())));
		}

		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void listAuthorizedProjectsReturnsOnlyAuthorizedProjects02() throws Exception {

		ProjectTestUtils.createAuthorizations(3, createUser());
		when(persistence.retrieveProjectAuthorizations(authenticatedUser.getId())).thenReturn(new ArrayList<ProjectAuthorization>());

		final List<ProjectRepresentation> projects = create(persistence, authenticationManager, mailFactory)
				.listAuthorizedProjects(authenticatedUser.getId());

		assertEquals(0, projects.size());

		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void authorizeAdminShouldCreateNewAuthorizationWithAdminCredentials() throws Exception {
		create(persistence, authenticationManager, mailFactory).authorizeAdmin(project);
		verify(persistence).authorize(DefaultAuthenticationCredentials.USER_EMAIL, projectId);
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldSucceedIfUserHasNoQuotaButIsAuthorizingItself() throws UnableToAuthorizeUserException,
			PersistenceException, NoResultFoundException {
		final String mail = authenticatedUser.getEmail();
		authenticatedUser.setProjectInvitationQuota(0);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldSucceedIfUserHasQuota() throws UnableToAuthorizeUserException,
			PersistenceException, NoResultFoundException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(1);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
	}

	@Test(expected = UnableToAuthorizeUserException.class)
	public void validateAndUpdateUserUserInvitaionQuotaShouldThrowExceptionIfUserHasNoQuota() throws UnableToAuthorizeUserException, PersistenceException,
			NoResultFoundException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(0);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldUpdateQuota() throws UnableToAuthorizeUserException,
			PersistenceException, NoResultFoundException {
		final String mail = "user@mail.com";
		authenticatedUser.setProjectInvitationQuota(2);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
		Assert.assertEquals(1, authenticatedUser.getProjectInvitationQuota());
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldNotifyUserInformationChangeWhenSucceed() throws PersistenceException,
			UnableToAuthorizeUserException, NoResultFoundException {
		final String mail = "user@mail.com";

		authenticatedUser.setProjectInvitationQuota(1);

		final ArgumentCaptor<UserInformationChangeEvent> captor = ArgumentCaptor.forClass(UserInformationChangeEvent.class);
		create(persistence, authenticationManager, mailFactory, multicastService).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
		verify(multicastService).multicastToUser(captor.capture(), eq(authenticatedUser));
		assertEquals(authenticatedUser.getId(), captor.getValue().getUserId());
	}

	@Test
	public void validateAndUpdateUserUserInvitaionQuotaShouldNotNotifyUserInformationChangeWhenFailed() {
		final String mail = "user@mail.com";

		authenticatedUser.setProjectInvitationQuota(0);
		try {
			create(persistence, authenticationManager, mailFactory, multicastService).validateAndUpdateUserUserInvitaionQuota(mail, authenticatedUser.getId());
		}
		catch (final Exception e) {}
		verify(multicastService, times(0)).multicastToUser(new UserInformationChangeEvent(authenticatedUser), authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldSucceedIfUserHasQuota() throws PersistenceException, AuthorizationException {
		authenticatedUser.setProjectCreationQuota(1);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
	}

	@Test(expected = AuthorizationException.class)
	public void validateAndUpdateUserProjectCreationQuotaShouldThrowExceptionIfUserHasNoQuota() throws AuthorizationException, PersistenceException {
		authenticatedUser.setProjectCreationQuota(0);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldUpdateQuota() throws AuthorizationException, PersistenceException {
		authenticatedUser.setProjectCreationQuota(2);
		create(persistence, authenticationManager, mailFactory).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		Assert.assertEquals(1, authenticatedUser.getProjectCreationQuota());
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldNotifyUserInformationChangeWhenSucceed() throws PersistenceException, AuthorizationException {
		authenticatedUser.setProjectCreationQuota(1);
		final ArgumentCaptor<UserInformationChangeEvent> captor = ArgumentCaptor.forClass(UserInformationChangeEvent.class);
		create(persistence, authenticationManager, mailFactory, multicastService).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		verify(multicastService).multicastToUser(captor.capture(), eq(authenticatedUser));
		assertEquals(authenticatedUser.getProjectCreationQuota(), captor.getValue().getProjectCreationQuota());
	}

	@Test
	public void validateAndUpdateUserProjectCreationQuotaShouldNotNotifyUserInformationChangeWhenFailed() throws PersistenceException, AuthorizationException {
		authenticatedUser.setProjectCreationQuota(0);
		try {
			create(persistence, authenticationManager, mailFactory, multicastService).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		}
		catch (final Exception e) {}
		verify(multicastService, times(0)).multicastToUser(new UserInformationChangeEvent(authenticatedUser), authenticatedUser);
	}

	@Test
	public void shouldRemoveTheAuthorizationFromPersistenceWhenRequested() throws Exception {
		final AuthorizationManager manager = create(persistence, authenticationManager, mailFactory, multicastService);
		manager.removeAuthorization(projectId, authenticatedUser.getId());
		final ArgumentCaptor<ProjectAuthorization> captor = ArgumentCaptor.forClass(ProjectAuthorization.class);
		verify(persistence).remove(captor.capture());

		final ProjectAuthorization authorization = captor.getValue();
		assertEquals(projectId, authorization.getProjectId());
		assertEquals(authenticatedUser.getId(), authorization.getUserId());
	}

	@Test(expected = UnableToRemoveAuthorizationException.class)
	public void shouldNotBeAbleToRemoveAuthorizationWhenThereIsNoAuthorization() throws Exception {
		final AuthorizationManager manager = create(persistence, authenticationManager, mailFactory, multicastService);
		when(persistence.retrieveProjectRepresentation(Mockito.any(UUID.class))).thenReturn(ProjectTestUtils.createRepresentation());

		manager.removeAuthorization(admin.getId(), new UUID());
	}

	@Test
	public void authorizeShouldNotifyAProjectAddition() throws Exception {
		final User user = createUser();
		final ProjectRepresentation project = createProjectRepresentation();

		final AuthorizationManager manager = create(persistence, authenticationManager, mailFactory, multicastService);
		manager.authorize(project.getId(), user.getEmail(), false);

		final ArgumentCaptor<ProjectAddedEvent> captor = ArgumentCaptor.forClass(ProjectAddedEvent.class);
		verify(multicastService, times(1)).multicastToUser(captor.capture(), eq(user));

		final ProjectAddedEvent createdProject = captor.getValue();
		assertEquals(project, createdProject.getProjectRepresentation());
	}

	@Test
	public void removeAuthorizationShouldNotifyAProjectRemotion() throws Exception {
		final User user = createUser();
		final ProjectRepresentation project = createProjectRepresentation();
		authorizeUser(user, project.getId());

		final AuthorizationManager manager = create(persistence, authenticationManager, mailFactory, multicastService);
		manager.removeAuthorization(project.getId(), user.getId());

		final ArgumentCaptor<ProjectRemovedEvent> captor = ArgumentCaptor.forClass(ProjectRemovedEvent.class);
		verify(multicastService, times(1)).multicastToUser(captor.capture(), eq(user));

		final ProjectRemovedEvent createdProject = captor.getValue();
		assertEquals(project, createdProject.getProjectRepresentation());
	}

	private ProjectRepresentation createProjectRepresentation() throws PersistenceException, NoResultFoundException {
		final ProjectRepresentation project = ProjectTestUtils.createRepresentation();
		when(persistence.retrieveProjectRepresentation(project.getId())).thenReturn(project);
		return project;
	}

	private User createUser() throws Exception {
		return persist(UserTestUtils.createUser());
	}

	private User persist(final User user) throws Exception {
		when(persistence.retrieveUserByEmail(user.getEmail())).thenReturn(user);
		when(persistence.retrieveUserById(user.getId())).thenReturn(user);
		when(authenticationManager.findUserByEmail(user.getEmail())).thenReturn(user);
		return user;
	}

	private void configureToRetrieveAdmin() throws NoResultFoundException, PersistenceException, UserNotFoundException {
		when(authenticationManager.findUserByEmail(authenticatedUser.getEmail())).thenReturn(authenticatedUser);
		when(authenticationManager.isUserAuthenticated()).thenReturn(true);
	}

	private void configureToRetrieveSnapshot(final ProjectRepresentation project) throws Exception {
		final ProjectSnapshot snapshot = mock(ProjectSnapshot.class);
		when(persistence.retrieveProjectSnapshot(project.getId())).thenReturn(snapshot);
		when(snapshot.getProject()).thenReturn(ProjectTestUtils.createProject(project));
	}

	private void authorizeUser(final User user, final UUID projectId) throws PersistenceException, NoResultFoundException {
		when(authenticationManager.getAuthenticatedUser()).thenReturn(user);
		final ProjectAuthorization authorization = ProjectTestUtils.createAuthorization(user, projectId);
		when(persistence.retrieveProjectAuthorization(user.getId(), projectId)).thenReturn(authorization);
	}
}
