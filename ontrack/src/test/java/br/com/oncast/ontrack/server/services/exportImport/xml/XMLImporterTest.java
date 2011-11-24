package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
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
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class XMLImporterTest {

	@Mock
	private PersistenceService persistenceService;
	@Mock
	private OntrackXML ontrackXML;
	private List<ProjectXMLNode> projects;
	private List<UserXMLNode> users;
	private XMLImporter importer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		configureImporter();

		projects = new ArrayList<ProjectXMLNode>();
		users = new ArrayList<UserXMLNode>();

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
	}

	private void configureImporter() throws Exception {
		importer = new XMLImporter(persistenceService);
		ReflectionTestUtils.set(importer, "ontrackXML", ontrackXML);
	}

	@Test
	public void shouldPersistProjectRepresentations() throws Exception {
		final ProjectRepresentation projectRepresentation1 = createProjectWithActions(1).getProjectRepresentation();
		final ProjectRepresentation projectRepresentation2 = createProjectWithActions(2).getProjectRepresentation();

		importer.persistObjects();

		assertProjectsWerePersisted(projectRepresentation1, projectRepresentation2);
	}

	@Test
	public void shouldPersistProjectRepresentationsBeforePersistingUserActions() throws Exception {
		createProjectWithActions(1);

		importer.persistObjects();

		final InOrder inOrder = inOrder(persistenceService);
		inOrder.verify(persistenceService).persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class));
		inOrder.verify(persistenceService, Mockito.atLeast(1)).persistActions(Mockito.anyLong(), Mockito.anyListOf(ModelAction.class), Mockito.any(Date.class));
	}

	@Test
	public void shouldPersistUserActionsWithRelatedProjectRepresentationIdAsProjectId() throws Exception {
		final int projectId = 1;
		final ProjectXMLNode projectXMLNode = createProjectWithActions(projectId);

		importer.persistObjects();
		assertActionsWerePersistedRelatedToThisProject(projectXMLNode.getActions(), projectId);
	}

	@Test
	public void shouldPersistUsersBeforePersistingPasswords() throws Exception {
		final User user = createUserWithPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(Mockito.any(User.class))).thenReturn(user);

		importer.persistObjects();

		final InOrder inOrder = inOrder(persistenceService);
		inOrder.verify(persistenceService).persistOrUpdateUser(Mockito.any(User.class));
		inOrder.verify(persistenceService).persistOrUpdatePassword(Mockito.any(Password.class));
	}

	@Test
	public void shouldPersistPasswordsWithRelatedUserId() throws Exception {
		final User user = createUserWithPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(Mockito.any(User.class))).thenReturn(user);

		importer.persistObjects();

		final ArgumentCaptor<Password> argument = ArgumentCaptor.forClass(Password.class);
		verify(persistenceService).persistOrUpdatePassword(argument.capture());

		assertEquals(user.getId(), argument.getValue().getUserId());
	}

	@Test
	public void shouldNotPersistPasswordForAUserIdWhenItDoesntHaveAPassword() throws Exception {
		final User user = createUserWithoutPassword();

		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenThrow(new NoResultFoundException("", null));
		when(persistenceService.persistOrUpdateUser(Mockito.any(User.class))).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, times(0)).persistOrUpdatePassword(Mockito.any(Password.class));
	}

	@Test
	public void shouldNotPersistAUserWhenThereIsAPersistedUserWithSameEmail() throws Exception {
		final User user = createUserWithoutPassword();
		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, times(0)).persistOrUpdateUser(Mockito.any(User.class));
	}

	@Test
	public void shouldNotUpdateUserPasswordWhenThereIsAPersistedUserWithSameEmail() throws Exception {
		final User user = createUserWithoutPassword();
		when(persistenceService.retrieveUserByEmail(user.getEmail())).thenReturn(user);

		importer.persistObjects();

		verify(persistenceService, times(0)).persistOrUpdatePassword(Mockito.any(Password.class));
	}

	private ProjectXMLNode createProjectWithActions(final long projectId) throws Exception, PersistenceException {
		final ProjectRepresentation projectRepresentation = ProjectTestUtils.createProjectRepresentation(projectId);
		final List<UserAction> actions = UserActionFactoryMock.createRandomUserActionList();

		final ProjectXMLNode projectXMLNode = new ProjectXMLNode(projectRepresentation, actions);
		projects.add(projectXMLNode);

		when(persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation)).thenReturn(projectRepresentation);

		return projectXMLNode;
	}

	private User createUserWithoutPassword() throws Exception {
		final User user = new User("email1");
		ReflectionTestUtils.set(user, "id", 1L);
		final UserXMLNode node = new UserXMLNode(user);

		users.add(node);
		return user;
	}

	private User createUserWithPassword() throws Exception {
		final User user = new User("email1");
		ReflectionTestUtils.set(user, "id", 1L);

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

	private void assertActionsWerePersistedRelatedToThisProject(final List<UserAction> actions, final long projectId)
			throws PersistenceException {
		final ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
		verify(persistenceService, times(actions.size())).persistActions(argument.capture(), Mockito.anyListOf(ModelAction.class),
				Mockito.any(Date.class));
		assertEquals(argument.getValue(), projectId, 0);
	}

}
