package br.com.oncast.ontrack.server.services.exportImport.xml;

import static br.com.oncast.ontrack.utils.mocks.models.UserTestUtils.createUser;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class XMLImporterTest {

	private static final long USER_ID = 0;
	@Mock
	private PersistenceService persistenceService;
	@Mock
	private OntrackXML ontrackXML;
	private List<ProjectXMLNode> projects;
	private List<UserXMLNode> users;
	private List<ProjectAuthorizationXMLNode> projectAuthorizations;

	private XMLImporter importer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		configureImporter();

		projects = new ArrayList<ProjectXMLNode>();
		users = new ArrayList<UserXMLNode>();
		projectAuthorizations = new ArrayList<ProjectAuthorizationXMLNode>();

		when(persistenceService.retrieveUserByEmail(DefaultAuthenticationCredentials.USER_EMAIL)).thenReturn(UserTestUtils.getAdmin());

		when(ontrackXML.getProjects()).thenAnswer(new Answer<List<ProjectXMLNode>>() {
			@Override
			public List<ProjectXMLNode> answer(final InvocationOnMock invocation) throws Throwable {
				return projects;
			}
		});
		when(ontrackXML.getUsers()).thenAnswer(new Answer<List<UserXMLNode>>() {
			@Override
			public List<UserXMLNode> answer(final InvocationOnMock invocation) throws Throwable {
				return users;
			}
		});
		when(ontrackXML.getProjectAuthorizations()).thenAnswer(new Answer<List<ProjectAuthorizationXMLNode>>() {

			@Override
			public List<ProjectAuthorizationXMLNode> answer(final InvocationOnMock invocation) throws Throwable {
				return projectAuthorizations;
			}
		});
	}

	private void configureImporter() throws Exception {
		importer = new XMLImporter(persistenceService);
		ReflectionTestUtils.set(importer, "ontrackXML", ontrackXML);
	}

	@Test
	public void shouldPersistProjectRepresentations() throws Exception {
		final ProjectRepresentation projectRepresentation1 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();
		final ProjectRepresentation projectRepresentation2 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();

		importer.persistObjects();

		assertProjectsWerePersisted(projectRepresentation1, projectRepresentation2);
	}

	@Test
	public void shouldPersistProjectRepresentationsBeforePersistingUserActions() throws Exception {
		addProjectWithActionsAndMockPersistedProject(new UUID());

		importer.persistObjects();

		final InOrder inOrder = inOrder(persistenceService);
		inOrder.verify(persistenceService).persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class));
		inOrder.verify(persistenceService, atLeast(1)).persistActions(any(UUID.class), anyListOf(ModelAction.class), anyLong(), any(Date.class));
	}

	@Test
	public void shouldPersistUserActionsWithRelatedProjectRepresentationIdAsProjectId() throws Exception {
		final UUID projectId = new UUID();
		final ProjectXMLNode projectXMLNode = addProjectWithActionsAndMockPersistedProject(projectId);

		importer.persistObjects();
		assertActionsWerePersistedRelatedToThisProject(projectXMLNode.getActions(), projectId);
	}

	@Test
	public void shouldPersistUsersBeforePersistingPasswords() throws Exception {
		final User user = addUserWithPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(user);

		importer.persistObjects();

		final InOrder inOrder = inOrder(persistenceService);
		inOrder.verify(persistenceService).persistOrUpdateUser(any(User.class));
		inOrder.verify(persistenceService).persistOrUpdatePassword(any(Password.class));
	}

	@Test
	public void shouldPersistPasswordsWithRelatedUserId() throws Exception {
		final User user = addUserWithPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(user);

		importer.persistObjects();

		final ArgumentCaptor<Password> argument = ArgumentCaptor.forClass(Password.class);
		verify(persistenceService).persistOrUpdatePassword(argument.capture());

		assertEquals(user.getId(), argument.getValue().getUserId());
	}

	@Test
	public void shouldNotPersistPasswordForAUserIdWhenItDoesntHaveAPassword() throws Exception {
		final User user = addUserWithoutPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, never()).persistOrUpdatePassword(any(Password.class));
	}

	@Test
	public void shouldNotPersistAUserWhenThereIsAPersistedUserWithSameEmail() throws Exception {
		final User user = addUserWithoutPassword();
		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, never()).persistOrUpdateUser(any(User.class));
	}

	@Test
	public void shouldNotUpdateUserPasswordWhenThereIsAPersistedUserWithSameEmail() throws Exception {
		final User user = addUserWithoutPassword();
		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, never()).persistOrUpdatePassword(any(Password.class));
	}

	@Test
	public void shouldPersistProjectAuthorizationsWithRelatedUserAndProjectRepresentation() throws Exception {
		final User user1 = addUserWithoutPassword(1, "user1");
		final User user2 = addUserWithoutPassword(2, "user2");
		final User user3 = addUserWithoutPassword(3, "user3");

		when(persistenceService.retrieveUserByEmail("user1")).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.retrieveUserByEmail("user2")).thenReturn(user2);
		when(persistenceService.retrieveUserByEmail("user3")).thenThrow(new NoResultFoundException("", null));

		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(user1, user3);

		final ProjectRepresentation project1 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();
		final ProjectRepresentation project2 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();

		addProjectAuthorization(user1, project1);
		addProjectAuthorization(user2, project2);

		importer.persistObjects();

		verify(persistenceService).authorize(user1.getEmail(), project1.getId());
		verify(persistenceService).authorize(user2.getEmail(), project2.getId());
		verify(persistenceService, never()).authorize(eq(user3.getEmail()), any(UUID.class));
	}

	@Test
	public void xmlUserIdIsOnlyUsedForMapInsideXMLAndShouldNotBeUsedForPersisting() throws Exception {
		final User user1 = addUserWithoutPassword(1, "user1");
		final User user2 = addUserWithoutPassword(2, "user2");

		final ProjectRepresentation project1 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();
		final ProjectRepresentation project2 = addProjectWithActionsAndMockPersistedProject(new UUID()).getProjectRepresentation();

		addProjectAuthorization(user1, project1);
		addProjectAuthorization(user2, project2);

		final User persistedUser1 = UserTestUtils.createUser(4, "user1");
		final User persistedUser2 = UserTestUtils.createUser(5, "user2");
		final User persistedUser3 = UserTestUtils.createUser(6, "user3");

		when(persistenceService.retrieveUserByEmail("user1")).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.retrieveUserByEmail("user2")).thenReturn(persistedUser2);
		when(persistenceService.retrieveUserByEmail("user3")).thenThrow(new NoResultFoundException("", null));

		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(persistedUser1, persistedUser3);

		importer.persistObjects();

		verify(persistenceService).authorize(persistedUser1.getEmail(), project1.getId());
		verify(persistenceService).authorize(persistedUser2.getEmail(), project2.getId());
		verify(persistenceService, never()).authorize(eq(persistedUser3.getEmail()), any(UUID.class));
	}

	@Test
	public void shouldKeepTheXmlProjectIdInPersistence() throws Exception {
		final User user1 = addUserWithoutPassword(1, "user1");
		final ProjectRepresentation project1 = addProjectWithActions(new UUID()).getProjectRepresentation();
		addProjectAuthorization(user1, project1);

		when(persistenceService.retrieveUserByEmail(Mockito.anyString())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(user1);

		importer.persistObjects();

		verify(persistenceService).persistOrUpdateProjectRepresentation(eq(project1));
		verify(persistenceService).authorize(user1.getEmail(), project1.getId());
	}

	@Test
	public void userIdShouldBeUpdatedWithPersistedOnesWhenImportingActions() throws Exception {
		final long persistedUserId = 3;
		final UUID projectId = new UUID();

		final long exportedUserId = 112233;
		addUserWithoutPassword(exportedUserId, "user1");
		final ProjectXMLNode node = addProjectWithActions(projectId, exportedUserId);

		when(persistenceService.retrieveUserByEmail(Mockito.anyString())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(any(User.class))).thenReturn(UserTestUtils.createUser(persistedUserId));
		when(persistenceService.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenReturn(
				ProjectTestUtils.createRepresentation(projectId));

		importer.persistObjects();

		verify(persistenceService, times(node.getActions().size())).persistActions(eq(projectId), anyListOf(ModelAction.class), eq(persistedUserId),
				any(Date.class));
	}

	private void addProjectAuthorization(final User user, final ProjectRepresentation project) {
		projectAuthorizations.add(new ProjectAuthorizationXMLNode(new ProjectAuthorization(user, project)));
	}

	private ProjectXMLNode addProjectWithActionsAndMockPersistedProject(final UUID projectId) throws Exception, PersistenceException {
		final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation(projectId);
		final List<UserAction> actions = UserActionTestUtils.createRandomUserActionList(projectId, USER_ID);

		final ProjectXMLNode projectXMLNode = new ProjectXMLNode(projectRepresentation, actions);
		projects.add(projectXMLNode);

		when(persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation)).thenReturn(projectRepresentation);
		return projectXMLNode;
	}

	private ProjectXMLNode addProjectWithActions(final UUID projectId) throws Exception, PersistenceException {
		return addProjectWithActions(projectId, 15);
	}

	private ProjectXMLNode addProjectWithActions(final UUID projectId, final long userId) throws Exception, PersistenceException {
		final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation(projectId);
		final List<UserAction> actions = UserActionTestUtils.createRandomUserActionList(projectId, userId);

		final ProjectXMLNode projectXMLNode = new ProjectXMLNode(projectRepresentation, actions);
		projects.add(projectXMLNode);

		return projectXMLNode;
	}

	private User addUserWithoutPassword(final long id, final String email) throws Exception {
		final User user = createUser(id, email);
		final UserXMLNode node = new UserXMLNode(user);

		users.add(node);
		return user;
	}

	private User addUserWithoutPassword() throws Exception {
		final User user = createUser(1L, "email1");
		final UserXMLNode node = new UserXMLNode(user);

		users.add(node);
		return user;
	}

	private User addUserWithPassword() throws Exception {
		final User user = createUser(1L, "email1");

		final Password password = new Password();
		password.setPasswordHash("hash");
		password.setPasswordSalt("salt");

		final UserXMLNode node = new UserXMLNode(user);
		node.setPassword(password);

		users.add(node);
		return user;
	}

	private void assertProjectsWerePersisted(final ProjectRepresentation... projectRepresentations) throws PersistenceException {
		for (final ProjectRepresentation projectRepresentation : projectRepresentations) {
			verify(persistenceService, times(1)).persistOrUpdateProjectRepresentation(projectRepresentation);
		}
	}

	private void assertActionsWerePersistedRelatedToThisProject(final List<UserAction> actions, final UUID projectId)
			throws PersistenceException {
		final ArgumentCaptor<UUID> argument = ArgumentCaptor.forClass(UUID.class);
		verify(persistenceService, times(actions.size())).persistActions(argument.capture(), anyListOf(ModelAction.class), anyLong(),
				any(Date.class));
		assertEquals(argument.getValue(), projectId);
	}

}
