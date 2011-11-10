package br.com.oncast.ontrack.server.services.exportImport.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class XMLWriterTest {

	private static final String ONTRACK_XML = "ontrack.xml";
	private XMLWriter xmlExporter;
	private List<User> userList;
	private List<Password> passwordList;
	private long version;

	@Before
	public void setUp() {
		xmlExporter = new XMLWriter();
		userList = UserActionFactoryMock.createUserList();
		passwordList = UserActionFactoryMock.createPasswordList();
		version = new Date().getTime();
	}

	@After
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

	private void testWithActionList(final List<UserAction> actionList) throws Exception {
		final OntrackXML ontrackXML = generateXMLAndRead(actionList);
		assertEquality(actionList, ontrackXML);
	}

	private void assertEquality(final List<UserAction> actionList, final OntrackXML ontrackXML) {
		assertTrue(ontrackXML.getUsers().containsAll(userList));
		assertTrue(ontrackXML.getPasswords().containsAll(passwordList));
		assertEquals(version, ontrackXML.getVersion());
		DeepEqualityTestUtils.assertObjectEquality(actionList, ontrackXML.getUserActions());
	}

	private OntrackXML generateXMLAndRead(final List<UserAction> actionList) throws Exception {
		final File ontrackFile = new File(ONTRACK_XML);

		xmlExporter.setUserList(userList).setPasswordList(passwordList).setActionList(actionList).setVersion(version)
				.export(new FileOutputStream(ontrackFile));

		final Serializer serializer = new Persister();
		final File source = new File(ONTRACK_XML);

		final OntrackXML ontrackXML = serializer.read(OntrackXML.class, source);
		return ontrackXML;
	}

	private List<UserAction> asList(final UserAction userAction) throws Exception {
		final List<UserAction> userActions = new ArrayList<UserAction>();
		userActions.add(userAction);
		return userActions;
	}
}
