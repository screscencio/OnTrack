package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class XMLWriterTest {

	private static final String ONTRACK_XML = "ontrack.xml";
	private XMLWriter xmlExporter;
	private List<User> userList;
	private List<Password> passwordList;
	private String version;

	@Before
	public void setUp() {
		deleteGeneratedXMLFile();

		xmlExporter = new XMLWriter();
		userList = UserActionFactoryMock.createUserList();
		passwordList = UserActionFactoryMock.createPasswordList();
		version = "2011_10_01";
	}

	@After
	public void tearDown() {
		deleteGeneratedXMLFile();
	}

	public void deleteGeneratedXMLFile() {
		final File generatedXML = new File(ONTRACK_XML);
		if (generatedXML.exists()) generatedXML.delete();
	}

	@Test
	public void shouldWriteUsersAndPasswordToXML() throws Exception {
		testWithActionList(new ArrayList<UserAction>());
	}

	@Test
	public void shouldWriteUsersPasswordAndReleaseRemoveActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createReleaseRemoveAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndReleaseScopeUpdatePriorityActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createReleaseScopeUpdatePriorityAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndReleaseUpdatePriorityActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createReleaseUpdatePriorityAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeDeclareEffortActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeDeclareEffortAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeDeclareProgressActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeDeclareProgressAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeMoveUpActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeMoveUpAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeMoveDownActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeMoveDownAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeUpdateActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeUpdateAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndReleaseCreateActionDefaultToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createReleaseCreateActionDefault()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeBindReleaseActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeBindReleaseAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertChildRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertChildRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndReleaseRemoveRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createReleaseRemoveRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertParentRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertParentRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertSiblingDownRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertSiblingDownRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertSiblingUpRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertSiblingUpRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeRemoveActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeRemoveAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertChildActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertChildAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertParentActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertParentAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeRemoveRollbackActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeRemoveRollbackAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertSiblingDownActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertSiblingDownAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeInsertSiblingUpActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeInsertSiblingUpAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeMoveLeftActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeMoveLeftAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndScopeMoveRightActionToXML() throws Exception {
		testWithActionList(asList(UserActionFactoryMock.createScopeMoveRightAction()));
	}

	@Test
	public void shouldWriteUsersPasswordAndActionsToXML() throws Exception {
		testWithActionList(UserActionFactoryMock.createCompleteUserActionList());
	}

	@Test
	public void shouldWriteActionsSeparatedByProjectsToXML() throws Exception {
		final ArrayList<UserAction> actionList = new ArrayList<UserAction>();

		actionList.addAll(UserActionFactoryMock.createRandomUserActionList(1, "Project 1"));
		actionList.addAll(UserActionFactoryMock.createRandomUserActionList(2, "Project 2"));
		actionList.addAll(UserActionFactoryMock.createRandomUserActionList(3, "Project 3"));

		assertEquality(actionList, generateXMLAndRead(actionList));
	}

	@Test
	public void shouldWriteActionsToXMLInTheSameOrderTheyArePassed() throws Exception {
		final ArrayList<UserAction> actionList = new ArrayList<UserAction>();
		actionList.addAll(UserActionFactoryMock.createCompleteUserActionListOrderedById());

		final OntrackXML xml = generateXMLAndRead(actionList);

		final ProjectXMLNode project = xml.getProjects().get(0);
		for (int i = 0; i < project.getActions().size(); i++) {
			assertEquals(actionList.get(i).getTimestamp(), project.getActions().get(i).getTimestamp());
		}
	}

	private List<ProjectXMLNode> separateByProject(final List<UserAction> userActions) {
		final Map<ProjectRepresentation, List<UserAction>> map = new HashMap<ProjectRepresentation, List<UserAction>>();
		for (final UserAction userAction : userActions) {
			final ProjectRepresentation projectRepresentation = userAction.getProjectRepresentation();
			if (!map.containsKey(projectRepresentation)) map.put(projectRepresentation, new ArrayList<UserAction>());
			map.get(projectRepresentation).add(userAction);
		}
		final List<ProjectXMLNode> list = new ArrayList<ProjectXMLNode>();
		for (final ProjectRepresentation projectRepresentation : map.keySet()) {
			list.add(new ProjectXMLNode(projectRepresentation, map.get(projectRepresentation)));
		}
		return list;
	}

	private void testWithActionList(final List<UserAction> actionList) throws Exception {
		final OntrackXML ontrackXML = generateXMLAndRead(actionList);
		assertEquality(actionList, ontrackXML);
	}

	private void assertEquality(final List<UserAction> actionList, final OntrackXML ontrackXML) {
		assertEquals(version, ontrackXML.getVersion());
		final List<UserXMLNode> userXMLNodes = ontrackXML.getUsers();
		assertEquals(userList.size(), userXMLNodes.size());
		for (final UserXMLNode userXMLNode : userXMLNodes) {
			assertContainsUser(userXMLNode.getUser());
			assertContainsPassword(userXMLNode.getPassword());
		}

		final List<ProjectXMLNode> projects = separateByProject(actionList);
		DeepEqualityTestUtils.assertObjectEquality(projects, ontrackXML.getProjects());
	}

	private void assertContainsUser(final User user) {
		boolean contains = false;
		for (final User userFromList : userList) {
			if (userFromList.getEmail().equals(user.getEmail())) {
				contains = true;
				break;
			}
		}
		assertTrue(contains);
	}

	private void assertContainsPassword(final Password password) {
		boolean contains = false;
		for (final Password passwordFromList : passwordList) {
			if (passwordFromList.getPasswordHash().equals(password.getPasswordHash()) &&
					passwordFromList.getPasswordSalt().equals(password.getPasswordSalt())) {
				contains = true;
				break;
			}
		}
		assertTrue(contains);
	}

	private OntrackXML generateXMLAndRead(final List<UserAction> actionList) throws Exception {
		final File ontrackFile = new File(ONTRACK_XML);

		xmlExporter.setUserList(getAllUsersWithPassword())
				.setProjectList(separateByProject(actionList)).setVersion(version)
				.export(new FileOutputStream(ontrackFile));

		final Serializer serializer = new Persister();
		final File source = new File(ONTRACK_XML);

		final OntrackXML ontrackXML = serializer.read(OntrackXML.class, source);
		return ontrackXML;
	}

	private List<UserXMLNode> getAllUsersWithPassword() {
		final List<UserXMLNode> userXMLNodeList = new ArrayList<UserXMLNode>();

		try {
			final List<User> users = userList;
			for (final User user : users) {
				userXMLNodeList.add(associatePasswordTo(user));
			}
		}
		catch (final PersistenceException e) {
			e.printStackTrace();
		}

		return userXMLNodeList;
	}

	private UserXMLNode associatePasswordTo(final User user) throws PersistenceException {
		final UserXMLNode userXMLNode = new UserXMLNode(user);
		userXMLNode.setPassword(getPasswordFor(user));
		return userXMLNode;
	}

	private Password getPasswordFor(final User user) {
		for (final Password password : passwordList) {
			if (password.getUserId() == user.getId()) return password;
		}
		return null;
	}

	private List<UserAction> asList(final UserAction userAction) throws Exception {
		final List<UserAction> userActions = new ArrayList<UserAction>();
		userActions.add(userAction);
		return userActions;
	}
}
